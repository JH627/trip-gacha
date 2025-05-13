package com.gacha.model.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BoardOrderBy {
    TITLE("제목", "title"),
    VIEW("조회수", "view_count"),
    LIKE("좋아요", "like_count"),
    COMMENT("댓글수", "comment_count");

    private final String description;
    private final String value;
}
