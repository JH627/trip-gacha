package com.gacha.model.service;

import java.util.List;

import com.gacha.model.dto.request.TripRequest;
import com.gacha.model.dto.response.trip.SpotInfo;

public interface TripService {
	
	/**
	 * 사용자의 추천 관광지 목록을 반환
	 * 
	 * @param userId 사용자ID
	 * @return 추천 관광지 목록
	 */
	List<SpotInfo> getRecommendSpotList(Integer userId);
	
	
	/**
	 * 관광지를 찜 목록에 추가
	 * 이미 찜 목록에 있는 경우 찜을 해제
	 * 
	 * @param userId 사용자ID
	 * @param dto 찜 상태를 변경할 관광지ID
	 */
	void toggleSpotBookmark(Integer userId, TripRequest.BookmarkSpot dto);
	
}
