package com.socket.model.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.socket.model.dto.plan.PlanDto;
import com.socket.model.dto.plan.PlanProgress;
import com.socket.model.dto.room.SocketRoomUser;

@Component
public class PlanSessionStore {
    private final Map<String, PlanDto> planMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, SocketRoomUser>> planningUserMapMap = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> selectedSpotSetMap = new ConcurrentHashMap<>();

    public void addPlan(String planId, String ownerId, List<SocketRoomUser> roomUsers) {
        PlanDto plan = PlanDto.builder()
                                .planId(planId)
                                .ownerId(ownerId)
                                .planProgress(PlanProgress.SELECT_ACCOMMODATION)
                                .build();
        
        planMap.put(planId, plan);

        if(!selectedSpotSetMap.containsKey(planId)){
            Set<String> selectedSpotSet = new ConcurrentSkipListSet<>();
            selectedSpotSetMap.put(planId, selectedSpotSet);
        }

        for(SocketRoomUser roomUser : roomUsers){
            joinPlan(planId, roomUser);
        }
    }

    public PlanProgress getPlanProgress(String planId){
        return planMap.get(planId).getPlanProgress();
    }

    public boolean goNextProgress(String planId){
        PlanProgress currentProgress = getPlanProgress(planId);

        if(currentProgress == PlanProgress.COMPLETE){
            return false;
        }

        currentProgress = currentProgress.next();

        planMap.get(planId).setPlanProgress(currentProgress);

        return true;
    }

    public boolean goPrevProgress(String planId){
        PlanProgress currentProgress = getPlanProgress(planId);

        if(currentProgress == PlanProgress.SELECT_ACCOMMODATION){
            return false;
        }

        currentProgress = currentProgress.prev();

        planMap.get(planId).setPlanProgress(currentProgress);

        return true;
    }

    public void joinPlan(String planId, SocketRoomUser userInfo){
        if(!planningUserMapMap.containsKey(planId)){
            Map<String, SocketRoomUser> planningUserMap = new ConcurrentHashMap<>();
            planningUserMapMap.put(planId, planningUserMap);
        }

        planningUserMapMap.get(planId).put(userInfo.getUserId(), userInfo);
    }

    public void addSpot(String planId, String spotId){
        selectedSpotSetMap.get(planId).add(spotId);
    }

    public boolean isUserInPlan(String planId, String userId){
        return planningUserMapMap.get(planId).containsKey(userId);
    }

    public boolean isPlanOwner(String planId, String userId){
        return planMap.get(planId).getOwnerId().equals(userId);
    }

    public List<SocketRoomUser> getPlanUserList(String planId){
        if(planningUserMapMap.get(planId)==null){
            return null;
        }

        return new ArrayList<>(planningUserMapMap.get(planId).values());
    }
}
