package com.gacha.model.dto.trip;

import org.springframework.web.multipart.MultipartFile;

import com.gacha.model.dto.enums.SpotCategory;
import com.gacha.model.dto.validation.annotation.ValidAddress;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
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
	@NotNull(message = "목적지 ID는 필수 입력값입니다.")
	private Integer destinationId;
	
	@NotNull(message = "카테고리는 필수 입력값입니다.")
	private SpotCategory category;
	
	@NotBlank(message = "이름은 필수 입력값입니다.")
	@Size(max = 255, message = "이름은 255자 이하로 입력해주세요.")
	private String name;
	
	@NotBlank(message = "주소는 필수 입력값입니다.")
	@Size(max = 100, message = "주소는 100자 이하로 입력해주세요.")
	@ValidAddress
	private String address;
	
	@NotBlank(message = "내용은 필수 입력값입니다.")
	private String content;
	
	@Nullable
	private MultipartFile img;
	
	@Nullable
	private Double latitude;
	
	@Nullable
	private Double longitude;
}
