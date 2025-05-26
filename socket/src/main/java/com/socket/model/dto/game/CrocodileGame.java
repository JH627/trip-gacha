package com.socket.model.dto.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class CrocodileGame {
    private String gameId;
    private List<CrocodileGamePlayer> players;
    private int currentTurnIndex;
    private List<ToothState> teeth;
    private int trapToothIndex;
    private String gameState;
    private String ownerId;

    public CrocodileGame(String gameId) {
        this.gameId = gameId;
        this.players = new ArrayList<>();
        this.currentTurnIndex = 0;
        this.teeth = new ArrayList<>();
        this.gameState = "waiting";
        
        // 8개의 이빨 초기화
        for (int i = 0; i < 8; i++) {
            teeth.add(new ToothState(false));
        }
    }

    public void addPlayer(String userId, String nickname, String img) {
        // 이미 참가한 플레이어인지 확인
        boolean alreadyJoined = players.stream()
            .anyMatch(player -> player.getUserId().equals(userId));
        
        if (!alreadyJoined) {
            // 첫 번째 플레이어가 방장
            if (players.isEmpty()) {
                ownerId = userId;
            }
            
            // 실제로는 데이터베이스에서 사용자 정보를 가져와야 함
            CrocodileGamePlayer player = new CrocodileGamePlayer();
            player.setUserId(userId);
            player.setNickname(nickname); // 임시 닉네임
            player.setImg(img); // 임시 이미지
            player.setEliminated(false);
            
            players.add(player);
        }
    }

    public boolean isOwner(String userId) {
        return ownerId.equals(userId);
    }

    public void startGame() {
        this.gameState = "playing";
        this.currentTurnIndex = 0;
        
        // 함정 이빨 랜덤 선택
        Random random = new Random();
        this.trapToothIndex = random.nextInt(8);
        
        // 모든 이빨 초기화
        for (ToothState tooth : teeth) {
            tooth.setPressed(false);
        }
    }

    public boolean isCurrentPlayer(String userId) {
        if (players.isEmpty() || currentTurnIndex >= players.size()) {
            return false;
        }
        return players.get(currentTurnIndex).getUserId().equals(userId);
    }

    public boolean pressTooth(int toothIndex, String userId) {
        if (toothIndex < 0 || toothIndex >= teeth.size() || teeth.get(toothIndex).isPressed()) {
            return false;
        }
        
        // 이빨 누르기
        teeth.get(toothIndex).setPressed(true);
        
        // 함정 이빨인지 확인
        if (toothIndex == trapToothIndex) {
            // 게임 종료 - 현재 플레이어가 패배
            gameState = "failed";
            players.get(currentTurnIndex).setEliminated(true);
            return true;
        }
        
        return false;
    }

    public void nextTurn() {
        // 다음 플레이어로 턴 넘기기
        do {
            currentTurnIndex = (currentTurnIndex + 1) % players.size();
        } while (players.get(currentTurnIndex).isEliminated() && 
                 players.stream().anyMatch(p -> !p.isEliminated()));
    }

    public Map<String, Object> getGameData() {
        Map<String, Object> data = new HashMap<>();
        data.put("players", players);
        data.put("currentTurnIndex", currentTurnIndex);
        data.put("teeth", teeth);
        data.put("trapToothIndex", trapToothIndex);
        data.put("gameState", gameState);
        return data;
    }

    public Map<String, Object> getGameResult() {
        CrocodileGamePlayer loser = players.get(currentTurnIndex);
        List<CrocodileGamePlayer> survivors = players.stream()
            .filter(player -> !player.isEliminated())
            .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("loser", loser);
        result.put("survivors", survivors);
        return result;
    }

    // Getters
    public List<CrocodileGamePlayer> getPlayers() {
        return players;
    }
}
