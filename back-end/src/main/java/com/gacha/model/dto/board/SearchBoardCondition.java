package com.gacha.model.dto.board;

import com.gacha.model.dto.enums.BoardCategoey;
import com.gacha.model.dto.enums.BoardOrderBy;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
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
public class SearchBoardCondition {
        // 페이지 번호 (기본 1페이지 - 페이지 오프셋은 시작이 0임)
        private int page = 0;
        // 페이지 당 항목 수 (기본: 5)
        private int offset = 5;

        // 제목 필터 (검색어)
        private String title;

        // 검색어 검색 기준 (제목, 내용, 글쓴이)
        private String searchType = "title";
        
        // 카테고리 (기본: 인기)
        private BoardCategoey category = BoardCategoey.POPULAR;

        // 정렬 기준: title, likes
        private BoardOrderBy orderBy = BoardOrderBy.TITLE; // 기본값: 작성일자

        // 정렬 방향: asc 또는 desc
        private Boolean isDesc = false;
        
        
}
