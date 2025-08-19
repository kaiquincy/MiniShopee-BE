package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;

    /** Tạo/lấy phòng direct với peer */
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> openRoom(@RequestBody ChatRoomCreateRequest req) {
        ChatRoomResponse room = chatService.getOrCreateDirectRoom(req.getPeerUserId());
        ApiResponse<ChatRoomResponse> resp = new ApiResponse<>();
        resp.setResult(room);
        return ResponseEntity.ok(resp);
    }

    /** Danh sách phòng của current user */
    @GetMapping("/rooms")
    public ApiResponse<List<ChatRoomResponse>> myRooms() {
        List<ChatRoomResponse> rooms = chatService.myRooms();
        ApiResponse<List<ChatRoomResponse>> resp = new ApiResponse<>();
        resp.setResult(rooms);
        return resp;
    }

    /** Lịch sử tin nhắn */
    @GetMapping("/rooms/{roomId}/messages")
    public ApiResponse<Page<ChatMessageResponse>> history(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessageResponse> h = chatService.history(roomId, page, size);
        ApiResponse<Page<ChatMessageResponse>> resp = new ApiResponse<>();
        resp.setResult(h);
        return resp;
    }

    /** Đánh dấu đã đọc */
    @PostMapping("/rooms/{roomId}/read")
    public ApiResponse<String> markRead(
            @PathVariable Long roomId,
            @RequestBody ChatReadRequest req) {
        chatService.markRead(roomId, req.getLastMessageId());
        ApiResponse<String> resp = new ApiResponse<>();
        resp.setResult("OK");
        return resp;
    }
}
