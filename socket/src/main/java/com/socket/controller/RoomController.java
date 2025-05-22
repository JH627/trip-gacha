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
import com.socket.model.dto.room.JoinRoomRequest;
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
    public void createRoom(StompHeaderAccessor accessor, CreateRoomRequest roomInfo) {
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

        SocketRoom roomSimpleInfo = new SocketRoom();
        roomSimpleInfo.setRoomId(newRoom.getRoomId());

        // 자기 자신
        messagingTemplate.convertAndSendToUser(
            sessionId,
            "/queue/room", // 유니캐스트용 endpoint
            new RoomResponse<SocketRoom>(RoomEventType.CREATE, true ,roomSimpleInfo)
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
            new RoomResponse<SocketRoomHeader>(RoomEventType.CREATED, true, roomHeader)
        );
    }

    // TODO : 비밀번호 확인하는 함수 구현
    @MessageMapping("/room/join/**")
    public void joinRoom(StompHeaderAccessor accessor, JoinRoomRequest request){

        String destination = accessor.getDestination();
        String userId = accessor.getUser().getName();
        boolean success = false;
        SocketRoomHeader roomHeader = new SocketRoomHeader();

        System.out.println(destination);
        System.out.println(userId);

        if (destination != null && destination.startsWith("/app/room/join/")) {
            String roomId = destination.substring("/app/room/join/".length());
            System.out.println("Room ID: " + roomId);

            SocketRoom room = store.get(roomId);

            System.out.println(room.getOwner().getUserId().equals(userId));
            System.out.println(room.getPassword().equals(request.getPassword()));

            // 방장이거나 비밀번호가 일치하면
            if(room.getOwner().getUserId().equals(userId) || room.getPassword().equals(request.getPassword())){
                // 유저 리스트에 추가
                SocketUserInfo user = lobbyStore.get(userId);
                SocketRoomUser simpleUser = new SocketRoomUser();
                simpleUser.setUserId(userId);
                simpleUser.setNickname(user.getNickname());
                simpleUser.setImg(user.getImg());

                room.getUserList().add(simpleUser);

                roomHeader.setRoomId(roomId);

                success = true;
            }
        }

        messagingTemplate.convertAndSendToUser(
            userId,
            "/queue/room",
            new RoomResponse<>(RoomEventType.JOIN, success, roomHeader)
        );
    }

    @MessageMapping("/room/:roomId")
    public void getRoomInfo(StompHeaderAccessor accessor){
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/room/")) {
            String roomId = destination.substring("/room/".length());
            System.out.println("Room ID: " + roomId);

            // 이후 로직 처리
            SocketRoom room = store.get(roomId);

            SocketRoom coptRoom = new SocketRoom();
            
            coptRoom.setRoomId(room.getRoomId());
            coptRoom.setTitle(room.getTitle());
            coptRoom.setDestination(room.getDestination());
            coptRoom.setStartDate(room.getStartDate());
            coptRoom.setEndDate(room.getEndDate());
            coptRoom.setUserList(room.getUserList());
    
            messagingTemplate.convertAndSend(
                "room/"+coptRoom.getRoomId(),
                new RoomResponse<SocketRoom>(RoomEventType.CREATE, true, coptRoom)
            );
        }
    }
}
