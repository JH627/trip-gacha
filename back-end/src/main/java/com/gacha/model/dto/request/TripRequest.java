package com.gacha.model.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.gacha.model.dto.validation.annotation.ValidAddress;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
	 * 숙소, 관광지 등록 폼
	 */
	@Getter
	@Setter
	public static class SpotRegistForm {
		@NotNull
		private Integer destinationId;
		@NotNull
		private SpotCategory category;
		@NotNull 
		@Size(max = 255)
	    private String name;
		@NotNull
		@Size(max = 100)
		@ValidAddress
	    private String address;
		@NotNull
	    private String content;
		@Nullable
	    private MultipartFile img;
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
