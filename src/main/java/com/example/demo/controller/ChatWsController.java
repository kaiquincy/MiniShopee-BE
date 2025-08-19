package com.example.demo.controller;

import com.example.demo.dto.ChatMessageResponse;
import com.example.demo.dto.ChatSendMessageRequest;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWsController {
    private final ChatService chatService;

    /** Client gửi tới /app/chat.send */
    @MessageMapping("/chat.send")
    public void send(ChatSendMessageRequest req, Principal principal) {
        // Không trả @SendTo vì ta push chủ động bằng SimpMessagingTemplate
        ChatMessageResponse out = chatService.send(req, principal);
        // done
    }
}
