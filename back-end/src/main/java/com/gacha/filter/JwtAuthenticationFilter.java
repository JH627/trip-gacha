package com.gacha.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gacha.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final List<String> excludedUrls = List.of(
        "/error",
        "/api/user/login", 
        "/api/user/regist", 
        "/api/email/verification", 
        "/api/email/verification-confirm"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain)
                                throws ServletException, IOException {
        String path = request.getRequestURI();
        // 로그인 필요 없는 경로는 그냥 통과
        if (excludedUrls.contains(path)) {
            System.out.println("cut");
            filterChain.doFilter(request, response);
            return;
        }
                                        
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            System.out.println("없음");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "인증이 필요합니다.");
            return;
        }

        Optional<Cookie> jwtCookie = Arrays.stream(cookies)
            .filter(cookie -> "jwt".equals(cookie.getName()))
            .findFirst();

        if (jwtCookie.isEmpty()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "JWT 쿠키가 없습니다.");
            return;
        }

        System.out.println(jwtCookie.get().getValue());

        try {
            String jwt = jwtCookie.get().getValue();
            String userId = JwtUtil.getSubject(jwt); // JWT 디코딩
            UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.setAttribute("userId", userId);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "JWT 검증 실패");
        }
    }
}
