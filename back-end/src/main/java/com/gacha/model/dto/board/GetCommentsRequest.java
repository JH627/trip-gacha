package com.gacha.model.dto.board;

import lombok.Data;

@Data
public class GetCommentsRequest {
    private Integer boardId;
    private Integer page;
    private Integer offset;
}
