package com.gacha.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.api.Response;
import com.gacha.global.jwt.annotation.LoginUser;
import com.gacha.model.dto.user.FullUserInfo;
import com.gacha.model.dto.user.RegistRequest;
import com.gacha.model.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원 도메인 API")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	
    private final UserService userService;

    @PostMapping("/regist")
    public ResponseEntity<Response<?>> regist(@ModelAttribute RegistRequest registRequest){
        if(!userService.regist(registRequest)){
            return ResponseEntity
                    .badRequest()
                    .body(Response.onFailure(HttpStatus.BAD_REQUEST, "400", "이미 가입한 이메일입니다.", null));
        }
        
    	return ResponseEntity.ok().body(Response.onSuccess());
    }
    
    @GetMapping("")
    public ResponseEntity<?> getUserInfo(@LoginUser Integer userId) {
        // cookie에 있는 jwt를 까서 거기에 있는 user_id로 사용자 정보 가져오기
        FullUserInfo fullUserInfo = userService.searchUserInfo(String.valueOf(userId));
        
        if(fullUserInfo == null){
            return ResponseEntity
            .badRequest()
            .body(Response.onFailure(HttpStatus.BAD_REQUEST, "400", "존재하지 않는 사용자", null));  
        }

        return ResponseEntity.ok(Response.onSuccess(fullUserInfo));
    }    
}
