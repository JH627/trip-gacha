package com.gacha.model.service;

import java.util.List;

import com.gacha.model.dto.board.AddBoardRequest;
import com.gacha.model.dto.board.AddCommentRequest;
import com.gacha.model.dto.board.BoardDetail;
import com.gacha.model.dto.board.CommentDetail;
import com.gacha.model.dto.board.GetCommentsRequest;
import com.gacha.model.dto.board.SearchBoardCondition;
import com.gacha.model.dto.board.UpdateBoardRequest;
import com.gacha.model.dto.board.BoardSearchResponse;

public interface BoardService {
    /**
     * 검색 조건에 맞는 게시글 목록을 반환합니다.
     * 
     * @param searchBoardCondition 검색 조건(페이지, 정렬, 카테고리 등)
     * @return 게시글 목록과 전체 개수를 포함한 응답
     */
    BoardSearchResponse searchByCondition(SearchBoardCondition searchBoardCondition);
    
    /**
     * 게시글 상세 정보를 조회합니다.
     * 
     * @param userId 사용자ID
     * @param boardId 게시글ID
     * @return 게시글 상세 정보
     */
    BoardDetail searchById(Integer userId, Integer boardId);

    /**
     * 새로운 게시글을 생성합니다.
     * 
     * @param boardDto 게시글 정보(제목, 내용, 카테고리)
     * @param userId 사용자ID
     */
    void createBoard(AddBoardRequest boardDto, Integer userId);

    /**
     * 게시글을 수정합니다.
     * 
     * @param boardDto 수정할 게시글 정보(게시글ID, 제목, 내용)
     * @param userId 사용자ID
     */
    void updateById(UpdateBoardRequest boardDto, Integer userId);

    /**
     * 게시글을 삭제합니다.
     * 
     * @param boardId 게시글ID
     * @param userId 사용자ID
     */
    void removeById(Integer boardId, Integer userId);

    /**
     * 게시글을 신고합니다.
     * 
     * @param boardId 게시글ID
     * @param userId 사용자ID
     */
    void report(Integer boardId, Integer userId);

    /**
     * 게시글에 좋아요를 추가하거나 취소합니다.
     * 
     * @param boardId 게시글ID
     * @param userId 사용자ID
     */
    void like(Integer boardId, Integer userId);

    /**
     * 게시글에 싫어요를 추가하거나 취소합니다.
     * 
     * @param boardId 게시글ID
     * @param userId 사용자ID
     */
    void dislike(Integer boardId, Integer userId);

    /**
     * 게시글의 댓글 목록을 조회합니다.
     * 
     * @param getCommentsRequest 댓글 조회 조건(게시글ID, 페이지 등)
     * @param userId 사용자ID
     * @return 댓글 목록
     */
    List<CommentDetail> searchCommentsById(GetCommentsRequest getCommentsRequest, Integer userId);

    /**
     * 게시글에 새로운 댓글을 작성합니다.
     * 
     * @param addCommentRequest 댓글 정보(게시글ID, 내용)
     * @param userId 사용자ID
     */
    void createComment(AddCommentRequest addCommentRequest, Integer userId);

    /**
     * 댓글을 삭제합니다.
     * 
     * @param commentId 댓글ID
     * @param userId 사용자ID
     */
    void removeCommentById(Integer commentId, Integer userId);

    /**
     * 댓글을 신고합니다.
     * 
     * @param commentId 댓글ID
     * @param userId 사용자ID
     */
    void reportComment(Integer commentId, Integer userId);
}
