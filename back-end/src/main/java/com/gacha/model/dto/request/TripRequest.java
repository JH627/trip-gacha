package com.gacha.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class TripRequest {

	/**
	 * 찜할 관광지 ID
	 */
	@Getter
    public static class BookmarkSpot {
		@NotNull
        private Integer spotId;
    }
	
	/**
	 * 정렬 기준
	 */
	@AllArgsConstructor
	public enum SpotSearchCondition {
		LIKE("좋아요 순"), 
		STARS("평점 순"),
		NAME("이름 순");
		
		private final String description;
	}
	
	/**
	 * 관광지 카테고리 상수
	 */
	@AllArgsConstructor
	public enum SpotCategory {
	    ACCOMMODATION("숙소"),
	    ATTRACTION("명소"),
	    RESTAURANT("식당"),
	    CAFE("카페"),
	    MARKED("찜 목록");

		private final String description;
	}

}
