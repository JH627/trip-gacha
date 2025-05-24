package com.gacha.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.api.Response;
import com.gacha.global.jwt.annotation.LoginUser;
import com.gacha.model.dto.chat.ChatRequest.ChatDetail;
import com.gacha.model.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "채팅 도메인 API")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
	
    private final ChatService chatService;

    @Operation(summary = "AI 채팅기능", description = "사용자가 입력한 문장을 기반으로 응답을 생성하여 반환합니다.<br/>"
    		+ "채팅 컨텍스트는 마지막 채팅으로부터 30분간 유효하며 이후에는 사라집니다.")
    @PostMapping
    public Response<String> chat(@LoginUser Integer userId, @RequestBody ChatDetail message) {
        String response = chatService.chat(userId, message);
        return Response.onSuccess(response);
    }
}


