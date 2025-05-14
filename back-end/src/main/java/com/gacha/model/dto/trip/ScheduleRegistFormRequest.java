package com.gacha.model.dto.trip;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gacha.model.dto.validation.annotation.ValidDateRange;

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
@ValidDateRange
public class ScheduleRegistFormRequest {
	@JsonIgnore
	private Integer tripScheduleId;
	@NotNull
	private Integer destinationId;
	@NotNull
	@Size(max = 50)
	private String title;
	@NotNull
	// yyyy-MM-dd 형식
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate;
	@NotNull
	// yyyy-MM-dd 형식
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate endDate;
	@NotNull @NotEmpty
	private List<ScheduleItem> scheduleItems;
	
	@Getter
	@Setter
	public static class ScheduleItem {
		@NotNull
		private Integer spotId;
		@NotNull
		private Integer day;
		@NotNull
		private Integer sequence;
	}
}
