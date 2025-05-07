package com.gacha.model.dto.response;

public class LogoutResponse extends Response<Object> {
    public LogoutResponse(){
        super();
    }

    public LogoutResponse(String status, String code, String message, Object obj){
        super(status, code, message, obj);
    }
}
