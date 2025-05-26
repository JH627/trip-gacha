package com.gacha.model.dto.trip;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleUpdateFormRequest {
	private Integer tripScheduleId;
	
	@NotNull(message = "제목은 필수 입력값입니다.")
	@Size(min = 1, max = 50, message = "제목은 1자 이상 50자 이하로 입력해주세요.")
	private String title;
	
	@NotNull(message = "시작일은 필수 입력값입니다.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate;
	
	@NotNull(message = "종료일은 필수 입력값입니다.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate endDate;
	
	@NotNull(message = "일정 항목은 필수 입력값입니다.")
	@NotEmpty(message = "일정 항목은 최소 1개 이상이어야 합니다.")
	@Valid
	private List<ScheduleItem> scheduleItems;
	
	@Getter
	@Setter
	public static class ScheduleItem {
		@NotNull(message = "관광지 ID는 필수 입력값입니다.")
		private Integer spotId;
		
		@NotNull(message = "일차는 필수 입력값입니다.")
		private Integer day;
		
		@NotNull(message = "순서는 필수 입력값입니다.")
		private Integer sequence;
	}
}
