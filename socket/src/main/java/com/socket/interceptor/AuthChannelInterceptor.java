package com.socket.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.socket.utils.JwtUtil;

import io.jsonwebtoken.JwtException;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try{
                    jwtUtil.validateToken(token);
                }catch(Exception e){
                    throw new MessagingException("Invalid JWT Token: " + e.getMessage());
                }

                Integer userId = jwtUtil.extractUserId(token);

                // 여기서 Principal 설정
                accessor.setUser(() -> userId.toString());
            } else {
                throw new IllegalArgumentException("Missing Authorization Header");
            }
        }

        return message;
    }
}

