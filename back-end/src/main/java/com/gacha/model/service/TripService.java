package com.gacha.model.service;

import java.util.List;

import com.gacha.model.dto.enums.SpotCategory;
import com.gacha.model.dto.enums.SpotSearchCondition;
import com.gacha.model.dto.trip.BookmarkSpotRequest;
import com.gacha.model.dto.trip.DestinationInfo;
import com.gacha.model.dto.trip.ScheduleDetail;
import com.gacha.model.dto.trip.ScheduleInfo;
import com.gacha.model.dto.trip.ScheduleRegistFormRequest;
import com.gacha.model.dto.trip.SpotInfo;
import com.gacha.model.dto.trip.SpotRegistFormRequest;
import com.gacha.model.dto.trip.SpotListResponse;

public interface TripService {
	
	/**
	 * 관광지를 찜 목록에 추가
	 * 이미 찜 목록에 있는 경우 찜을 해제
	 * 
	 * @param userId 사용자ID
	 * @param dto 찜 상태를 변경할 관광지ID
	 */
	void toggleSpotBookmark(Integer userId, BookmarkSpotRequest dto);

	/**
	 * 검색키워드에 따른 목적지리스트 반환
	 * 
	 * @param keyword 검색 키워드
	 * @return 목적지 리스트
	 */
	List<DestinationInfo> getDestinationList(String keyword);

	/**
	 * 검색 키워드에 따른 관광지 리스트 반환
	 * 
	 * @param userId 사용자ID 
	 * @param destinationId 목적지ID
	 * @param keyword 검색키워드
	 * @param category 카테고리
	 * @param sort 정렬기준
	 * @param page 페이지
	 * @return 관광지 리스트와 전체 개수를 포함한 응답
	 */
	SpotListResponse getSpotList(Integer userId, Integer destinationId, String keyword, 
			SpotCategory category, SpotSearchCondition sort, Integer page);


	/**
	 * 장소 직접 등록
	 * 
	 * @param userId 사용자ID
	 * @param form 장소정보(목적지ID, 카테고리, 이름, 주소, 설명, 이미지 파일)
	 */
	void registSpot(Integer userId, SpotRegistFormRequest form);
	
	/**
	 * 여행 일정 등록
	 * 
	 * @param userId 사용자ID
	 * @param form 여행 일정 정보(목적지ID, 제목, 시작일, 종료일, 일정 아이템들)
	 */
	void registSchedule(Integer userId, ScheduleRegistFormRequest form);
	
	/**
	 * 여행 일정 리스트 조회
	 * 
	 * @param userId 사용자ID
	 * @return 내가 등록한 여행일정 리스트(일정 이름, 목적지 이름, 시작일, 종료일, 목적지 대표 이미지, 일정 생성일)
	 */
	List<ScheduleInfo> getScheduleList(Integer userId);


	/**
	 * 여행 일정 상세 조회
	 * 
	 * @param userId 사용자ID
	 * @param scheduleId 일정ID
	 * @return 여행 일정 상세(일정이름, 시작일, 종료일, 일정 생성일, N일차(관광지 정보, 순서))
	 */
	ScheduleDetail getScheduleDetail(Integer userId, Integer scheduleId);
}
