package com.gacha.model.dto.board;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardDetail {
    private String category;
    private String title;
    private String authorName;
    private String createdAt;
    private int viewCount;
    private int likeCount;
    private String content;
    private boolean liked;
    private boolean isReported;
}