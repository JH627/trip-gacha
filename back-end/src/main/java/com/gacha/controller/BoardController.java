package com.gacha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.api.Response;
import com.gacha.global.jwt.annotation.LoginUser;
import com.gacha.model.dto.board.AddBoardRequest;
import com.gacha.model.dto.board.BoardDto;
import com.gacha.model.dto.board.BoardHeader;
import com.gacha.model.dto.board.SearchBoardCondition;
import com.gacha.model.service.BoardService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "게시판 도메인 API")
@RestController
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @PostMapping("")
    public ResponseEntity<Response<?>> addBoard(@LoginUser Integer userId, @ModelAttribute AddBoardRequest addBoardRequest) {
        System.out.println(addBoardRequest);
        
        BoardDto boardDto = BoardDto.builder()
                                    .title(addBoardRequest.getTitle())
                                    .content(addBoardRequest.getContent())
                                    .category(addBoardRequest.getCategory())
                                    .build();
        
        System.out.println(boardDto);
        
        boardService.createBoard(boardDto, userId);
        
        return ResponseEntity.ok(Response.onSuccess());
    }
    
    @GetMapping("")
    public ResponseEntity<Response<?>> getMethodName(@ModelAttribute SearchBoardCondition condition) {
        List<BoardHeader> boardHeaderList = boardService.searchByCondition(condition); 
        
        System.out.println(boardHeaderList);

        return ResponseEntity.ok(Response.onSuccess(boardHeaderList));
    }
    
}
