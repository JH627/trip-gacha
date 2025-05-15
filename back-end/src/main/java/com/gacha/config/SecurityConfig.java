package com.gacha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.gacha.global.jwt.filter.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
	private final String[] ALLOW_URL = {
			"/error",
			"/auth/login", "/auth/refresh-token", "/auth/logout",
			"/user/regist", 
			"/email/verification", "/email/verification-confirm", 
			"/swagger-ui/**", "/v3/api-docs/**"
	};
	
	private final JwtFilter jwtFilter;
    
    @Bean
	public SecurityFilterChain filterChain(HttpSecurity http, CorsConfig corsConfig) throws Exception {
		http
			// 세션 사용하지 않음
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// 허용할 URL, 역할별로 나눌 URL, 인증을 요구하는 URL 설정
			.authorizeHttpRequests(request -> request
				// allowUrl을 모두 허용
				.requestMatchers(ALLOW_URL).permitAll()
				// 이외의 요청에 대해서는 인증이 필요하도록 설정
				.anyRequest().authenticated())
			// Cors 설정 추가
			.addFilter(corsConfig.corsFilter())
			// jwtFilter를 UsernamePasswordAuthenticationFilter 앞에 오도록 설정
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			// formLogin 비활성화
			.formLogin(AbstractHttpConfigurer::disable)
			// httpBasic 비활성화
			.httpBasic(HttpBasicConfigurer::disable)
			// csrf 비활성화
			.csrf(AbstractHttpConfigurer::disable);
		return http.build();
	}
}
