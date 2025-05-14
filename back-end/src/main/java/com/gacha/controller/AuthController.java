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
    private static final boolean SECURE = true;

    @PostMapping("/login")
    public ResponseEntity<Response<?>> login(@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse = authService.login(loginRequest);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.getAccessToken());

        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, loginResponse.getRefreshToken())
                .maxAge(REFRESH_TOKEN_DURATION)
                .sameSite("Lax")
                .httpOnly(HTTP_ONLY)
                .secure(false)
                .path(COOKIE_PATH)
                .build();

        httpHeaders.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<?>> logout(
    		@RequestHeader(value = "Authorization", required = false) String authorization,
    		@CookieValue("refresh_token") String refreshToken){
    	
        authService.logout(authorization, refreshToken);

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
    
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue("refresh_token") String refreshToken) {

        String newAccessToken = authService.reGenerateAccessToken(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .build();
    }

    
    
}
