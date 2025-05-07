package com.gacha.model.dto.response;

public class LoginResponse extends Response<Object> {
    public LoginResponse(){
        super();
    }

    public LoginResponse(String status, String code, String message, Object obj){
        super(status, code, message, obj);
    }
}
