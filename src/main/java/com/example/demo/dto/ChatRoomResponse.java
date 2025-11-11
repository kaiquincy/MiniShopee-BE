package com.example.demo.dto;

import com.example.demo.model.ChatRoom;
import lombok.Data;

@Data
public class ChatRoomResponse {
    private Long roomId;
    private String userAId;
    private String userBId;
    private String userAFullName;
    private String userBFullName;
    private ChatMessageResponse lastMsg;

    // Constructor
    public ChatRoomResponse(ChatRoom cr) {
        this.roomId = cr.getId();
        this.userAId = cr.getUserA().getUsername();
        this.userBId = cr.getUserB().getUsername();
        this.userAFullName = cr.getUserA().getFullName();
        this.userBFullName = cr.getUserB().getFullName();
    }

    // Getter and Setter for lastMsg
    public void setLastMsg(ChatMessageResponse lastMsg) {
        this.lastMsg = lastMsg;
    }

}
