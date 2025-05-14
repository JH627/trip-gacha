package com.gacha.model.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.gacha.config.ChatClientFactory;
import com.gacha.model.dto.chat.ChatRequest.ChatDetail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClientFactory clientFactory;

    @Override
    public String chat(Integer userId, ChatDetail userInput) {
        ChatClient client = clientFactory.getClientFor(userId);
        return client.prompt()
                     .system(c -> c.param("language", "Korean")
                                   .param("character", "친절한"))
                     .user(userInput.getContent())
                     .call()
                     .content();
    }
}
