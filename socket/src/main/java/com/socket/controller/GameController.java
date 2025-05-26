package com.socket.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.socket.model.dto.game.GameProgress;
import com.socket.model.dto.game.GameResponse;
import com.socket.model.dto.game.GameUserInfo;
import com.socket.model.dto.game.InviteRequest;
import com.socket.model.dto.room.SocketRoomUser;
import com.socket.model.store.FastClickStore;
import com.socket.model.store.PlanSessionStore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class GameController {
    private final SimpMessagingTemplate messagingTemplate;
    private final FastClickStore fastClickStore;
    private final PlanSessionStore planStore;

    @MessageMapping("/game/get-user-id")
    public void getRequesterId(StompHeaderAccessor accessor){
        String requesterId = accessor.getUser().getName();

        messagingTemplate.convertAndSendToUser(
            requesterId,
            "/queue/game/userId", // 유니캐스트용 endpoint
            requesterId
        );
    }

    @MessageMapping("/game/invite")
    public void invitePlayers(StompHeaderAccessor accessor, InviteRequest request){
        List<String> userIds = request.getUserIds();
        String requesterId = accessor.getUser().getName();

        for(String id : userIds){
            System.out.println("초대할 유저 id : " + id);
        }

        if(userIds==null || userIds.isEmpty()){
            return;
        }

        List<SocketRoomUser> planUserList = planStore.getPlanUserList(request.getPlanId());

        List<SocketRoomUser> filteredUsers = planUserList.stream()
                                                .filter(user -> userIds.contains(user.getUserId()) || user.getUserId().equals(requesterId))
                                                .collect(Collectors.toList());

        // 게임 Store 만들고, 새로운 게임방 추가 (결과 저장할 곳)
        switch (request.getGameType()) {
            case FAST_CLICK:
                fastClickStore.initFastClick(request.getPlanId(), filteredUsers);
                break;
            case CROCODILIA:
                // ...
                break;
        }

        for(String userId: userIds){
            messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/game", // 유니캐스트용 endpoint
                // 게임 고른거만
                request.getGameType()
            );
        }
    }


    @MessageMapping("/game/fast-click/start/**")
    public void startFastClick(StompHeaderAccessor accessor){
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/app/game/fast-click/start/")) {
            String gameId = destination.substring("/app/game/fast-click/start/".length());
            
            System.out.println("게임 시작 - gameId: " + gameId);
            
            messagingTemplate.convertAndSend(
                "/topic/game/fast-click/" + gameId,
                new GameResponse<>(null, GameProgress.PLAY_GAME)
            );

            System.out.println("PLAY_GAME 신호 전송 완료");
            
            // 스케줄러 실행 확인
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {
                try {
                    System.out.println("10초 후 결과 처리 시작 - gameId: " + gameId);
                    
                    List<GameUserInfo> results = fastClickStore.getResult(gameId);
                    
                    System.out.println("결과 개수: " + (results != null ? results.size() : "null"));
                    System.out.println("결과 -----");
                    for(GameUserInfo u : results){
                        System.out.println(u);
                    }
                    
                    messagingTemplate.convertAndSend(
                        "/topic/game/fast-click/" + gameId,
                        new GameResponse<>(results, GameProgress.RESULT)
                    );
                    
                    System.out.println("결과 전송 완료");
                    
                } catch (Exception e) {
                    System.err.println("결과 처리 중 오류: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    executor.shutdown(); // 리소스 정리
                }
            }, 10, TimeUnit.SECONDS);
            
            System.out.println("10초 타이머 설정 완료");
        }
    }

    @MessageMapping("/game/fast-click/update/**")
    public void updateFastClick(StompHeaderAccessor accessor, Integer clickTime){
        String destination = accessor.getDestination();
        String userId = accessor.getUser().getName();

        System.out.println("유저 " + userId + " 시간 : " + clickTime);

        if (destination != null && destination.startsWith("/app/game/fast-click/update/")) {
            String gameId = destination.substring("/app/game/fast-click/update/".length());

            fastClickStore.updateResult(gameId, userId, clickTime);
        }
    }
}
