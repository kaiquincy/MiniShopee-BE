package com.example.demo.config;

import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils; // bạn đã có class này trong Auth
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(acc.getCommand())) {
            // Lấy token từ native header "Authorization": "Bearer xxx"
            String auth = acc.getFirstNativeHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                String username = jwtUtils.getUsernameFromToken(token);
                if (username != null && jwtUtils.validateToken(token)) {
                    UserDetails ud = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken user =
                            new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                    acc.setUser(user);
                }
            }
        }
        return message;
    }
}
