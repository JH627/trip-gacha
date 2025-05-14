package com.gacha.model.dto.enums;

import lombok.AllArgsConstructor;

/**
 * 관광지 카테고리 상수
 */
@AllArgsConstructor
public enum SpotCategory {
    ACCOMMODATION("숙소"),
    ATTRACTION("명소"),
    RESTAURANT("식당"),
    CAFE("카페"),
    MARKED("찜 목록");

	private final String description;
}