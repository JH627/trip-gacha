package com.gacha.model.service;

import com.gacha.model.dto.request.ChatRequest.ChatDetail;

public interface ChatService {
    String chat(Integer userId, ChatDetail userInput);
}
