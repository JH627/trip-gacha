package com.gacha.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gacha.model.dao.BoardDao;
import com.gacha.model.dto.board.BoardDetail;
import com.gacha.model.dto.board.BoardDto;
import com.gacha.model.dto.board.BoardHeader;
import com.gacha.model.dto.board.SearchBoardCondition;

@Service
public class BoardServiceImpl implements BoardService {
    @Autowired
    private BoardDao boardDao;

    @Override
    public List<BoardHeader> searchByCondition(SearchBoardCondition condition) {
        try {
            condition.setPage(condition.getPage() > 0 ? (condition.getPage() - 1) * condition.getOffset() : 0);
            System.out.println(condition);
            List<BoardHeader> boardList = boardDao.selectByCondition(condition);
            return boardList;
        } catch(Exception e){
            System.out.println("게시글 조회 중 버그남 ㅎㅎ;");
            throw e;
        }
    }

    @Override
    public BoardDetail searchById(Integer userId, Integer boardId) {
        try{
            BoardDetail boardInfo = boardDao.selectById(userId, boardId);
            boardDao.updateViewCount(userId, boardId);
            boardInfo.setViewCount(boardInfo.getViewCount()+1);
            return boardInfo;
        }catch(Exception e){
            throw e;
        }
    }   

    @Override
    public void createBoard(BoardDto boardDto, Integer userId) {
        try{
            boardDao.insert(boardDto, userId);
        } catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void updateById(BoardDto boardDto, Integer userId) {
        return;
    }

    @Override
    public void removeById(Integer boardId, Integer userId) {
        return;
    }

    @Override
    public void report(Integer boardId, Integer userId) {
        return;
    }

    @Override
    public void like(Integer boardId, Integer userId) {
        return;
    }

    @Override
    public void dislike(Integer boardId, Integer userId) {
        return;
    }    
}
