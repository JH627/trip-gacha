package com.socket.config;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                    WebSocketHandler wsHandler,
                                                    Map<String, Object> attributes) {
                        String sessionId = UUID.randomUUID().toString(); // 혹은 HttpSession ID를 가져올 수도 있음
                        return () -> sessionId;
                    }
                })
                .withSockJS(); // Vue에서 SockJS로 연결 시 필요
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // Vue에서 보낼 경로 prefix
        registry.enableSimpleBroker("/topic", "/queue"); // Vue에서 받을 경로 prefix
    }
}
