package com.gacha.exception;

import org.springframework.http.HttpStatus;

import com.gacha.global.api.Response;
import com.gacha.global.exception.code.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtErrorCode implements BaseErrorCode {
	
	// JWT 관련 에러
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "401", "토큰이 유효하지 않습니다.");
	
    private final HttpStatus status;
    private final String code;
    private final String message;
    
    @Override
    public <T> Response<T> getResponse() {
        return null;
    }
}
