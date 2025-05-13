package com.gacha.model.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BoardCategoey {
    popular("인기", "popular"),
    free("자유", "free"),
    idea("건의", "idea");

    private final String description;
    private final String value;
}
