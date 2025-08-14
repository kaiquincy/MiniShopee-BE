package com.example.demo.dto;

import com.example.demo.model.ChatMessage;
import com.example.demo.enums.MessageType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private Long id;
    private Long roomId;
    private Long senderId;
    private MessageType type;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public ChatMessageResponse(ChatMessage m) {
        this.id = m.getId();
        this.roomId = m.getRoom().getId();
        this.senderId = m.getSender().getId();
        this.type = m.getType();
        this.content = m.getContent();
        this.createdAt = m.getCreatedAt();
        this.readAt = m.getReadAt();
    }
}
