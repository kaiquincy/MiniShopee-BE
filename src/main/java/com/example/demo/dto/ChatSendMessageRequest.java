package com.example.demo.dto;

import com.example.demo.enums.MessageType;
import lombok.Data;

@Data
public class ChatSendMessageRequest {
    private Long roomId;
    private MessageType type = MessageType.TEXT;
    private String content;
}
