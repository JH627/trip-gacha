package com.gacha.model.dto.trip;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 여행 일정 리스트 정보
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleInfo {
  private Integer scheduleId;
  private String title;
  private String destination;
  private String destinationImg;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate endDate;

  private LocalDate createAt;
}
