package com.gacha.model.service;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gacha.exception.BoardErrorCode;
import com.gacha.exception.BoardException;
import com.gacha.model.dao.BoardDao;
import com.gacha.model.dto.board.AddBoardRequest;
import com.gacha.model.dto.board.AddCommentRequest;
import com.gacha.model.dto.board.BoardDetail;
import com.gacha.model.dto.board.BoardDto;
import com.gacha.model.dto.board.BoardHeader;
import com.gacha.model.dto.board.CommentDetail;
import com.gacha.model.dto.board.GetCommentsRequest;
import com.gacha.model.dto.board.SearchBoardCondition;
import com.gacha.model.dto.board.UpdateBoardRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.gacha.model.dto.board.BoardSearchResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
	
    private final BoardDao boardDao;

    @Override
    @Transactional(readOnly = true)
    public BoardSearchResponse searchByCondition(SearchBoardCondition condition) {
        condition.setPage(condition.getPage() > 0 ? (condition.getPage() - 1) * condition.getOffset() : 0);
        
        List<BoardHeader> boardList = boardDao.selectByCondition(condition);
        int totalCount = boardDao.countByCondition(condition);
            
        return BoardSearchResponse.builder()
            .boards(boardList)
            .currentPage(condition.getPage() / condition.getOffset() + 1)
            .totalCount(totalCount)
            .build();
    }

    @Override
    @Transactional
    public BoardDetail searchById(Integer userId, Integer boardId) {
        BoardDetail boardInfo = boardDao.selectById(userId, boardId);
        // 조회수 + 1
        boardDao.updateViewCount(userId, boardId);
        boardInfo.setViewCount(boardInfo.getViewCount() + 1);
        return boardInfo;
    }   

    @Override
    @Transactional
    public void createBoard(AddBoardRequest dto, Integer userId) {
        BoardDto boardDto = BoardDto.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .build();
        
        boardDao.insert(boardDto, userId);
    }

    @Override
    @Transactional
    public void updateById(UpdateBoardRequest dto, Integer userId) {
        BoardDto boardDto = BoardDto
                .builder()
                .boardId(dto.getBoardId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();    	
        boardDao.updateById(boardDto, userId);
    }

    @Override
    @Transactional
    public void removeById(Integer boardId, Integer userId) {
        boardDao.deleteById(boardId, userId);
    }

    @Override
    @Transactional
    public void report(Integer boardId, Integer userId) {
        try {
            boardDao.report(boardId, userId);
        } catch(DuplicateKeyException e) {
        	// 중복 신고
            throw new BoardException(BoardErrorCode.DUPLICATED_REPORT);
        }
    }

    @Override
    @Transactional
    public void like(Integer boardId, Integer userId) {
        try{
            boardDao.like(boardId, userId);
        }catch(DuplicateKeyException e){
            boardDao.deleteLike(boardId, userId);
        }
    }

    @Override
    @Transactional
    public void dislike(Integer boardId, Integer userId) {
        try{
            boardDao.dislike(boardId, userId);
        }catch(DuplicateKeyException e){
            boardDao.deleteDislike(boardId, userId);
        }
    }

    @Override
    @Transactional
    public List<CommentDetail> searchCommentsById(GetCommentsRequest getCommentsRequest, Integer userId) {
    	return boardDao.selectCommentsById(getCommentsRequest, userId);
    }

    @Override
    public void createComment(AddCommentRequest addCommentRequest, Integer userId) {
    	boardDao.insertComment(addCommentRequest, userId);
    }

    @Override
    public void removeCommentById(Integer commentId, Integer userId) {
        boardDao.deleteComment(commentId, userId);
    }

    @Override
    public void reportComment(Integer commentId, Integer userId) {
        try{
            boardDao.reportComment(commentId, userId);
        }catch(DuplicateKeyException e){
            throw new BoardException(BoardErrorCode.DUPLICATED_REPORT);
        }
    }
}
