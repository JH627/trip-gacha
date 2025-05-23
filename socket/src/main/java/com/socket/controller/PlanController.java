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
import com.socket.model.dto.room.SocketRoom;
import com.socket.model.dto.room.SocketRoomUser;
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
        SocketRoom room = roomStore.get(roomId);

        planStore.addPlan(planId, userId, room.getUserList());

        JoinPlanDto joinplan = new JoinPlanDto(planId, PlanProgress.SELECT_ACCOMMODATION);

        // room의 모두에게 넘어가라고 해! + { planId, progress } 반환
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId,
            new RoomResponse<JoinPlanDto>(RoomEventType.PLAN, true, joinplan)
        );
    }

    @MessageMapping("/plan/join/**")
    // roomId랑 planId를 줘야함 ㅇㅇ
    public void joinPlan(StompHeaderAccessor accessor){
        String destination = accessor.getDestination();
        System.out.println(destination);
        String userId = accessor.getUser().getName();

        if (destination != null && destination.startsWith("/app/plan/join/")) {
            String planId = destination.substring("/app/plan/join/".length());

            // plan에 있는 유저들 중에서 내 아이디와 일치하는 유저가 없으면 스타트 멤버가 아님
            if(!planStore.isUserInPlan(planId, userId)){
                return;
            }

            // 현재 상태를 반환해!
            messagingTemplate.convertAndSend(
                "/topic/plan/" + planId,
                planStore.getPlanProgress(planId)
            );
        }
    }

    @MessageMapping("/plan/move/**")
    public void movePlanPage(StompHeaderAccessor accessor, boolean goNext){
        String destination = accessor.getDestination();
        String userId = accessor.getUser().getName();

        if (destination != null && destination.startsWith("/app/plan/move/")) {
            String planId = destination.substring("/app/plan/move/".length());

            // 그 오우너가 아니면 앙대여
            if(!planStore.isPlanOwner(planId, userId)){
                // 권한이 없습니다 전송 ( 팀원한테만 )
                messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/plan",
                    "페이지 이동 권한이 없습니다."
                );
                return;
            }

            boolean result = false;

            System.out.println("현재 위치 :" + planStore.getPlanProgress(planId));

            if(goNext){
                result = planStore.goNextProgress(planId);
            } else {
                result = planStore.goPrevProgress(planId);
            }
            
            if(!result){
                // 앞입니다 or 뒤입니다 전송 ( 방장한테만 )
                messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/plan",
                    goNext ? "마지막 단계 입니다." : "처음 단계 입니다."
                );
                return;
            }

            System.out.println("이동 위치 :" + planStore.getPlanProgress(planId));

            // 이동 성공 ( 다 같이 이동 )
            messagingTemplate.convertAndSend(
                "/topic/plan/" + planId,
                planStore.getPlanProgress(planId)
            );
        }
    }

    //@MessageMapping("/plan/current/**")
}
