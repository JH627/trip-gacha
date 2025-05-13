package com.gacha.model.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BoardCategoey {
    POPULAR("인기", "popular"),
    FREE("자유", "free"),
    IDEA("건의", "idea");

    private final String description;
    private final String value;
}
