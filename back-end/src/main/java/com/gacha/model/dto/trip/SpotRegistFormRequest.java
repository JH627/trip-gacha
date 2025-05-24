package com.gacha.model.dto.trip;

import org.springframework.web.multipart.MultipartFile;

import com.gacha.model.dto.enums.SpotCategory;
import com.gacha.model.dto.validation.annotation.ValidAddress;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 숙소, 관광지 등록 폼
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpotRegistFormRequest {
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
    @Nullable
    private Double latitude;
    @Nullable
    private Double longitude;
}
