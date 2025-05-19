package com.gacha.model.dto.board;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardSearchResponse {
    private List<BoardHeader> boards;
    private int currentPage;
    private int totalCount;
} 