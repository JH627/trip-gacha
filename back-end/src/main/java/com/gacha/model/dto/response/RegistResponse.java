package com.gacha.model.dto.response;

public class RegistResponse extends Response<Object> {
    public RegistResponse(){
        super();
    }

    public RegistResponse(String status, String code, String message, Object obj){
        super(status, code, message, obj);
    }
}
