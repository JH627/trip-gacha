package com.gacha.model.dto.enums;

import lombok.AllArgsConstructor;

/**
 * 정렬 기준
 */
@AllArgsConstructor
public enum SpotSearchCondition {
	LIKE("좋아요 순"), 
	STARS("평점 순"),
	NAME("이름 순");
	
	private final String description;
}
