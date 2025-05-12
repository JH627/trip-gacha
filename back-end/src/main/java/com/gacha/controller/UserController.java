package com.gacha.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.model.dto.request.LoginRequest;
import com.gacha.model.dto.request.RegistRequest;
import com.gacha.model.dto.response.Response;
import com.gacha.model.dto.user.FullUserInfo;
import com.gacha.model.dto.user.UserDto;
import com.gacha.model.dto.user.UserInfo;
import com.gacha.model.service.UserService;
import com.gacha.util.JwtUtil;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/regist")
    public ResponseEntity<Response<?>> regist(@ModelAttribute RegistRequest registRequest){
        if(!userService.regist(registRequest)){
            return ResponseEntity
                    .badRequest()
                    .body(Response.onFailure(HttpStatus.BAD_REQUEST, "400", "이미 가입한 이메일입니다.", null));
        }
        
    	return ResponseEntity.ok().body(Response.onSuccess());
    }

    @PostMapping("/login")
    public ResponseEntity<Response<?>> login(@RequestBody LoginRequest loginRequest){
        System.out.println(loginRequest.getEmail());
        
        UserDto findUser = userService.login(loginRequest);

        if(findUser == null){
            return ResponseEntity
                .badRequest()
                .body(Response.onFailure(HttpStatus.BAD_REQUEST, "400", "로그인 실패", null));
        }

        String jwt = JwtUtil.generateToken(findUser.getUserId().toString(), 60 * 60 * 24 * 14 * 1000);
        
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofDays(14))
            .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Response.onSuccess(new UserInfo(findUser.getEmail(), findUser.getNickname())));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<?>> logout(){
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.ofDays(0))
            .build();

        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(Response.onSuccess());
    }

    @GetMapping("")
    public ResponseEntity<?> getUserInfo(@RequestAttribute("userId") String userId) {
        // cookie에 있는 jwt를 까서 거기에 있는 user_id로 사용자 정보 가져오기
        FullUserInfo fullUserInfo = userService.searchUserInfo(userId);
        
        if(fullUserInfo == null){
            return ResponseEntity
            .badRequest()
            .body(Response.onFailure(HttpStatus.BAD_REQUEST, "400", "존재하지 않는 사용자", null));  
        }

        return ResponseEntity.ok(Response.onSuccess(fullUserInfo));
    }    
}
