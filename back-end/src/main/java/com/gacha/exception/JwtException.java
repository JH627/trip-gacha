package com.gacha.exception;

import com.gacha.global.exception.CustomException;
import com.gacha.global.exception.code.BaseErrorCode;

public class JwtException extends CustomException {
	public JwtException(BaseErrorCode code) {
		super(code);
	}
}
