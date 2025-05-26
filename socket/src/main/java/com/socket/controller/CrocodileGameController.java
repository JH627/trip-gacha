package com.socket.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.socket.model.dto.game.CrocodileGame;
import com.socket.model.dto.game.GameProgress;
import com.socket.model.dto.game.GameResponse;
import com.socket.model.dto.room.SocketRoomUser;
import com.socket.model.store.PlanSessionStore;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class CrocodileGameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, CrocodileGame> games = new ConcurrentHashMap<>();
    private final PlanSessionStore planStore;

    @MessageMapping("/game/crocodile/join/{gameId}")
    public void joinGame(@DestinationVariable String gameId, StompHeaderAccessor accessor) {
        String userId = accessor.getUser().getName();
        
        CrocodileGame game = games.computeIfAbsent(gameId, k -> new CrocodileGame(gameId));
        
        List<SocketRoomUser> planUserList = planStore.getPlanUserList(gameId);

        for(SocketRoomUser user : planUserList){
            if(user.getUserId().equals(userId)){
                game.addPlayer(userId, user.getNickname(), user.getImg());
                break;
            }
        }

        
        // 대기 상태 브로드캐스트
        GameResponse response = new GameResponse();
        response.setProgress(GameProgress.WAIT);
        response.setData(Map.of("players", game.getPlayers()));
        
        messagingTemplate.convertAndSend("/topic/game/crocodile/" + gameId, response);
    }

    @MessageMapping("/game/crocodile/start/{gameId}")
    public void startGame(@DestinationVariable String gameId, StompHeaderAccessor accessor) {
        String userId = accessor.getUser().getName();
        CrocodileGame game = games.get(gameId);

        System.out.println("악엇" + gameId);
        
        if (game == null || !game.isOwner(userId)) {
            return; // 게임이 없거나 방장이 아닌 경우
        }
        
        // 게임 시작
        game.startGame();
        
        // 게임 시작 상태 브로드캐스트
        GameResponse response = new GameResponse();
        response.setProgress(GameProgress.PLAY_GAME);
        response.setData(game.getGameData());
        
        messagingTemplate.convertAndSend("/topic/game/crocodile/" + gameId, response);
    }

    @MessageMapping("/game/crocodile/press/{gameId}")
    public void pressTooth(@DestinationVariable String gameId, 
                          @Payload Map<String, Integer> payload,
                          StompHeaderAccessor accessor) {
        String userId = accessor.getUser().getName();
        CrocodileGame game = games.get(gameId);
        
        if (game == null || !game.isCurrentPlayer(userId)) {
            return; // 게임이 없거나 현재 턴이 아닌 경우
        }
        
        int toothIndex = payload.get("toothIndex");
        boolean gameEnded = game.pressTooth(toothIndex, userId);
        
        if (gameEnded) {
            // 게임 종료 - 결과 전송
            GameResponse response = new GameResponse();
            response.setProgress(GameProgress.RESULT);
            response.setData(game.getGameResult());
            
            messagingTemplate.convertAndSend("/topic/game/crocodile/" + gameId, response);
            
            // 게임 제거
            games.remove(gameId);
        } else {
            // 게임 계속 - 다음 턴
            game.nextTurn();
            
            GameResponse response = new GameResponse();
            response.setProgress(GameProgress.PLAY_GAME);
            response.setData(game.getGameData());
            
            messagingTemplate.convertAndSend("/topic/game/crocodile/" + gameId, response);
        }
    }
}
