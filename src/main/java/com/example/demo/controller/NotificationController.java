package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /** Tạo thông báo mới */
    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> create(
            @RequestBody NotificationRequest req) {
        NotificationResponse nr = notificationService.createNotification(req);
        ApiResponse<NotificationResponse> resp = new ApiResponse<>();
        resp.setResult(nr);
        resp.setMessage("Tạo notification thành công");
        return ResponseEntity.ok(resp);
    }

    /** Lấy danh sách thông báo của user hiện tại */
    @GetMapping
    public ApiResponse<List<NotificationResponse>> list() {
        List<NotificationResponse> list = notificationService.getNotificationsForCurrentUser();
        ApiResponse<List<NotificationResponse>> resp = new ApiResponse<>();
        resp.setResult(list);
        return resp;
    }

    /** Đánh dấu 1 thông báo là đã đọc */
    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        ApiResponse<String> resp = new ApiResponse<>();
        resp.setResult("Marked as read");
        return ResponseEntity.ok(resp);
    }

    /** Lấy số lượng thông báo chưa đọc */
    @GetMapping("/unread-count")
    public ApiResponse<Long> unreadCount() {
        long cnt = notificationService.countUnreadForCurrentUser();
        ApiResponse<Long> resp = new ApiResponse<>();
        resp.setResult(cnt);
        return resp;
    }
}
