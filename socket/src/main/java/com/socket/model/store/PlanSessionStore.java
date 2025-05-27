package com.socket.model.store;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.socket.model.dto.plan.PlanDate;
import com.socket.model.dto.plan.PlanDto;
import com.socket.model.dto.plan.PlanProgress;
import com.socket.model.dto.plan.ScheduleDetail;
import com.socket.model.dto.plan.ScheduleDetailItem;
import com.socket.model.dto.plan.SpotDto;
import com.socket.model.dto.plan.UpdateSpotInDateRequest;
import com.socket.model.dto.room.SocketRoom;
import com.socket.model.dto.room.SocketRoomUser;

@Component
public class PlanSessionStore {
    private final Map<String, PlanDto> planMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, SocketRoomUser>> planningUserMapMap = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, SpotDto>> selectedSpotMapMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, List<SpotDto>>> decidePlanPerDayMapMap =  new ConcurrentHashMap<>();
    private final Map<String, ScheduleDetail> finalScheduleDatailMap = new ConcurrentHashMap<>();

    public void addPlan(String planId, String ownerId, SocketRoom roomInfo) {
        PlanDto plan = PlanDto.builder()
                                .planId(planId)
                                .ownerId(ownerId)
                                .destinationId(roomInfo.getDestination())
                                .planProgress(PlanProgress.SELECT_ACCOMMODATION)
                                .startDate(roomInfo.getStartDate())
                                .endDate(roomInfo.getEndDate())
                                .build();
        
        planMap.put(planId, plan);

        if(!selectedSpotMapMap.containsKey(planId)){
            Map<Integer, SpotDto> selectedSpotMap = new ConcurrentHashMap<>();
            selectedSpotMapMap.put(planId, selectedSpotMap);
        }

        for(SocketRoomUser roomUser : roomInfo.getUserList()){
            joinPlan(planId, roomUser);
        }

        if(!decidePlanPerDayMapMap.containsKey(planId)){
            Map<String, List<SpotDto>> decidePlanPerDayMap = new ConcurrentHashMap<>();

            LocalDate startDate = LocalDate.parse(plan.getStartDate());
            LocalDate endDate = LocalDate.parse(plan.getEndDate());

            while(!startDate.isAfter(endDate)){
                decidePlanPerDayMap.put(startDate.toString(), new LinkedList<>());
                startDate = startDate.plusDays(1);
            }

            System.out.println(decidePlanPerDayMap);

            decidePlanPerDayMapMap.put(planId, decidePlanPerDayMap);
        }
    }

    public PlanProgress getPlanProgress(String planId){
        return planMap.get(planId).getPlanProgress();
    }

    public PlanDate getPlanDate(String planId){
        return new PlanDate(planMap.get(planId).getStartDate(), planMap.get(planId).getEndDate());
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

    public void addSpot(String planId, SpotDto spotInfo){
        selectedSpotMapMap.get(planId).put(spotInfo.getSpotId(), spotInfo);

        System.out.println("추가 후 장바구니 개수 : " + selectedSpotMapMap.get(planId).size());
    }

    public void removeSpot(String planId, SpotDto spotInfo){
        selectedSpotMapMap.get(planId).remove(spotInfo.getSpotId());

        System.out.println("삭제 후 장바구니 개수 : " + selectedSpotMapMap.get(planId).size());
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

    public Integer getPlanDestinationId(String planId){
        if(planMap.get(planId)==null){
            return null;
        }

        return planMap.get(planId).getDestinationId();
    }

    public List<Integer> getSpotIdListByPlanId(String planId) {
        Map<Integer, SpotDto> spotMap = selectedSpotMapMap.get(planId);
        if (spotMap == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(spotMap.keySet());
    }

    public List<SpotDto> getSelectedSpotList(String planId){
        Map<Integer, SpotDto> spotMap = selectedSpotMapMap.get(planId);
        if (spotMap == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(spotMap.values());
    }

    public void decideSpotInDateOnPlan(String planId, UpdateSpotInDateRequest request){
        SpotDto updateSpot = selectedSpotMapMap.get(planId).get(request.getSpotId());
        decidePlanPerDayMapMap.get(planId).get(request.getDate()).add(updateSpot);
    }

    public Map<String, List<SpotDto>> getSchedule (String planId){
        return decidePlanPerDayMapMap.get(planId);
    }

    public void addSpotToSchedule(String planId, String date, Integer spotId){
        System.out.println(planId + " " + spotId + " " + date);
        SpotDto updateSpot = selectedSpotMapMap.get(planId).get(spotId);
        decidePlanPerDayMapMap.get(planId).get(date).add(updateSpot);
    }
    // 스케줄 추가 ,삭제 만들기
    public void removeSpotToSchedule(String planId, String date, Integer spotId){
        List<SpotDto> spots = decidePlanPerDayMapMap.get(planId).get(date);
        spots.removeIf(s -> s.getSpotId() == spotId);
    }

    public void initFinalScheduleDetail(String planId, String userId){
        Map<String, List<SpotDto>> tripPlanMap = decidePlanPerDayMapMap.get(planId);

        System.out.println("=== tripPlanMap 내용 출력 ===");
        for (Map.Entry<String, List<SpotDto>> entry : tripPlanMap.entrySet()) {
            String date = entry.getKey();
            List<SpotDto> spots = entry.getValue();
            
            System.out.println("날짜: " + date);
            for (SpotDto spot : spots) {
                System.out.println(spot);
            }
        }

        PlanDto planInfo = planMap.get(planId);

        ScheduleDetail schedule = convertToScheduleDetail(tripPlanMap, planInfo, userId);

        finalScheduleDatailMap.put(planId, schedule);
    }

    public ScheduleDetail getFinalScheduleDetail(String planId){
        return finalScheduleDatailMap.get(planId);
    }

    public void updateFinalScheduleDetail(String planId, ScheduleDetail updateScheduleDetail){
        finalScheduleDatailMap.put(planId, updateScheduleDetail);
    }

    public static ScheduleDetail convertToScheduleDetail(
        Map<String, List<SpotDto>> dateSpotMap,
        PlanDto planDto,
        String currentUserId // 로그인한 사용자 ID
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = LocalDate.parse(planDto.getStartDate(), formatter);
        LocalDate end = LocalDate.parse(planDto.getEndDate(), formatter);

        ScheduleDetail scheduleDetail = ScheduleDetail.builder()
            .title("")
            .startDate(planDto.getStartDate())
            .endDate(planDto.getEndDate())
            .createAt(LocalDateTime.now()) // PlanDto에 없으므로 현재 시각 사용
            .shared(false)                 // PlanDto에 없음 → 기본 false
            .mine(planDto.getOwnerId().equals(currentUserId))
            .scheduleDetailItems(new ArrayList<>())
            .build();

        List<ScheduleDetailItem> detailItems = new ArrayList<>();

        for (Map.Entry<String, List<SpotDto>> entry : dateSpotMap.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey(), formatter);
            int day = (int) ChronoUnit.DAYS.between(start, date) + 1;

            List<SpotDto> spots = entry.getValue();
            for (int i = 0; i < spots.size(); i++) {
                SpotDto spot = spots.get(i);

                System.out.println(spot);

                SpotDto spotInfo = SpotDto.builder()
                    .spotId(spot.getSpotId())
                    .name(spot.getName())
                    .address(spot.getAddress())
                    .phone(spot.getPhone())
                    .category(spot.getCategory())
                    .content(spot.getContent())
                    .img(spot.getImg())
                    .workTime(spot.getWorkTime())
                    .stars(spot.getStars())
                    .likes(spot.getLikes())
                    .latitude(spot.getLatitude())
                    .longitude(spot.getLongitude())
                    .destination(spot.getDestination())
                    .build();

                ScheduleDetailItem item = ScheduleDetailItem.builder()
                    .day(day)
                    .order(i + 1)
                    .spotInfo(spotInfo)
                    .build();

                detailItems.add(item);
            }
        }

        detailItems = detailItems.stream()
            .sorted(Comparator.comparingInt(ScheduleDetailItem::getDay)
                              .thenComparingInt(ScheduleDetailItem::getOrder))
            .collect(Collectors.toList());

        scheduleDetail.setScheduleDetailItems(detailItems);
        return scheduleDetail;
    }
}
