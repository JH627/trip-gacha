package com.gacha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.api.Response;
import com.gacha.global.jwt.annotation.LoginUser;
import com.gacha.model.dto.board.AddBoardRequest;
import com.gacha.model.dto.board.BoardDetail;
import com.gacha.model.dto.board.BoardDto;
import com.gacha.model.dto.board.BoardHeader;
import com.gacha.model.dto.board.SearchBoardCondition;
import com.gacha.model.dto.board.UpdateBoardRequest;
import com.gacha.model.service.BoardService;
import com.gacha.util.StringValidator;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    
    @GetMapping("s")
    public ResponseEntity<Response<?>> searchBoards(@ModelAttribute SearchBoardCondition condition) {
        List<BoardHeader> boardHeaderList = boardService.searchByCondition(condition); 
        
        System.out.println(boardHeaderList);

        return ResponseEntity.ok(Response.onSuccess(boardHeaderList));
    }
    
    @GetMapping("/{boardId}")
    public ResponseEntity<Response<?>> searchBoard(@LoginUser Integer userId, @PathVariable Integer boardId) {
        BoardDetail boardInfo = boardService.searchById(userId, boardId);

        return ResponseEntity.ok(Response.onSuccess(boardInfo));
    }

    @PatchMapping("")
    public ResponseEntity<Response<?>> updateBoard(@RequestBody UpdateBoardRequest updateBoardRequest, @LoginUser Integer userId) {        
        if(StringValidator.isEmpty(updateBoardRequest.getTitle(), updateBoardRequest.getContent())){
            return ResponseEntity.ok(Response.onFailure(HttpStatus.BAD_REQUEST, "400", "제대로 입력 부탁", null));
        }
        
        BoardDto boardDto = BoardDto
                                .builder()
                                .boardId(updateBoardRequest.getBoardId())
                                .title(updateBoardRequest.getTitle())
                                .content(updateBoardRequest.getContent())
                                .build();
        
        boardService.updateById(boardDto, userId);

        return ResponseEntity.ok(Response.onSuccess());
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Response<?>> deleteBoard(@LoginUser Integer userId, @PathVariable Integer boardId) {        
        boardService.removeById(boardId, userId);

        return ResponseEntity.ok(Response.onSuccess());
    }

    @PostMapping("/report")
    public ResponseEntity<Response<?>> reportBoard(@LoginUser Integer userId, @RequestParam("boardId") Integer boardId) {
        boardService.report(boardId, userId);
        
        return ResponseEntity.ok(Response.onSuccess());
    }

    @PostMapping("/like")
    public ResponseEntity<Response<?>> likeBoard(@LoginUser Integer userId, @RequestParam("boardId") Integer boardId) {
        boardService.like(boardId, userId);
        
        return ResponseEntity.ok(Response.onSuccess());
    }

    @PostMapping("/dislike")
    public ResponseEntity<Response<?>> dislikeBoard(@LoginUser Integer userId, @RequestParam("boardId") Integer boardId) {
        boardService.dislike(boardId, userId);
        
        return ResponseEntity.ok(Response.onSuccess());
    }
}
