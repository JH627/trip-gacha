package com.socket.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.socket.model.dto.lobby.LobbyResponse;
import com.socket.model.dto.lobby.LobyEventType;
import com.socket.model.dto.lobby.SocketUserInfo;
import com.socket.model.dto.room.RoomEventType;
import com.socket.model.dto.room.RoomResponse;
import com.socket.model.dto.room.SocketRoomHeader;
import com.socket.model.store.LobbySessionStore;
import com.socket.model.store.RoomSessionStore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class LobbyController {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbySessionStore store;
    private final RoomSessionStore roomStore;

    // 로비로 들어옴
    @MessageMapping("/lobby/join")
    public void enterLoby(StompHeaderAccessor accessor, SocketUserInfo userInfo) {
        String sessionId = accessor.getUser().getName();

        userInfo.setSocketId(sessionId);
        userInfo.setNickname(userInfo.getNickname()+store.getAll().size());

        System.out.println("추가된 사용자 :" + userInfo.getSocketId());

        printUsers(store.getAll());

        messagingTemplate.convertAndSendToUser(
            sessionId,
            "/queue/lobby", // 유니캐스트용 endpoint
            new LobbyResponse<List<SocketUserInfo>>( LobyEventType.INIT, store.getAll())
        );

        store.add(sessionId, userInfo);

        messagingTemplate.convertAndSend(
            "/topic/lobby",
            new LobbyResponse<SocketUserInfo>( LobyEventType.JOIN, userInfo)
        );

        messagingTemplate.convertAndSend(
            "/topic/room",
            new RoomResponse<List<SocketRoomHeader>>( RoomEventType.INIT, true, roomStore.getAll())
        );
    }

    public void printUsers(Collection<SocketUserInfo> users) {
        System.out.println("------------");
        for (SocketUserInfo user : users) {
            System.out.println(user.getSocketId());
        }
    }
}
