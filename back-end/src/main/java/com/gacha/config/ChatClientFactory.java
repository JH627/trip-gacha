package com.gacha.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatClientFactory {
	
    private final ChatClient.Builder builder;
    private final ChatMemoryRegistry memoryRegistry;

    /**
     * 매 요청 시점에 userId에 맞는 ChatClient를 생성해 반환.
     */
    public ChatClient getClientFor(Integer userId) {
        ChatMemory userMemory = memoryRegistry.getMemory(userId);
        return builder
            .defaultSystem("""
                당신은 여행지를 추천해주는 인공지능입니다.
                말을 할 때는 {language}로 하고 {character} 성격으로 대답해주세요.
                """)
            .defaultAdvisors(
                new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE - 1),
                new MessageChatMemoryAdvisor(userMemory)
            )
            .build();
    }
}


