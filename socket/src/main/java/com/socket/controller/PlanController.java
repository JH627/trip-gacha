package com.socket.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.socket.model.dto.plan.StartPlanDto;
import com.socket.model.dto.plan.PlanProgress;
import com.socket.model.dto.plan.SpotDto;
import com.socket.model.dto.plan.SpotResponse;
import com.socket.model.dto.plan.StartPlanRequest;
import com.socket.model.dto.room.RoomEventType;
import com.socket.model.dto.room.RoomResponse;
import com.socket.model.dto.room.SocketRoom;
import com.socket.model.dto.room.SocketRoomUser;
import com.socket.model.store.PlanSessionStore;
import com.socket.model.store.RoomSessionStore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class PlanController {
    private final SimpMessagingTemplate messagingTemplate;
    private final PlanSessionStore planStore;
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

        // 방 정보 중, 상태를 planning으로 바꿈 -> 중간에 들어오거나 튕긴 사람들을 재접속 시킬거임
        roomStore.changePlanState(roomId);
        
        // Plan store에 추가 (UUID로 플랜 id 정의)
        String planId = roomId;
        SocketRoom room = roomStore.get(roomId);

        planStore.addPlan(planId, userId, room.getDestination(), room.getUserList());

        StartPlanDto startPlan = new StartPlanDto(planId, PlanProgress.SELECT_ACCOMMODATION);

        // room의 모두에게 넘어가라고 해! + { planId, progress } 반환
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId,
            new RoomResponse<StartPlanDto>(RoomEventType.PLAN, true, startPlan)
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

            System.out.println(roomStore.get(planId));

            // spotMapMap의 id 리스트도 추가 반환
            messagingTemplate.convertAndSend(
                "/topic/plan/" + planId,
                planStore.getPlanProgress(planId)
            );
            
            SpotResponse response = new SpotResponse("add", planStore.getSpotIdListByPlanId(planId));

            messagingTemplate.convertAndSend(
                "/topic/plan/spot/" + planId,
                response
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

    @MessageMapping("/plan/user-list")
    public void sendPlanUserList(StompHeaderAccessor accessor, String planId){
        String userId = accessor.getUser().getName();
        planId = planId.replace("\"", "");

        if (planId != "" && !planId.isBlank()) {
            List<SocketRoomUser> planUserList = planStore.getPlanUserList(planId);
            
            if(planUserList==null){
                messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/user-list",
                    new ArrayList<>()
                );

                return;
            }

            List<SocketRoomUser> filteredList = planUserList.stream()
                .filter(user -> !user.getUserId().equals(userId))
                .collect(Collectors.toList());

            // 이동 성공 ( 다 같이 이동 )
            messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/user-list",
                filteredList
            );
        }
    }

    @MessageMapping("/plan/destination")
    public void sendDestinationId(StompHeaderAccessor accessor, String planId){
        String userId = accessor.getUser().getName();
        planId = planId.replace("\"", "");

        if (planId != "" && !planId.isBlank()) {
            Integer destinationId = planStore.getPlanDestinationId(planId);
            
            if(destinationId==null){
                messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/destination",
                    0
                );

                return;
            }

            // 이동 성공 ( 다 같이 이동 )
            messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/destination",
                destinationId
            );
        }
    }

    @MessageMapping("/plan/add-cart/**")
    public void addToSpotCart(StompHeaderAccessor accessor, SpotDto spotInfo){
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/app/plan/add-cart/")) {
            String planId = destination.substring("/app/plan/add-cart/".length());
            System.out.println(planId);
            planStore.addSpot(planId, spotInfo);
            // 모두에게 이번에 추가한 Id 전달 (리스트에 담아서 전달)

            SpotResponse response = new SpotResponse("add", new ArrayList<>(Arrays.asList(spotInfo.getSpotId())));

            messagingTemplate.convertAndSend(
                "/topic/plan/spot/" + planId,
                response
            );
        }
    }

    @MessageMapping("/plan/remove-cart/**")
    public void removeToSpotCart(StompHeaderAccessor accessor, SpotDto spotInfo){
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/app/plan/remove-cart/")) {
            String planId = destination.substring("/app/plan/remove-cart/".length());
            System.out.println(planId);
            planStore.removeSpot(planId, spotInfo);
            // 모두에게 이번에 추가한 Id 전달 (리스트에 담아서 전달)

            SpotResponse response = new SpotResponse("remove", new ArrayList<>(Arrays.asList(spotInfo.getSpotId())));

            messagingTemplate.convertAndSend(
                "/topic/plan/spot/" + planId,
                response
            );
        }
    }

    @MessageMapping("/plan/get-cart/**")
    public void getSpotCart(StompHeaderAccessor accessor){
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/app/plan/get-cart/")) {
            String planId = destination.substring("/app/plan/get-cart/".length());
            System.out.println(planId);

            List<SpotDto> cart = planStore.getSelectedSpotList(planId);

            messagingTemplate.convertAndSend(
                "/topic/plan/cart/" + planId,
                cart
            );
        }
    }
}
