package com.gacha.model.dto.board;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BoardHeader {
    private Integer boardId;
    private String title;
    private String authorName;
    private LocalDateTime createdAt;
    private Integer likeCount;
    private Integer viewCount;
    private Integer commentCount;
}
