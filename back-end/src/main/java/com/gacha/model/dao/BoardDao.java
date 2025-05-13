package com.gacha.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gacha.model.dto.board.BoardDto;
import com.gacha.model.dto.request.SearchBoardCondition;

@Mapper
public interface BoardDao {
    // 게시글 리스트 검색
    List<BoardDto> selectByCondition();
    
    // 게시글 조회
    BoardDto selectById(SearchBoardCondition searchBoardCondition, Integer boardId);

    // 게시글 생성
    void insert(BoardDto boardDto, Integer userId);

    // 게시글 수정
    void updateById(BoardDto boardDto, Integer userId);

    // 게시글 삭제
    void deleteById(Integer boardId, Integer userId);

    // 게시글 신고
    void report(Integer boardId, Integer userId);

    // 게시글 좋아요
    void like(Integer boardId, Integer userId);

    // 게시글 싫어요
    void dislike(Integer boardId, Integer userId);
}
