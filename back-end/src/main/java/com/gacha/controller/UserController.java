package com.gacha.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.api.Response;
import com.gacha.global.jwt.annotation.LoginUser;
import com.gacha.model.dto.user.FullUserInfo;
import com.gacha.model.dto.user.RegistRequest;
import com.gacha.model.dto.user.UpdateRequest;
import com.gacha.model.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원 도메인 API")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	
    private final UserService userService;

    @Operation(summary = "회원 가입", description = "사용자 입력 정보를 바탕으로 회원가입을 진행합니다.")
    @PostMapping("/regist")
    public Response<?> regist(@ModelAttribute RegistRequest registRequest){
        userService.regist(registRequest);
    	return Response.onSuccess();
    }
    
    @Operation(summary = "회원 필수 정보", description = "회원 필수 정보(이메일, 닉네임, 프로필 이미지 링크)를 반환합니다.")
    @GetMapping("")
    public Response<?> getUserInfo(@LoginUser Integer userId) {
        FullUserInfo fullUserInfo = userService.searchUserInfo(userId);
        return Response.onSuccess(fullUserInfo);
    }    
    
    @Operation(summary = "회원 부가 정보", description = "회원 부가 정보(생성한 여행 일정 수, 찜한 관광지 수, 작성한 게시글 수, 서비스 이용일을 반환합니다.")
    @GetMapping("/activity-stats")
    public Response<?> getUserStats(@LoginUser Integer userId) {
    	return Response.onSuccess(userService.getUserStats(userId));
    }
    
    @Operation(summary = "회원 정보 업데이트", description = "회원 정보(닉네임, 프로필 이미지)를 업데이트합니다.")
    @PutMapping("")
    public Response<?> updateUserInfo(@LoginUser Integer userId, @ModelAttribute UpdateRequest updateRequest) {
    	userService.updateProfile(userId, updateRequest);
        return Response.onSuccess();
    }  
}
