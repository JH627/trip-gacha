package com.gacha.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gacha.model.dto.trip.SpotInfo;

@Mapper
public interface SpotDao {
    // 추천 관광지 목록 조회
    List<SpotInfo> selectAll(@Param("userId") Integer userId);

    // 찜 목록 조회
    List<SpotInfo> selectBookmarkedSpots(
        @Param("userId") Integer userId,
        @Param("destinationId") Integer destinationId,
        @Param("keyword") String keyword,
        @Param("sort") String sort,
        @Param("offset") Integer offset,
        @Param("pageSize") Integer pageSize
    );

    // 전체 관광지 조회
    List<SpotInfo> selectAllSpots(
        @Param("userId") Integer userId,
        @Param("destinationId") Integer destinationId,
        @Param("keyword") String keyword,
        @Param("sort") String sort,
        @Param("offset") Integer offset,
        @Param("pageSize") Integer pageSize
    );

    // 카테고리별 관광지 조회
    List<SpotInfo> selectByDesinationIdAndCategory(
        @Param("userId") Integer userId,
        @Param("destinationId") Integer destinationId,
        @Param("category") String category,
        @Param("keyword") String keyword,
        @Param("sort") String sort,
        @Param("offset") Integer offset,
        @Param("pageSize") Integer pageSize
    );

    // 찜하기 상태 확인
    boolean isBookmarked(@Param("userId") Integer userId, @Param("spotId") Integer spotId);

    // 관광지 존재 여부 확인
    boolean existsSpot(@Param("spotId") Integer spotId);

    // 찜하기 추가
    void addBookmark(@Param("userId") Integer userId, @Param("spotId") Integer spotId);

    // 찜하기 삭제
    void deleteBookmark(@Param("userId") Integer userId, @Param("spotId") Integer spotId);

    // 관광지 등록
    void insertSpot(
        @Param("destinationId") Integer destinationId,
        @Param("name") String name,
        @Param("content") String content,
        @Param("img") String img,
        @Param("address") String address,
        @Param("category") String category
    );

    // 전체 개수 조회
    int getTotalCount();
}
