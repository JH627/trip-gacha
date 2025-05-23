package com.gacha.model.dto.trip;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotListResponse {
    private List<SpotInfo> spots;
    private int total;
} 