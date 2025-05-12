package com.gacha.config;

import java.time.Duration;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class ChatMemoryRegistry {

    private final Cache<Integer, ChatMemory> memoryCache;

    public ChatMemoryRegistry() {
        this.memoryCache = Caffeine.newBuilder() // 최대 엔트리 수 제한
            .maximumSize(1000) // 마지막 접근 후 30분 이후에 만료
            .expireAfterAccess(Duration.ofMinutes(30)) // 캐시 통계 활성화(필요시)
            .recordStats()
            .build();
    }

    public ChatMemory getMemory(Integer userId) {
        return memoryCache.get(userId, id -> new InMemoryChatMemory());
    }
}

