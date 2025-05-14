package com.gacha.model.service;

import java.util.List;

import com.gacha.model.dto.board.BoardDetail;
import com.gacha.model.dto.board.BoardDto;
import com.gacha.model.dto.board.BoardHeader;
import com.gacha.model.dto.board.CommentDetail;
import com.gacha.model.dto.board.GetCommentsRequest;
import com.gacha.model.dto.board.SearchBoardCondition;

public interface BoardService {
    // 게시글 리스트 검색
    List<BoardHeader> searchByCondition(SearchBoardCondition searchBoardCondition);
    
    // 게시글 조회
    BoardDetail searchById(Integer userId, Integer boardId);

    // 게시글 생성
    void createBoard(BoardDto boardDto, Integer userId);

    // 게시글 수정
    void updateById(BoardDto boardDto, Integer userId);

    // 게시글 삭제
    void removeById(Integer boardId, Integer userId);

    // 게시글 신고
    void report(Integer boardId, Integer userId);

    // 게시글 좋아요
    void like(Integer boardId, Integer userId);

    // 게시글 싫어요
    void dislike(Integer boardId, Integer userId);

    List<CommentDetail> searchCommentsById(GetCommentsRequest getCommentsRequest, Integer userId);
}
