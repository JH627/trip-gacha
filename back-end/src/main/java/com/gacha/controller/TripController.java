package com.gacha.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.exception.TripErrorCode;
import com.gacha.exception.TripException;
import com.gacha.global.api.Response;
import com.gacha.global.jwt.annotation.LoginUser;
import com.gacha.model.dto.enums.SpotCategory;
import com.gacha.model.dto.enums.SpotSearchCondition;
import com.gacha.model.dto.trip.BookmarkSpotRequest;
import com.gacha.model.dto.trip.ScheduleRegistFormRequest;
import com.gacha.model.dto.trip.SpotRegistFormRequest;
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
	
	@Operation(summary = "관광지 찜하기", description = "관광지를 내 찜 목록에 추가한다. 이미 찜 목록에 있는 경우 찜을 해제한다.")
	@PostMapping("/bookmark")
	public Response<?> bookmark(@LoginUser Integer userId, @Valid @RequestBody BookmarkSpotRequest bookmarkSpot) {
		tripService.toggleSpotBookmark(userId, bookmarkSpot);
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
			+ "정렬 ENUM => STARS → 평점순 / NAME → 숙소 이름순")
	@GetMapping("/accommodation")
	public Response<?> getAccommodationList(
			@LoginUser Integer userId,
			@RequestParam(required = true) Integer destinationId,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "STARS") SpotSearchCondition sort,
			@RequestParam(required = false, defaultValue = "0") Integer page
			) {
		return Response.onSuccess(tripService.getSpotList(userId, destinationId, keyword, SpotCategory.ACCOMMODATION, sort, page));
	}
	
	@Operation(summary = "관광지 리스트 조회", description = ""
			+ "선택한 목적지 내 검색 키워드에 따른 정렬된 관광지 리스트를 반환한다.<br/>"
			+ "키워드가 없는 경우 선택한 목적지 내 모든 관광지 리스트를 반환한다.<br/>"
			+ "정렬 기준이 없는 경우 평점 기준으로 기본 정렬된다.<br/>"
			+ "관광지 카테고리가 없는 경우 명소를 기본으로 설정한다<br />"
			+ "관광지 카테고리 ENUM => ATTRACTION -> 명소 / RESTAUTRANT -> 식당 / CAFE -> 카페 / MARKED -> 찜 목록 / ALLSPOT -> 모든 관광지"
			+ "정렬 ENUM => STARS → 평점순 / NAME → 관광지 이름순")
	@GetMapping("/spot")
	public Response<?> getSpotList(
			@LoginUser Integer userId,
			@RequestParam(required = false) Integer destinationId,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false, defaultValue = "ATTRACTION") SpotCategory category,
			@RequestParam(required = false, defaultValue = "STARS") SpotSearchCondition sort,
			@RequestParam(required = false, defaultValue = "0") Integer page
			) {
		
		// 관광지 조회 단계에서는 숙소는 조회되지 않음
		if (category == SpotCategory.ACCOMMODATION) {
			throw new TripException(TripErrorCode.INVALID_SPOT_CATEGORY);
		}
		
		return Response.onSuccess(tripService.getSpotList(userId, destinationId, keyword, category, sort, page));
	}
	
	@Operation(summary = "숙소, 관광지 등록", description = "숙소, 관광지를 직접 등록한다.<br/>"
			+ "관광지 카테고리 ENUM => 숙소 -> ACCOMMODATION / ATTRACTION -> 명소 / RESTAUTRANT -> 식당 / CAFE -> 카페")
	@PostMapping(path = "/spot", consumes = "multipart/form-data")
	public Response<?> registSpot(@LoginUser Integer userId, @Valid @ModelAttribute SpotRegistFormRequest form) {
		tripService.registSpot(userId, form);
		return Response.onSuccess();
	}
	
	@Operation(summary = "여행 일정 저장", description = "여행 일정을 저장한다.")
	@PostMapping(path = "/schedule")
	public Response<?> registSchedule(@LoginUser Integer userId, @Valid @RequestBody ScheduleRegistFormRequest form) {
		tripService.registSchedule(userId, form);
		return Response.onSuccess();
	}
	
	
	@Operation(summary = "여행 일정 리스트 조회", description = "내 여행 일정 리스트를 반환한다.")
	@GetMapping(path = "/schedule")
	public Response<?> getScheduleList(@LoginUser Integer userId) {
		return Response.onSuccess(tripService.getScheduleList(userId));
	}
	
	@Operation(summary = "여행 일정 상세", description = "여행 일정을 상세하게 알려준다.")
	@GetMapping(path = "/schedule/{scheduleId}")
	public Response<?> getScheduleDetail(@LoginUser Integer userId, @PathVariable(required = true) Integer scheduleId) {
		return Response.onSuccess(tripService.getScheduleDetail(userId, scheduleId));
	}
	
}
