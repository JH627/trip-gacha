package com.gacha.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gacha.model.dto.request.TripRequest;

import java.util.List;

@Mapper
public interface TripScheduleDao {

    int insertSchedule(@Param("userId") Integer userId,
    				   @Param("form") TripRequest.ScheduleRegistForm form);

    void insertScheduleItems(@Param("items") List<TripRequest.ScheduleRegistForm.ScheduleItem> items,
                             @Param("scheduleId") Integer scheduleId);
} 