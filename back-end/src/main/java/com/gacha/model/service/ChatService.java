package com.gacha.model.service;

import com.gacha.model.dto.chat.ChatRequest.ChatDetail;

public interface ChatService {
	
	/**
	 * AI에게 채팅을 보내고 응답을 반환합니다.
	 *  
	 * @param userId 사용자ID
	 * @param userInput 입력한 채팅
	 * @return AI응답
	 */
    String chat(Integer userId, ChatDetail userInput);
}
