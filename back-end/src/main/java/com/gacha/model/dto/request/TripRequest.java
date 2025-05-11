package com.gacha.model.dto.request;

import jakarta.validation.constraints.NotNull;
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
}
