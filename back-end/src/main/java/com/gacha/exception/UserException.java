package com.gacha.exception;

import com.gacha.global.exception.CustomException;

public class UserException extends CustomException {
    public UserException(UserErrorCode code) {
        super(code);
    }
}
