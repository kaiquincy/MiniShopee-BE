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
    private String userBAvatarUrl;
    private ChatMessageResponse lastMsg;

    // GIỮ NGUYÊN constructor cũ
    public ChatRoomResponse(ChatRoom cr) {
        this.roomId = cr.getId();
        this.userAId = cr.getUserA().getUsername();
        this.userBId = cr.getUserB().getUsername();
        this.userAFullName = cr.getUserA().getFullName();
        this.userBFullName = cr.getUserB().getFullName();
        this.userBAvatarUrl = cr.getUserB().getAvatarUrl();
    }

    // ✅ factory mới: đảm bảo userB luôn là peer
    public ChatRoomResponse normalizeFor(Long currentUserId, ChatRoom cr) {
        var a = cr.getUserA();
        var b = cr.getUserB();

        boolean currentIsA = a.getId().equals(currentUserId);
        var current = currentIsA ? a : b;
        var peer = currentIsA ? b : a;

        this.userAId = current.getUsername();
        this.userAFullName = current.getFullName();

        this.userBId = peer.getUsername();
        this.userBFullName = peer.getFullName();
        this.userBAvatarUrl = peer.getAvatarUrl();

        return this;
    }

}