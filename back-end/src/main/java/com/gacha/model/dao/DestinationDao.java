package com.gacha.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gacha.model.dto.response.trip.DestinationInfo;

@Mapper
public interface DestinationDao {
	List<DestinationInfo> selectByKeyword(String keyword);
}
