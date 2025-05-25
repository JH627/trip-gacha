package com.socket.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.socket.model.dto.game.InviteRequest;
import com.socket.model.dto.lobby.LobbyResponse;
import com.socket.model.dto.lobby.LobyEventType;
import com.socket.model.dto.lobby.SocketUserInfo;
import com.socket.model.dto.plan.StartPlanRequest;
import com.socket.model.store.PlanSessionStore;
import com.socket.model.store.RoomSessionStore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class GameController {
    private final SimpMessagingTemplate messagingTemplate;
    private final PlanSessionStore planStore;
    private final RoomSessionStore roomStore;

    @MessageMapping("/game/invite")
    public void invitePlayers(StompHeaderAccessor accessor, InviteRequest request){
        String requesterId = accessor.getUser().getName();

        List<String> invitedUserIdList = request.getUserIds();

        for(String id : invitedUserIdList){
            System.out.println("초대할 유저 id : " + id);
        }

        if(invitedUserIdList==null || invitedUserIdList.isEmpty()){
            return;
        }

        // 게임 Store 만들고, 새로운 게임방 추가 (결과 저장할 곳)

        for(String invitedUserId : invitedUserIdList){
            messagingTemplate.convertAndSendToUser(
                invitedUserId,
                "/queue/game", // 유니캐스트용 endpoint
                // 게임 고른거만
                request.getGameType()
            );
        }
    }
}
