package com.gacha.global.exception.code;

import org.springframework.http.HttpStatus;

import com.gacha.model.dto.response.Response;

public interface BaseErrorCode {
    <T> Response<T> getResponse();
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
