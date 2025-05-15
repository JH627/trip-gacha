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
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "401", "토큰이 유효하지 않습니다."),
	EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "401", "액세스 토큰이 만료되었습니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "401", "리프레시 토큰이 만료되었습니다."),
	MALFORMED_TOKEN(HttpStatus.UNAUTHORIZED, "401", "잘못된 형식의 토큰입니다."),
	INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "401", "유효하지 않은 토큰 서명입니다."),
	UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "401", "지원하지 않는 토큰입니다."),
	BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "401", "블랙리스트에 등록된 토큰입니다.");
	
    private final HttpStatus status;
    private final String code;
    private final String message;
    
    @Override
    public <T> Response<T> getResponse() {
        return null;
    }
}
