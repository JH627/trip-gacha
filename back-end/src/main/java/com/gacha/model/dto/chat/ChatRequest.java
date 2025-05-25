package com.gacha.model.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class ChatRequest {
	
	/**
	 * 찜할 관광지 ID
	 */
	@Getter
    public static class ChatDetail {
		@NotBlank(message = "채팅 내용은 필수 입력값입니다.")
		@Size(min = 1, max = 1000, message = "채팅 내용은 1자 이상 1000자 이하로 입력해주세요.")
        private String content;
    }
	
}
