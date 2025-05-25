package com.gacha.exception;

import org.springframework.http.HttpStatus;

import com.gacha.global.api.Response;
import com.gacha.global.exception.code.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BoardErrorCode implements BaseErrorCode {
	
    DUPLICATED_REPORT(HttpStatus.BAD_REQUEST, "400", "이미 신고한 게시글입니다."),
    DUPLICATED_LIKE(HttpStatus.BAD_REQUEST, "400", "이미 좋아요를 누른 게시글입니다."),
    DUPLICATED_DISLIKE(HttpStatus.BAD_REQUEST, "400", "이미 싫어요를 누른 게시글입니다."),
	
	// 목적지 관련 예외
	DESTINATION_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "목적지를 찾을 수 없습니다.");
	
    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> Response<T> getResponse() {
        return null;
    }
}
