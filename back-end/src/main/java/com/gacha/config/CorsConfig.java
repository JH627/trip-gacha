package com.gacha.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowedOrigins(List.of("http://localhost:5173"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

		// 쿠키 관련 설정
		config.setAllowCredentials(true);

		config.setAllowedHeaders(List.of(
			"Authorization",
			"Content-Type",
			"X-Requested-With",
			"Accept",           // 쿠키 처리를 위해 추가 (RefreshToken 용)
			"Origin",           // CORS 요청에 필요
			"Access-Control-Request-Method",
			"Access-Control-Request-Headers"
		));

		config.setExposedHeaders(List.of(
			"Authorization",
			"Set-Cookie",       // 쿠키 설정을 위해 필요 (RefreshToken 용)
			"Access-Control-Allow-Origin",
			"Access-Control-Allow-Credentials"
		));

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
