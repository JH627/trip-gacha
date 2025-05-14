package com.gacha.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.api.Response;
import com.gacha.model.dto.chat.ChatRequest.ChatDetail;
import com.gacha.model.service.ChatService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "채팅 도메인 API")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
	
    private final ChatService chatService;

    @PostMapping
    public Response<String> chat(@RequestAttribute("userId") Integer userId, @RequestBody ChatDetail message) {
        String response = chatService.chat(userId, message);
        return Response.onSuccess(response);
    }
}


