package com.gacha.global.api;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gacha.global.exception.code.BaseSuccessCode;
import com.gacha.global.exception.code.GeneralSuccessCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"status", "code", "message", "result"})
@ToString
public class Response<T> {
    private HttpStatus status;
    private String code;
    private String message;
    private T result;
    
    // 일반 응답 코드
    public static <T> Response<T> onSuccess() {
        return new Response<>(GeneralSuccessCode.OK.getStatus(), GeneralSuccessCode.OK.getCode(), GeneralSuccessCode.OK.getMessage(), null);
    }
    
    public static <T> Response<T> onSuccess(T result) {
        return new Response<>(GeneralSuccessCode.OK.getStatus(), GeneralSuccessCode.OK.getCode(), GeneralSuccessCode.OK.getMessage(), result);
    }
    
    // 커스텀 응답 코드
    public static <T> Response<T> onSuccess(BaseSuccessCode code) {
        return new Response<>(code.getStatus(), code.getCode(), code.getMessage(), null);
    }
    
    public static <T> Response<T> onSuccess(BaseSuccessCode code, T result) {
    	return new Response<>(code.getStatus(), code.getCode(), code.getMessage(), result);
    }

    // 실패 응답 코드
    public static <T> Response<T> onFailure(HttpStatus status, String code, String message, T result) {
        return new Response<>(status, code, message, result);
    }
}
