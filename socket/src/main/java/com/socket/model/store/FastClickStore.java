package com.socket.model.store;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.socket.model.dto.game.GameUserInfo;
import com.socket.model.dto.room.SocketRoomUser;

@Component
public class FastClickStore {
    Map<String, Map<String, GameUserInfo>> FastClickDatabase = new ConcurrentHashMap<>();

    public void initFastClick(String gameId, List<SocketRoomUser> userList){
         Map<String, GameUserInfo> FastClickData = new ConcurrentHashMap<>();

         for(SocketRoomUser user : userList){
            GameUserInfo gameUserInfo = GameUserInfo.builder()
                                                    .userId(user.getUserId())
                                                    .nickname(user.getNickname())
                                                    .img(user.getImg())
                                                    .clickTime(999)
                                                    .build();
            
            System.out.println(gameUserInfo);

            FastClickData.put(user.getUserId(), gameUserInfo);
         }

         FastClickDatabase.put(gameId, FastClickData);
    }

    public void updateResult(String gameId, String userId, Integer clickTime){
        System.out.println(gameId + " " + userId);
        System.out.println(FastClickDatabase.get(gameId));
        System.out.println(FastClickDatabase.get(gameId).containsKey(userId));

        FastClickDatabase.get(gameId).get(userId).setClickTime(clickTime);

        System.out.println(FastClickDatabase.get(gameId).get(userId));
    }

    public List<String> getUserIdList(String gameId){
        List<String> idList = new ArrayList<>(FastClickDatabase.get(gameId).keySet());
        return idList;
    }

    public List<GameUserInfo> getResult(String gameId){
        System.out.println("id : " + gameId);
        System.out.println(FastClickDatabase.keySet());
        List<GameUserInfo> result = new ArrayList<>(FastClickDatabase.get(gameId).values());
        result.sort(Comparator.comparing(GameUserInfo::getClickTime));
        return result;
    }
}
