package com.gacha.global.exception.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GeneralSuccessCode implements BaseSuccessCode {
	
    OK(HttpStatus.OK, "200", "요청 성공"),
    CREATE(HttpStatus.CREATED, "201", "생성 성공");
	
    private final HttpStatus status;
    private final String code;
    private final String message;
}
