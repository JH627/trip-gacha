package com.gacha.global.exception;

import com.gacha.global.exception.code.BaseErrorCode;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private BaseErrorCode code;

    public CustomException(BaseErrorCode code) {
        this.code = code;
    }

}
