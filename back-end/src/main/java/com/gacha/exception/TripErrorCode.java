package com.gacha.exception;

import org.springframework.http.HttpStatus;

import com.gacha.global.exception.code.BaseErrorCode;
import com.gacha.model.dto.response.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TripErrorCode implements BaseErrorCode {
	
	INVALID_SPOT_CATEGORY(HttpStatus.BAD_REQUEST, "400", "허용되지 않은 관광지 카테고리입니다."),
	SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "관광지를 찾을 수 없습니다.");
	
    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> Response<T> getResponse() {
        return null;
    }
}
