package com.gacha.global.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gacha.global.exception.CustomException;
import com.gacha.global.exception.code.BaseErrorCode;
import com.gacha.global.exception.code.GeneralErrorCode;
import com.gacha.model.dto.response.Response;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response<String>> customException(CustomException e) {
        BaseErrorCode code = e.getCode();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Response.onFailure(code.getStatus(), code.getCode(), code.getMessage(), null)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<String>> exception(Exception e) {
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        		Response.onFailure(code.getStatus(), code.getCode(), code.getMessage(), null)
        );
    }

}