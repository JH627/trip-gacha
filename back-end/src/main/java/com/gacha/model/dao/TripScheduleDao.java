package com.gacha.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gacha.model.dto.trip.ScheduleDetail;
import com.gacha.model.dto.trip.ScheduleInfo;
import com.gacha.model.dto.trip.ScheduleRegistFormRequest;

import java.util.List;

@Mapper
public interface TripScheduleDao {

    int insertSchedule(@Param("userId") Integer userId,
    				   @Param("form") ScheduleRegistFormRequest form);

    void insertScheduleItems(@Param("items") List<ScheduleRegistFormRequest.ScheduleItem> items,
                             @Param("scheduleId") Integer scheduleId);

	List<ScheduleInfo> selectAllScheduleByUserId(Integer userId);

	boolean checkIsShared(Integer userId, Integer scheduleId);

	ScheduleDetail selectScheduleByUserId(Integer userId, Integer scheduleId);
	
	boolean isOwner(Integer userId, Integer scheduleId);
	
	/**
	 * 여행 일정 공유 상태를 토글한다.
	 * @param scheduleId 일정 ID
	 */
	void toggleShareStatus(Integer scheduleId);
} 