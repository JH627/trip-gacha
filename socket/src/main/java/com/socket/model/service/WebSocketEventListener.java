package com.socket.model.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.socket.model.dto.lobby.LobbyResponse;
import com.socket.model.dto.lobby.LobyEventType;
import com.socket.model.dto.lobby.SocketUserInfo;
import com.socket.model.dto.room.RoomEventType;
import com.socket.model.dto.room.RoomResponse;
import com.socket.model.dto.room.SocketRoom;
import com.socket.model.dto.room.SocketRoomUser;
import com.socket.model.store.LobbySessionStore;
import com.socket.model.store.RoomSessionStore;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final LobbySessionStore store;
    private final RoomSessionStore roomStore;

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();

        if (user != null) {
            String sessionId = user.getName();

            // store에서 해당 유저 삭제
            SocketUserInfo removedUser = store.remove(sessionId);

            List<SocketUserInfo> removeUserList = new ArrayList<>();

            removeUserList.add(removedUser);

            System.out.println("remove :" + removedUser);

            LobbyResponse<SocketUserInfo> msg = new LobbyResponse<SocketUserInfo>(LobyEventType.LEAVE, removedUser);

            if (removedUser != null) {
                // 다른 사용자들에게 알림 전송
                messagingTemplate.convertAndSend(
                    "/topic/lobby",
                    msg // 또는 유저가 나갔다는 별도의 메시지 객체를 만들어 보내도 됨
                );
            }

            // 연결 끊긴 유저가 속했던 방 번호
            String roomId = roomStore.findUsersRoomId(sessionId);
            SocketRoom userRoom = roomStore.get(roomId);

            if(roomId != null && !roomId.isBlank()){
                // 해당 방에서 사용자 정보를 삭제하라는 뜻
                SocketRoom leaveUserRoom = new SocketRoom();
                SocketRoomUser leaveUser = null;
                for (SocketRoomUser u : userRoom.getUserList()) {
                    if (u.getUserId().equals(sessionId)) {
                        leaveUser = u;
                        break;
                    }
                }

                List<SocketRoomUser> leaveUserList = new ArrayList<>();
                leaveUserList.add(leaveUser);

                leaveUserRoom.setUserList(leaveUserList);

                messagingTemplate.convertAndSend(
                    "/topic/room/" + roomId,
                    new RoomResponse<>(RoomEventType.LEAVE, true, leaveUserRoom)
                );
            }

            // 삭제된 유저가 owner일 경우, 방 삭제
            if(userRoom.getOwner().getUserId().equals(sessionId)){
                messagingTemplate.convertAndSend(
                    "/topic/room/" + roomId,
                    new RoomResponse<>(RoomEventType.BOOM, true, null)
                );
            }
        }
    }
}
