package com.gacha.model.dto.board;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentDetail {
    private String commentId;
    private String authorName;
    private LocalDateTime createdAt;
    private String content;
    private Boolean isMine;
    private Boolean isDeleted;
}
