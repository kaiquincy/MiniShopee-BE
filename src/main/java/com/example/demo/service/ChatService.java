package com.example.demo.service;

import com.example.demo.dto.ChatMessageResponse;
import com.example.demo.dto.ChatRoomResponse;
import com.example.demo.dto.ChatSendMessageRequest;
import com.example.demo.enums.MessageType;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.*;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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

    /** Tạo/lấy phòng 2 người (userA.id < userB.id) */
    @Transactional
    public ChatRoomResponse getOrCreateDirectRoom(Long peerUserId) {
        Long me = userService.getCurrentUserId();
        if (me.equals(peerUserId)) throw new AppException(ErrorCode.PASSWORD_INVALID, "Không thể chat với chính mình");

        Long a = Math.min(me, peerUserId);
        Long b = Math.max(me, peerUserId);

        ChatRoom room = roomRepo.findDirectBetween(a, b).orElseGet(() -> {
            User userA = userRepo.findById(a).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            User userB = userRepo.findById(b).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            return roomRepo.save(ChatRoom.builder().userA(userA).userB(userB).build());
        });
        return new ChatRoomResponse(room);
    }

    /** Danh sách phòng của current user */
    public List<ChatRoomResponse> myRooms() {
        Long me = userService.getCurrentUserId();
        return roomRepo.findAllOfUser(me).stream().map(ChatRoomResponse::new).toList();
    }

    /** Lịch sử tin nhắn (desc theo thời gian) */
    public Page<ChatMessageResponse> history(Long roomId, int page, int size) {
        assertMember(roomId, userService.getCurrentUserId());
        Page<ChatMessage> p = msgRepo.findByRoomIdOrderByCreatedAtDesc(roomId, PageRequest.of(page, size));
        return p.map(ChatMessageResponse::new);
    }

    /** Gửi tin nhắn (qua WS + lưu DB) */
    @Transactional
    public ChatMessageResponse send(ChatSendMessageRequest req, Principal principal) {
        Long me = userService.getCurrentUserId();
        ChatRoom room = roomRepo.findById(req.getRoomId())
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Room không tồn tại"));
        assertMember(room.getId(), me);

        User sender = userRepo.findById(me).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
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

        // Đẩy thêm vào queue riêng của đối phương (thông báo)
        Long peerId = peerOf(room, me);
        messaging.convertAndSendToUser(String.valueOf(peerId), "/queue/notify", out);

        return out;
    }

    /** Đánh dấu đã đọc đến messageId (đơn giản: tất cả msg của peer chưa có readAt -> set now) */
    @Transactional
    public void markRead(Long roomId, Long lastMessageId) {
        Long me = userService.getCurrentUserId();
        assertMember(roomId, me);
        // Ở mức tối giản: load page đầu rồi set readAt cho các bản ghi cần thiết
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
}
