package com.gacha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.model.dto.request.LoginRequest;
import com.gacha.model.dto.request.RegistRequest;
import com.gacha.model.dto.response.Response;
import com.gacha.model.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/regist")
    public ResponseEntity<Response<?>> regist(@ModelAttribute RegistRequest registRequest){
        System.out.println(registRequest.getEmail());
        
        if(!userService.regist(registRequest)){
            return ResponseEntity
                    .badRequest()
                    .body(Response.onFailure(HttpStatus.BAD_REQUEST, "400", "이미 가입한 이메일입니다.", null));
        }
        
    	return ResponseEntity.ok().body(Response.onSuccess());
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
