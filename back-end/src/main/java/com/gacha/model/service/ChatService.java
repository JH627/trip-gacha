package com.gacha.model.service;

import com.gacha.model.dto.chat.ChatRequest.ChatDetail;

public interface ChatService {
    String chat(Integer userId, ChatDetail userInput);
}
