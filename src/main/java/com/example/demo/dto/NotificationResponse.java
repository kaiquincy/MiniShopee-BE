package com.example.demo.dto;

import com.example.demo.model.Notification;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String imgUrl;
    private String type;
    private Long referenceId;
    private Boolean read;
    private LocalDateTime createdAt;

    /** Map trực tiếp từ entity */
    public NotificationResponse(Notification n) {
        this.id = n.getId();
        this.userId = n.getUser().getId();
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.imgUrl = n.getImgUrl();
        this.type = n.getType();
        this.referenceId = n.getReferenceId();
        this.read = n.getRead();
        this.createdAt = n.getCreatedAt();
    }
}
