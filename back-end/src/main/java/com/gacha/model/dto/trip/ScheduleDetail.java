package com.gacha.model.dto.trip;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 여행 일정 상세(일정이름, 시작일, 종료일, 일정 생성일, N일차(관광지 정보, 순서))
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDetail {
	private String title;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate endDate;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate createAt;

	private List<ScheduleDetailItem> scheduleDetailItems;
}
