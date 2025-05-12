package com.gacha.model.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImageCategory {
    profile("사용자 프로필 사진", "profile/"),
    spot("관광지 사진", "spot/"),
    board("게시글", "board/");

    private final String description;
    private final String url;
}
