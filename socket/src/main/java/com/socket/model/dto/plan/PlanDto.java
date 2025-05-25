package com.socket.model.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanDto {
    private String planId;
    private String ownerId;
    private Integer destinationId;
    private PlanProgress planProgress;
}
