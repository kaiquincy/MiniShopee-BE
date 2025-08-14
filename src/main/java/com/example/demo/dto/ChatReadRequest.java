package com.example.demo.dto;

import lombok.Data;

@Data
public class ChatReadRequest {
    private Long roomId;
    private Long lastMessageId; // đánh dấu đã đọc tới id này
}
