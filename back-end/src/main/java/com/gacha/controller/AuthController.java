package com.gacha.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gacha.global.api.Response;
import com.gacha.model.dto.user.LoginRequest;
import com.gacha.model.dto.user.LoginResponse;
import com.gacha.model.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "인증 도메인 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);
    private static final String COOKIE_PATH = "/";
    private static final boolean HTTP_ONLY = true;
    private static final boolean SECURE = false;

    @Operation(summary = "로그인", description = "아이디와 비밀번호를 기반으로 로그인을 진행합니다.<br/>"
    		+ "로그인 성공시 헤더에 AccessToken이 반환되고, Cookie에 RefreshToken이 반환됩니다.")
    @PostMapping("/login")
    public ResponseEntity<Response<?>> login(@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse = authService.login(loginRequest);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.getAccessToken());

        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, loginResponse.getRefreshToken())
                .maxAge(REFRESH_TOKEN_DURATION)
                .sameSite("Lax")
                .httpOnly(HTTP_ONLY)
                .secure(SECURE)
                .path(COOKIE_PATH)
                .build();

        httpHeaders.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .build();
    }
    
    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.<br/>"
    		+ "전달된 토큰은 블랙리스트에 등록됩니다.")
    @PostMapping("/logout")
    public ResponseEntity<Response<?>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {
        
        if (authorization != null || refreshToken != null) {
            authService.logout(authorization, refreshToken);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .maxAge(0)
                .httpOnly(HTTP_ONLY)
                .secure(SECURE)
                .path(COOKIE_PATH)
                .build();

        httpHeaders.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
        return ResponseEntity.noContent()
                .headers(httpHeaders)
                .build();
    }
    
    @Operation(summary = "리프레시 토큰 재발급", description = "RefreshToken을 기반으로 AccessToken을 재발급합니다.")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {

        String newAccessToken = authService.reGenerateAccessToken(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .build();
    }

    
    
}
