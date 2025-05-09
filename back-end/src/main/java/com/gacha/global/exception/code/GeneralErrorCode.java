package com.gacha.global.exception.code;

import org.springframework.http.HttpStatus;

import com.gacha.model.dto.response.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {
	
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "일시적인 서버 에러입니다. 잠시 후 다시 시도해주세요.");
	
    private final HttpStatus status;
    private final String code;
    private final String message;
    
    @Override
    public <T> Response<T> getResponse() {
        return Response.onFailure(this.status, this.code, this.message, null);
    }
}
