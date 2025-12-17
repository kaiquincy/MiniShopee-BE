package com.example.demo.service;

import com.example.demo.dto.ChatMessageResponse;
import com.example.demo.dto.ChatRoomResponse;
import com.example.demo.dto.ChatSendMessageRequest;
import com.example.demo.enums.MessageType;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatRoom;
import com.example.demo.model.User;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;
    private final UserService userService;
    private final SimpMessagingTemplate messaging;

    Logger logger = org.slf4j.LoggerFactory.getLogger(ChatService.class);

    /** Tạo/lấy phòng 2 người (userA.id < userB.id) — REST, dùng SecurityContext */
    @Transactional
    public ChatRoomResponse getOrCreateDirectRoom(Long peerUserId) {
        Long me = userService.getCurrentUserId();
        if (me.equals(peerUserId)) {
            throw new AppException(ErrorCode.SELF_CHAT_DENIED);
        }

        Long a = Math.min(me, peerUserId);
        Long b = Math.max(me, peerUserId);

        ChatRoom room = roomRepo.findDirectBetween(a, b).orElseGet(() -> {
            User userA = userRepo.findById(a).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            User userB = userRepo.findById(b).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            return roomRepo.save(ChatRoom.builder().userA(userA).userB(userB).build());
        });
        return new ChatRoomResponse(room);
    }

    /** Danh sách phòng của current user — REST */
    public List<ChatRoomResponse> myRooms() {
        Long me = userService.getCurrentUserId();
        List<ChatRoom> rooms = roomRepo.findAllOfUser(me);
        // ChatMessage lastMsgAlias = msgRepo.findTopByRoomIdOrderByCreatedAtDesc(room.getId())
        //                                  .orElse(null);
        return rooms.stream().map(room -> {
            ChatRoomResponse dto = new ChatRoomResponse(room).normalizeFor(me, room);

            // Lấy tin nhắn cuối cùng của room
            ChatMessage lastMsgAlias = msgRepo.findTopByRoomIdOrderByCreatedAtDesc(room.getId())
                                            .orElse(null);

            if (lastMsgAlias != null) {
                dto.setLastMsg(new ChatMessageResponse(lastMsgAlias));
            }

            return dto;
        }).toList();
    }

    /** Lịch sử tin nhắn (desc theo thời gian) — REST */
    public Page<ChatMessageResponse> history(Long roomId, int page, int size) {
        assertMember(roomId, userService.getCurrentUserId());
        Page<ChatMessage> p = msgRepo.findByRoomIdOrderByCreatedAtDesc(roomId, PageRequest.of(page, size));
        return p.map(ChatMessageResponse::new);
    }

    /**
     * Gửi tin nhắn (qua WS + lưu DB)
     * LƯU Ý: WebSocket lấy user từ Principal (username do interceptor set),
     * KHÔNG dùng SecurityContext ở đây để tránh NPE.
     */
    @Transactional
    public ChatMessageResponse send(ChatSendMessageRequest req, Principal principal) {
        logger.info("ChatService.send called");

        if (principal == null || principal.getName() == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Không xác định được người dùng (WS Principal null)");
        }

        // principal.getName() = username (đã set trong WebSocketJwtChannelInterceptor)
        String username = principal.getName();
        User sender = userRepo.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "User không tồn tại: " + username));
        Long me = sender.getId();

        ChatRoom room = roomRepo.findById(req.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Room không tồn tại"));

        // Kiểm tra thành viên phòng theo userId của sender
        assertMember(room.getId(), me);

        logger.info("User {} gửi tin nhắn {} tới room {}", username, req.getType() ,room.getId());

        ChatMessage m = ChatMessage.builder()
                .room(room)
                .sender(sender)
                .type(req.getType() != null ? req.getType() : MessageType.TEXT)
                .content(req.getContent())
                .build();

        ChatMessage saved = msgRepo.save(m);
        ChatMessageResponse out = new ChatMessageResponse(saved);

        // Broadcast tới những client đang sub phòng này
        messaging.convertAndSend("/topic/rooms/" + room.getId(), out);

        // Notify riêng cho đối phương qua /user/queue/notify
        User peer = peerUser(room, me);
        if (peer != null && peer.getUsername() != null) {
            // convertAndSendToUser dùng Principal.getName() của client (username)
            messaging.convertAndSendToUser(peer.getUsername(), "/queue/notify", out);
        }

        return out;
    }

    /** Đánh dấu đã đọc đến messageId — REST */
    @Transactional
    public void markRead(Long roomId, Long lastMessageId) {
        Long me = userService.getCurrentUserId();
        assertMember(roomId, me);
        Page<ChatMessage> first = msgRepo.findByRoomIdOrderByCreatedAtDesc(roomId, PageRequest.of(0, 100));
        LocalDateTime now = LocalDateTime.now();
        for (ChatMessage m : first) {
            if (m.getId() <= lastMessageId && !m.getSender().getId().equals(me) && m.getReadAt() == null) {
                m.setReadAt(now);
                msgRepo.save(m);
            }
        }
        // bắn read-receipt
        messaging.convertAndSend("/topic/rooms/" + roomId, "READ:" + me + ":" + lastMessageId);
    }

    // ---------- helpers ----------
    private void assertMember(Long roomId, Long uid) {
        ChatRoom cr = roomRepo.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Room không tồn tại"));
        Long a = cr.getUserA().getId(), b = cr.getUserB().getId();
        if (!uid.equals(a) && !uid.equals(b)) {
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không thuộc phòng chat này");
        }
    }

    private Long peerOf(ChatRoom cr, Long me) {
        return cr.getUserA().getId().equals(me) ? cr.getUserB().getId() : cr.getUserA().getId();
    }

    private User peerUser(ChatRoom cr, Long me) {
        return cr.getUserA().getId().equals(me) ? cr.getUserB() : cr.getUserA();
    }
}
