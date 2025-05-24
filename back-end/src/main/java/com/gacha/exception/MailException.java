package com.gacha.exception;

import com.gacha.global.exception.CustomException;
import com.gacha.global.exception.code.BaseErrorCode;

public class MailException extends CustomException {
	public MailException(BaseErrorCode code) {
		super(code);
	}
}
