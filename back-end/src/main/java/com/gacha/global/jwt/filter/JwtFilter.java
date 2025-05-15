package com.gacha.global.jwt.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gacha.exception.JwtErrorCode;
import com.gacha.exception.JwtException;
import com.gacha.global.api.Response;
import com.gacha.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, Object> redisTemplate;

	private final String BLACKLIST_PREFIX = "jwt:blacklist:";
	private final String AUTHORIZATION = "Authorization";
	private final String OPTIONS = "OPTIONS";
	
	private final List<String> EXCLUDE_URL = List.of(
			"/error",
			"/auth/login", "/auth/refresh-token", "/auth/logout", 
			"/user/regist", 
			"/email/verification", 
			"/email/verification-confirm",
			"/swagger-ui",
			"/swagger-ui/",
			"/swagger-ui/index.html",
			"/swagger-ui/**",
			"/v3/api-docs",
			"/v3/api-docs/**"
		);
	
	/**
	 * 특정 요청에 대해 필터를 적용하지 않도록 설정
	 */
	@Override
	public boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		boolean shouldNotFilter = OPTIONS.equalsIgnoreCase(request.getMethod()) ||
				EXCLUDE_URL.stream().anyMatch(pattern -> {
					if (pattern.endsWith("/**")) {
						String basePattern = pattern.substring(0, pattern.length() - 3);
						return path.startsWith(basePattern);
					}
					return path.equals(pattern);
				});

		log.debug("요청 경로: {}, 필터 적용 제외 여부: {}", path, shouldNotFilter);
		return shouldNotFilter;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			// 1. HttpServletRequest에 있는 header에서 Authorization header를 가져와 토큰을 가져온다.
			String accessToken = getAccessTokenFromHeader(request.getHeader(AUTHORIZATION));

			// 토큰이 없거나 비어있는 경우 예외 발생
			if (accessToken == null || accessToken.isEmpty()) {
				sendErrorResponse(response, JwtErrorCode.INVALID_TOKEN);
				return;
			}

			// 토큰이 블랙리스트에 있는 토큰인 경우 예외 발생
			if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken))) {
				log.error("Blacklisted Token: {}", accessToken);
				sendErrorResponse(response, JwtErrorCode.BLACKLISTED_TOKEN);
				return;
			}

			// 토큰 유효성 검증
			jwtUtil.validateToken(accessToken);
			
			// 토큰에서 사용자 ID 추출
			Integer userId = jwtUtil.extractUserId(accessToken);
			
			if (userId == null) {
				log.error("토큰 안에 사용자 ID가 없음");
				sendErrorResponse(response, JwtErrorCode.INVALID_TOKEN);
				return;
			}
			
			// Authentication 객체 생성 및 SecurityContext에 저장
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				userId, null, Collections.emptyList()
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// 토큰이 유효하면 다음 필터로 진행
			filterChain.doFilter(request, response);
		} catch (ServletException e) {
			// ServletException의 원인(cause) 확인
			Throwable cause = e.getCause();
			if (cause instanceof JwtException) {
				log.error("JWT Exception: {} (Type: {})", cause.getMessage(), cause.getClass().getName());
				sendErrorResponse(response, JwtErrorCode.INVALID_TOKEN);
			} else {
				log.error("Servlet Exception: {} (Cause: {})", e.getMessage(),
						cause != null ? cause.getClass().getName() : "null");
				sendErrorResponse(response, JwtErrorCode.INVALID_TOKEN);
			}
		} catch (MalformedJwtException e) {
			log.error("Malformed JWT Token: {}", e.getMessage());
			sendErrorResponse(response, JwtErrorCode.MALFORMED_TOKEN);
		} catch (SignatureException e) {
			log.error("Invalid JWT Signature: {}", e.getMessage());
			sendErrorResponse(response, JwtErrorCode.INVALID_SIGNATURE);
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT Token: {}", e.getMessage());
			sendErrorResponse(response, JwtErrorCode.UNSUPPORTED_TOKEN);
		} catch (ExpiredJwtException e) {
			log.error("JWT Token expired: {}", e.getMessage());
			sendErrorResponse(response, JwtErrorCode.EXPIRED_ACCESS_TOKEN);
		} catch (JwtException e) {
			log.error("JWT Token error: {}", e.getMessage());
			sendErrorResponse(response, JwtErrorCode.INVALID_TOKEN);
		} catch (Exception e) {
			log.error("Error Occurred: {} (Type: {})", e.getMessage(), e.getClass().getName());
			sendErrorResponse(response, JwtErrorCode.INVALID_TOKEN);
		}
	}
	
	/**
	 * 에러 응답을 보내는 메소드
	 * ControllerAdvice 스타일로 일관된 에러 포맷 유지
	 */
	private void sendErrorResponse(HttpServletResponse response, JwtErrorCode errorCode) throws IOException {
		Response<?> apiResponse = Response.onFailure(
			errorCode.getStatus(),
			errorCode.getCode(),
			errorCode.getMessage(),
			null
		);
		
		response.setStatus(errorCode.getStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		
		String responseBody = objectMapper.writeValueAsString(apiResponse);
		response.getWriter().write(responseBody);
	}
	
	/**
	 * 응답 헤더에서 인증 토큰 추출
	 */
	private String getAccessTokenFromHeader(String header) {
		if (header == null || !header.startsWith("Bearer ")) {
			return null;
		}
		return header.substring(7);
	}
}
