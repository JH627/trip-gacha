package com.socket.model.dto.plan;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ScheduleDetail {
    private String title;
    private String startDate;
    private String endDate;
    private LocalDateTime createAt;
    private List<ScheduleDetailItem> scheduleDetailItems;
    private boolean shared;
    private boolean mine;
}
