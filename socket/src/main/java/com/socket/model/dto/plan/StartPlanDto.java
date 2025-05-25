package com.socket.model.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartPlanDto {
    private String planId;
    private PlanProgress progress;
}
