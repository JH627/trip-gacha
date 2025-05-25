package com.gacha.exception;

import org.springframework.http.HttpStatus;

import com.gacha.global.api.Response;
import com.gacha.global.exception.code.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailErrorCode implements BaseErrorCode {
	
    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "400", "인증코드가 일치하지 않습니다."),
    MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "500", "메일 발송에 실패했습니다."),
    AUTH_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "404", "해당 이메일에 대한 인증 코드가 존재하지 않습니다."),
    MAIL_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "500", "메일 생성 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> Response<T> getResponse() {
        return null;
    }
}