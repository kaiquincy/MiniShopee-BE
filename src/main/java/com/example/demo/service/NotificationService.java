package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepo;
    private final UserService userService;

    /** Tạo mới thông báo */
    @Transactional
    public NotificationResponse createNotification(NotificationRequest req) {
        User user = userService.findById(req.getUserId())
            .orElseThrow(() -> new AppException(
                ErrorCode.USER_NOT_EXISTED));
        Notification n = Notification.builder()
            .user(user)
            .message(req.getMessage())
            .type(req.getType())
            .referenceId(req.getReferenceId())
            .read(false)
            .build();
        Notification saved = notificationRepo.save(n);
        return new NotificationResponse(saved);
    }

    /** Lấy toàn bộ thông báo của user hiện tại */
    public List<NotificationResponse> getNotificationsForCurrentUser() {
        Long uid = userService.getCurrentUserId();
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(uid)
            .stream()
            .map(NotificationResponse::new)
            .collect(Collectors.toList());
    }

    /** Đánh dấu 1 thông báo đã đọc */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepo.findById(notificationId)
            .orElseThrow(() -> new AppException(
                ErrorCode.NOTIFICATION_NOT_FOUND));
        Long uid = userService.getCurrentUserId();
        if (!n.getUser().getId().equals(uid)) {
            throw new AppException(
                ErrorCode.ACCESS_DENIED);
        }
        n.setRead(true);
        notificationRepo.save(n);
    }

    /** Đếm số thông báo chưa đọc của user hiện tại */
    public long countUnreadForCurrentUser() {
        return notificationRepo.countByUserIdAndReadFalse(userService.getCurrentUserId());
    }
}
