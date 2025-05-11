package com.gacha.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.model.dto.request.TripRequest;
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
	public Response<?> spotList() {
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
	
}
