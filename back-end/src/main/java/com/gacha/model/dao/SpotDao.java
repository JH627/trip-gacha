package com.gacha.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gacha.model.dto.response.trip.SpotInfo;

@Mapper
public interface SpotDao {
    List<SpotInfo> selectAll(int userId);
    
    boolean isBookmarked(int userId, int spotId);
    
    void deleteBookmark(int userId, int spotId);
    
    void addBookmark(int userId, int spotId);

    List<SpotInfo> selectByDesinationIdAndCategory(Integer userId, Integer destinationId, String category, String keyword, String sort, Integer page);
    
    List<SpotInfo> selectBookmarkedSpots(Integer userId, Integer destinationId, String keyword, String sort, Integer page);
}
