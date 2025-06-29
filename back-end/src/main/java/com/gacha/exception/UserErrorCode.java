package com.gacha.exception;

import org.springframework.http.HttpStatus;

import com.gacha.global.api.Response;
import com.gacha.global.exception.code.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
	
	NOT_FOUND(HttpStatus.NOT_FOUND, "404", "사용자를 찾을 수 없습니다."),
	DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "400", "이미 가입한 이메일입니다.");
	
    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> Response<T> getResponse() {
        return null;
    }
}
