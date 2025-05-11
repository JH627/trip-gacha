package com.gacha.exception;

import org.springframework.http.HttpStatus;

import com.gacha.global.exception.code.BaseErrorCode;
import com.gacha.model.dto.response.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TripErrorCode implements BaseErrorCode {
	
	SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "관광지를 찾을 수 없습니다.");
	
    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public <T> Response<T> getResponse() {
        return null;
    }
}
