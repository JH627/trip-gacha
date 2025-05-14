package com.gacha.model.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentRequest {
    private String content;
    private Integer boardId;
    private Integer commentId;
}
