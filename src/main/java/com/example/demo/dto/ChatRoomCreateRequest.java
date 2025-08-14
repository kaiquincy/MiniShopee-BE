package com.example.demo.dto;

import lombok.Data;

@Data
public class ChatRoomCreateRequest {
    private Long peerUserId; // người cần chat cùng
}
