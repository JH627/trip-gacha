package com.gacha.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gacha.model.dto.board.AddCommentRequest;
import com.gacha.model.dto.board.BoardDetail;
import com.gacha.model.dto.board.BoardDto;
import com.gacha.model.dto.board.BoardHeader;
import com.gacha.model.dto.board.CommentDetail;
import com.gacha.model.dto.board.GetCommentsRequest;
import com.gacha.model.dto.board.SearchBoardCondition;

@Mapper
public interface BoardDao {
    // 게시글 리스트 검색
    List<BoardHeader> selectByCondition(SearchBoardCondition searchBoardCondition);
    
    // 전체 게시글 개수 조회
    int countByCondition(SearchBoardCondition searchBoardCondition);
    
    // 게시글 조회
    BoardDetail selectById(Integer userId, Integer boardId);

    // 게시글 생성
    void insert(BoardDto boardDto, Integer userId);

    // 게시글 조회수 증가
    void updateViewCount(Integer userId, Integer boardId);

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

    void deleteLike(Integer boardId, Integer userId);

    void deleteDislike(Integer boardId, Integer userId);

    // 좋아요 상태 확인
    boolean isLiked(Integer boardId, Integer userId);

    // 싫어요 상태 확인
    boolean isDisliked(Integer boardId, Integer userId);

    List<CommentDetail> selectCommentsById(GetCommentsRequest getCommentsRequest, Integer userId);

    void insertComment(AddCommentRequest addCommentRequest, Integer userId);

    void deleteComment(Integer commentId, Integer userId);

    void reportComment(Integer commentId, Integer userId);
}
