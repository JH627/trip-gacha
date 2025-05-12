package com.gacha.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ChatRequest {
	
	/**
	 * 찜할 관광지 ID
	 */
	@Getter
    public static class ChatDetail {
		@NotNull
        private String content;
    }
	
}
