package com.gacha.model.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDetailItem {
  private int day;
  private SpotInfo spotInfo;
  private int order;
}