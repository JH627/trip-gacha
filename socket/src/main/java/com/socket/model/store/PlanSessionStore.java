package com.socket.model.store;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.springframework.stereotype.Component;

import com.socket.model.dto.plan.PlanDto;
import com.socket.model.dto.plan.PlanProgress;
import com.socket.model.dto.room.SocketRoomUser;

@Component
public class PlanSessionStore {
    private final Map<String, PlanDto> planMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, SocketRoomUser>> planningUserMapMap = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> selectedSpotSetMap = new ConcurrentHashMap<>();

    public void addPlan(String planId, String ownerId) {
        PlanDto plan = PlanDto.builder()
                                .planId(planId)
                                .ownerId(ownerId)
                                .planProgress(PlanProgress.SELECT_ACCOMMODATION)
                                .build();
        
        planMap.put(planId, plan);

        if(!selectedSpotSetMap.containsKey(planId)){
            Set<String> selectedSpotList = new ConcurrentSkipListSet<>();
            selectedSpotSetMap.put(planId, selectedSpotList);
        }
    }

    public void joinPlan(String planId, SocketRoomUser userInfo){
        if(!planningUserMapMap.containsKey(planId)){
            Map<String, SocketRoomUser> planningUserMap = new ConcurrentHashMap<>();
            planningUserMapMap.put(planId, planningUserMap);
        }

        // userId, userInfo를 넣으면됨
        planningUserMapMap.get(planId).put(userInfo.getUserId(), userInfo);
    }

    public void addSpot(String planId, String spotId){
        selectedSpotSetMap.get(planId).add(spotId);
    }
}
