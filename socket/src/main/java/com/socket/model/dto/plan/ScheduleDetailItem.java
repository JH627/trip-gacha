package com.socket.model.dto.plan;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleDetailItem {
    private int day; // 일차
    private SpotDto spotInfo;
    private int order; // 순서
}
