package com.gacha.global.jwt.resovler;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.gacha.global.jwt.annotation.LoginUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class) 
                && parameter.getParameterType().isAssignableFrom(Integer.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    	// 컨텍스트 인증 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            // userId 반환
            if (principal instanceof Integer) {
                return principal;
            }
        }
        return null;
    }
}