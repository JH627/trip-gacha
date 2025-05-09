package com.gacha.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.exception.code.GeneralSuccessCode;
import com.gacha.model.dto.request.LoginRequest;
import com.gacha.model.dto.request.RegistRequest;
import com.gacha.model.dto.response.LoginResponse;
import com.gacha.model.dto.response.LogoutResponse;
import com.gacha.model.dto.response.RegistResponse;
import com.gacha.model.dto.response.Response;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("/regist")
    public Response<?> regist(@ModelAttribute RegistRequest registRequest){
        // TODO : service, dao 추가 + 회원 가입 로직 추가
        //return ResponseEntity.ok().body(new RegistResponse("ok", "200", "가입 성공", null));
    	return Response.onSuccess(null);
    }

    @PostMapping("/login")
    public Response<?> login(@ModelAttribute LoginRequest loginRequest){
        // TODO : service, dao 추가 + 로그인 로직 추가
        return Response.onSuccess(null);
    }

    @PostMapping("/logout")
    public Response<?> logout(){
        // TODO : service, dao 추가 + 로그아웃 로직 추가
        return Response.onSuccess(null);
    }
}
