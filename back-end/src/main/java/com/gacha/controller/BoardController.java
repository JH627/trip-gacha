package com.gacha.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.api.Response;
import com.gacha.global.jwt.annotation.LoginUser;
import com.gacha.model.dto.board.AddBoardRequest;
import com.gacha.model.dto.board.AddCommentRequest;
import com.gacha.model.dto.board.BoardDetail;
import com.gacha.model.dto.board.CommentDetail;
import com.gacha.model.dto.board.GetCommentsRequest;
import com.gacha.model.dto.board.SearchBoardCondition;
import com.gacha.model.dto.board.UpdateBoardRequest;
import com.gacha.model.service.BoardService;
import com.gacha.model.dto.board.BoardSearchResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시글 등록", description = "사용자 입력을 기반으로 게시글을 등록합니다.")
    @PostMapping("")
    public Response<?> addBoard(@LoginUser Integer userId, @Valid @RequestBody AddBoardRequest addBoardRequest) {
        boardService.createBoard(addBoardRequest, userId);
        return Response.onSuccess();
    }

    @Operation(summary = "게시글 검색", description = "키워드, 정렬 기준, 검색 조건을 통하여 게시글을 검색한 후 반환합니다.")
    @GetMapping("/search")
    public Response<?> searchBoards(@ModelAttribute SearchBoardCondition condition) {
        BoardSearchResponse response = boardService.searchByCondition(condition);
        return Response.onSuccess(response);
    }

    @Operation(summary = "게시글 상세 확인", description = "게시글 ID를 기반으로 게시글 내용을 반환합니다.")
    @GetMapping("/{boardId}")
    public Response<?> searchBoard(@LoginUser Integer userId, @PathVariable Integer boardId) {
        BoardDetail boardInfo = boardService.searchById(userId, boardId);
        return Response.onSuccess(boardInfo);
    }

    @Operation(summary = "게시글 수정", description = "새로 입력된 내용을 기반으로 게시글을 수정합니다.")
    @PatchMapping("")
    public Response<?> updateBoard(@LoginUser Integer userId, @Valid @RequestBody UpdateBoardRequest updateBoardRequest) {
        boardService.updateById(updateBoardRequest, userId);
        return Response.onSuccess();
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public Response<?> deleteBoard(@LoginUser Integer userId, @PathVariable Integer boardId) {
        boardService.removeById(boardId, userId);
        return Response.onSuccess();
    }

    @Operation(summary = "게시글 신고", description = "게시글을 신고합니다.")
    @PostMapping("/report")
    public Response<?> reportBoard(@LoginUser Integer userId, @RequestParam Integer boardId) {
        boardService.report(boardId, userId);
        return Response.onSuccess();
    }

    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요를 등록합니다. 이미 좋아요를 눌렀던 경우 좋아요를 취소합니다.")
    @PostMapping("/like")
    public Response<?> likeBoard(@LoginUser Integer userId, @RequestParam Integer boardId) {
        boardService.like(boardId, userId);
        return Response.onSuccess();
    }

    @Operation(summary = "게시글 싫어요", description = "게시글 싫어요를 등록합니다. 이미 싫어요를 눌렀던 경우 싫어요를 취소합니다.")
    @PostMapping("/dislike")
    public Response<?> dislikeBoard(@LoginUser Integer userId, @RequestParam Integer boardId) {
        boardService.dislike(boardId, userId);
        return Response.onSuccess();
    }

    @Operation(summary = "댓글 목록 조회", description = "게시글에 달린 댓글 목록을 가져옵니다.")
    @GetMapping("/comments")
    public Response<?> getComments(@LoginUser Integer userId, @ModelAttribute GetCommentsRequest getCommentsRequest) {
        List<CommentDetail> comments = boardService.searchCommentsById(getCommentsRequest, userId);
        return Response.onSuccess(comments);
    }

    @Operation(summary = "댓글 등록", description = "게시글에 댓글을 등록합니다.")
    @PostMapping("/comment")
    public Response<?> addComment(@LoginUser Integer userId, @Valid @RequestBody AddCommentRequest addCommentRequest) {
        boardService.createComment(addCommentRequest, userId);
        return Response.onSuccess();
    }

    @Operation(summary = "댓글 삭제", description = "게시글에 달린 댓글을 삭제합니다.<br/>"
    		+ "실제로 지워지지 않으며 이후에 조회하는 경우, 댓글이 삭제되었다는 표시와 함께 반환됩니다.")
    @DeleteMapping("comment/{commentId}")
    public Response<?> deleteComment(@LoginUser Integer userId, @PathVariable Integer commentId) {
        boardService.removeCommentById(commentId, userId);
        return Response.onSuccess();
    }

    @Operation(summary = "댓글 신고", description = "댓글을 신고합니다.")
    @PostMapping("comment/report")
    public Response<?> reportComment(@LoginUser Integer userId, @RequestParam Integer commentId) {
        boardService.reportComment(commentId, userId);
        return Response.onSuccess();
    }
}
