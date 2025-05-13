package com.gacha.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gacha.model.dao.BoardDao;
import com.gacha.model.dto.board.BoardDto;
import com.gacha.model.dto.request.SearchBoardCondition;

@Service
public class BoardServiceImpl implements BoardService {
    @Autowired
    private BoardDao boardDao;

    @Override
    public List<BoardDto> searchByCondition(SearchBoardCondition searchBoardCondition) {
        List<BoardDto> boardList = new ArrayList<>();

        return boardList;
    }

    @Override
    public BoardDto searchById(Integer boardId) {
        return null;
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
