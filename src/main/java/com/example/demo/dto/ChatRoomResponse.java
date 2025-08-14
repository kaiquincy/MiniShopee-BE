package com.example.demo.dto;

import com.example.demo.model.ChatRoom;
import lombok.Data;

@Data
public class ChatRoomResponse {
    private Long roomId;
    private Long userAId;
    private Long userBId;

    public ChatRoomResponse(ChatRoom cr) {
        this.roomId = cr.getId();
        this.userAId = cr.getUserA().getId();
        this.userBId = cr.getUserB().getId();
    }
}
