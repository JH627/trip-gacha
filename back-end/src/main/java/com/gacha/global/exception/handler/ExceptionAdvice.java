package com.gacha.global.exception.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.gacha.global.exception.CustomException;
import com.gacha.global.exception.code.BaseErrorCode;
import com.gacha.global.exception.code.GeneralErrorCode;
import com.gacha.model.dto.response.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

	// Custom Error Handling
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response<String>> handleCustomException(CustomException e) {
        BaseErrorCode code = e.getCode();
        log.warn("[CustomException] code: {}, message: {}", code.getCode(), code.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.onFailure(code.getStatus(), code.getCode(), code.getMessage(), null));
    }

    // RequestBody Binding Handling
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        
        BaseErrorCode code = GeneralErrorCode.INVALID_INPUT_VALUE;
        log.error("[ValidationException] Invalid input: {}", errors);
        // 실서비스 시 result parameter 제거
        return ResponseEntity.badRequest()
                .body(Response.onFailure(code.getStatus(), code.getCode(), code.getMessage(), errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
    	log.error("[HttpMessageNotReadable] message: {}", e.getMessage());
    	
        BaseErrorCode code = GeneralErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity.badRequest()
                .body(Response.onFailure(code.getStatus(), code.getCode(), "잘못된 요청 형식입니다.", null));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Response<String>> handleBindException(BindException e) {
        BaseErrorCode code = GeneralErrorCode.INVALID_INPUT_VALUE;
        log.error("[BindException] message: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(Response.onFailure(code.getStatus(), code.getCode(), "요청 바인딩 실패", null));
    }
    
    // RequestParam, PathVariable annotation error handling
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<String>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName(); // 파라미터 이름
        String invalidValue = e.getValue() != null ? e.getValue().toString() : "null";
        String message = String.format("요청 파라미터 '%s'의 값 '%s'는 허용되지 않습니다.", paramName, invalidValue);

        BaseErrorCode code = GeneralErrorCode.INVALID_INPUT_VALUE;
        log.error("[MethodArgumentTypeMismatch] {}", message);

        return ResponseEntity.badRequest()
                .body(Response.onFailure(code.getStatus(), code.getCode(), message, null));
    }

    // Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<String>> handleException(Exception e) {
    	log.error("[UnhandledException] message: {}", e.getMessage());
        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.onFailure(code.getStatus(), code.getCode(), code.getMessage(), null));
    }
}
