package com.gacha.exception;

import com.gacha.global.exception.CustomException;

public class TripException extends CustomException {
    public TripException(TripErrorCode code) {
        super(code);
    }
}
