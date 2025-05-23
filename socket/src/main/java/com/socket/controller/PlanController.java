package com.socket.controller;

import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.socket.model.dto.plan.JoinPlanDto;
import com.socket.model.dto.plan.PlanProgress;
import com.socket.model.dto.plan.StartPlanRequest;
import com.socket.model.dto.room.RoomEventType;
import com.socket.model.dto.room.RoomResponse;
import com.socket.model.store.LobbySessionStore;
import com.socket.model.store.PlanSessionStore;
import com.socket.model.store.RoomSessionStore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class PlanController {
    private final SimpMessagingTemplate messagingTemplate;
    private final PlanSessionStore planStore;
    private final LobbySessionStore lobbyStore;
    private final RoomSessionStore roomStore;

    @MessageMapping("/plan/start")
    public void startPlan(StompHeaderAccessor accessor, StartPlanRequest request){
        String userId = accessor.getUser().getName();
        String roomId = request.getRoomId();

        if(roomId==null || roomId.isBlank() || roomStore.get(roomId)==null){
            // 방이 없습니다.
            return;
        }

        if(!roomStore.isRoomOwner(roomId, userId)){
            // 당신은 owner가 아닙니다
            return;
        }

        // TODO : 방 정보 중, 상태를 planning으로 바꿈 -> 중간에 들어오거나 튕긴 사람들을 재접속 시킬거임
        
        // Plan store에 추가 (UUID로 플랜 id 정의)
        String planId = UUID.randomUUID().toString();
        
        planStore.addPlan(planId, userId);

        JoinPlanDto joinplan = new JoinPlanDto(planId, PlanProgress.SELECT_ACCOMMODATION);

        // room의 모두에게 넘어가라고 해! + { planId, progress } 반환
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId,
            new RoomResponse<JoinPlanDto>(RoomEventType.PLAN, true, joinplan)
        );
    }
}
