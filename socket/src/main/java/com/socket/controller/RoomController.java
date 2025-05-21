package com.socket.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.socket.model.dto.lobby.LobbyResponse;
import com.socket.model.dto.lobby.LobyEventType;
import com.socket.model.dto.lobby.SocketUserInfo;
import com.socket.model.dto.room.CreateRoomRequest;
import com.socket.model.dto.room.RoomEventType;
import com.socket.model.dto.room.RoomResponse;
import com.socket.model.dto.room.SocketRoom;
import com.socket.model.dto.room.SocketRoomHeader;
import com.socket.model.dto.room.SocketRoomUser;
import com.socket.model.store.LobbySessionStore;
import com.socket.model.store.RoomSessionStore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class RoomController {
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomSessionStore store;
    private final LobbySessionStore lobbyStore;

    @MessageMapping("/room/create")
    public void enterLoby(StompHeaderAccessor accessor, CreateRoomRequest roomInfo) {
        String sessionId = accessor.getUser().getName();

        SocketUserInfo ownerInfo = lobbyStore.get(sessionId);

        SocketRoomUser owner = new SocketRoomUser(ownerInfo.getSocketId(), ownerInfo.getNickname(), ownerInfo.getImg());

        System.out.println("요청 보낸 사용자 : " + owner.getNickname());

        SocketRoom newRoom = new SocketRoom(UUID.randomUUID().toString(), 
                                            roomInfo.getTitle(), 
                                            roomInfo.getPassword(),
                                            roomInfo.getStartDate(),
                                            roomInfo.getEndDate(),
                                            roomInfo.getDestination(),
                                            owner,
                                            new ArrayList<>());

        store.add(newRoom.getRoomId(), newRoom);

        // 자기 자신
        messagingTemplate.convertAndSendToUser(
            sessionId,
            "/queue/lobby", // 유니캐스트용 endpoint
            newRoom.getRoomId()
        );

        SocketRoomHeader roomHeader = new SocketRoomHeader(newRoom.getRoomId(), 
                                newRoom.getTitle(), 
                                newRoom.getDestination(), 
                                1, 
                                newRoom.getStartDate(), 
                                newRoom.getEndDate());
        
        // 모든 사용자
        messagingTemplate.convertAndSend(
            "/topic/room",
            new RoomResponse<SocketRoomHeader>(RoomEventType.CREATE, roomHeader)
        );
    }
}
