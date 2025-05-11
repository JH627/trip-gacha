package com.gacha.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.exception.TripErrorCode;
import com.gacha.exception.TripException;
import com.gacha.model.dto.request.TripRequest;
import com.gacha.model.dto.request.TripRequest.SpotCategory;
import com.gacha.model.dto.response.Response;
import com.gacha.model.service.TripService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "여행 도메인 API")
@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
public class TripController {

	private final TripService tripService;
	
	@Operation(summary = "추천 관광지 목록 조회", description = "메인 페이지에 사용할 추천 여행지 목록을 가져옵니다.")
	@GetMapping("/recommend")
	public Response<?> getRecommendSpotList() {
		Integer tempUserId = 1; // 임시 유저 ID
		return Response.onSuccess(tripService.getRecommendSpotList(tempUserId));
	}
	
	@Operation(summary = "관광지 찜하기", description = "관광지를 내 찜 목록에 추가한다. 이미 찜 목록에 있는 경우 찜을 해제한다.")
	@PostMapping("/bookmark")
	public Response<?> bookmark(@Valid @RequestBody TripRequest.BookmarkSpot bookmarkSpot) {
		Integer tempUserId = 1; // 임시 유저 ID
		tripService.toggleSpotBookmark(tempUserId, bookmarkSpot);
		return Response.onSuccess();
	}
	
	@Operation(summary = "목적지 리스트 조회", description = "검색 키워드에 따른 목적지 리스트를 반환한다. 검색 키워드가 없는 경우에는 모든 리스트를 반환한다.")
	@GetMapping("/destination")
	public Response<?> getDestinationList(@RequestParam(required = false) String keyword) {
		return Response.onSuccess(tripService.getDestinationList(keyword));
	}
	
	@Operation(summary = "숙소 리스트 조회", description = ""
			+ "검색 키워드에 따른 선택한 목적지 내 정렬된 숙소 리스트를 반환한다.<br/>"
			+ "키워드가 없는 경우 선택한 목적지 내 모든 숙소 리스트를 반환한다.<br/>"
			+ "정렬 기준이 없는 경우 평점 기준으로 기본 정렬된다.<br/>"
			+ "정렬 ENUM => LIKE → 좋아요순 / STARS → 평점순 / NAME → 숙소 이름순")
	@GetMapping("/accommodation")
	public Response<?> getAccommodationList(
			@RequestParam(required = true) Integer destinationId,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "STARS") TripRequest.SpotSearchCondition sort,
			@RequestParam(required = false, defaultValue = "0") Integer page
			) {
		Integer tempUserId = 1; // 임시 유저 ID
		return Response.onSuccess(tripService.getSpotList(tempUserId, destinationId, keyword, SpotCategory.ACCOMMODATION, sort, page));
	}
	
	@Operation(summary = "관광지 리스트 조회", description = ""
			+ "선택한 목적지 내 검색 키워드에 따른 정렬된 관광지 리스트를 반환한다.<br/>"
			+ "키워드가 없는 경우 선택한 목적지 내 모든 관광지 리스트를 반환한다.<br/>"
			+ "정렬 기준이 없는 경우 평점 기준으로 기본 정렬된다.<br/>"
			+ "관광지 카테고리가 없는 경우 명소를 기본으로 설정한다<br />"
			+ "관광지 카테고리 ENUM => ATTRACTION -> 명소 / RESTAUTRANT -> 식당 / CAFE -> 카페 / MARKED -> 찜 목록"
			+ "정렬 ENUM => LIKE → 좋아요순 / STARS → 평점순 / NAME → 관광지 이름순")
	@GetMapping("/spot")
	public Response<?> getSpotList(
			@RequestParam(required = true) Integer destinationId,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "ATTRACTION") TripRequest.SpotCategory category,
			@RequestParam(required = false, defaultValue = "STARS") TripRequest.SpotSearchCondition sort,
			@RequestParam(required = false, defaultValue = "0") Integer page
			) {
		
		// 관광지 조회 단계에서는 숙소는 조회되지 않음
		if (category == TripRequest.SpotCategory.ACCOMMODATION) {
			throw new TripException(TripErrorCode.INVALID_SPOT_CATEGORY);
		}
		
		Integer tempUserId = 1; // 임시 유저 ID
		return Response.onSuccess(tripService.getSpotList(tempUserId, destinationId, keyword, category, sort, page));
	}
	
	
	
}
