package com.socket.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.socket.model.dto.lobby.LobbyResponse;
import com.socket.model.dto.lobby.LobyEventType;
import com.socket.model.dto.lobby.SocketUserInfo;
import com.socket.model.store.LobbySessionStore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbySessionStore store;

    // 로비로 들어옴
    @MessageMapping("/loby/enter")
    public void enterLoby(StompHeaderAccessor accessor, SocketUserInfo userInfo) {
        String sessionId = accessor.getUser().getName();

        userInfo.setSocketId(sessionId);
        userInfo.setNickname(userInfo.getNickname()+store.getAll().size());

        System.out.println("추가된 사용자 :" + userInfo.getSocketId());

        printUsers(store.getAll());

        messagingTemplate.convertAndSendToUser(
            sessionId,
            "/queue/loby", // 유니캐스트용 endpoint
            new LobbyResponse( LobyEventType.ENTER, store.getAll())
        );

        store.add(sessionId, userInfo);

        messagingTemplate.convertAndSend(
            "/topic/loby",
            new LobbyResponse( LobyEventType.ENTER, new ArrayList<SocketUserInfo>(List.of(userInfo)))
        );
    }

    public void printUsers(Collection<SocketUserInfo> users) {
        System.out.println("------------");
        for (SocketUserInfo user : users) {
            System.out.println(user.getSocketId());
        }
    }
}
