package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.demo.security.JwtUtils;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils; // service verify token của bạn

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        StompCommand cmd = accessor.getCommand();
        if (cmd == null) return message;

        switch (cmd) {
            case CONNECT -> {
                // 1) Ưu tiên lấy từ STOMP native header "Authorization"
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                String token = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }

                // 2) Dự phòng: lấy token từ handshake attributes (nếu bạn gắn ở HandshakeInterceptor)
                if (token == null) {
                    Object attrsObj = accessor.getHeader("simpSessionAttributes");
                    if (attrsObj instanceof Map<?, ?> attrs) {
                        Object t = attrs.get("token");
                        if (t instanceof String s && !s.isBlank()) token = s;
                    }
                }

                if (token == null) {
                    // tuỳ policy: cho ẩn danh hay chặn
                    throw new MessagingException("Missing Authorization token on STOMP CONNECT");
                }

                // validate & build Principal
                if (!jwtUtils.validateToken(token)) {
                    throw new MessagingException("Invalid JWT");
                }

                String userIdOrName = jwtUtils.getUsernameFromToken(token); // hoặc getUsername()
                // authorities tuỳ bạn
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userIdOrName, null, Collections.emptyList());

                // Gắn principal cho phiên STOMP
                accessor.setUser(authentication);

                // (Khuyến nghị) set vào SecurityContext cho thread hiện tại
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
            }
            case DISCONNECT -> {
                // dọn context khi phiên đóng
                SecurityContextHolder.clearContext();
            }
            default -> { /* ignore */ }
        }

        return message;
    }
}
