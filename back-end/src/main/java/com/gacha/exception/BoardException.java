package com.gacha.exception;

import com.gacha.global.exception.CustomException;

public class BoardException extends CustomException {
    public BoardException(BoardErrorCode code) {
        super(code);
    }
}
