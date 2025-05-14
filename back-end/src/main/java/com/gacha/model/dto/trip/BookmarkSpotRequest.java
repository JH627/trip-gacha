package com.gacha.model.dto.trip;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 찜할 관광지 ID
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkSpotRequest {
	@NotNull
    private Integer spotId;
}
