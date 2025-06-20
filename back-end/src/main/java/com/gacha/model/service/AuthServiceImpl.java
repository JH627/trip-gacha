package com.gacha.model.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.gacha.exception.JwtErrorCode;
import com.gacha.exception.JwtException;
import com.gacha.exception.UserErrorCode;
import com.gacha.exception.UserException;
import com.gacha.model.dao.UserDao;
import com.gacha.model.dto.user.LoginRequest;
import com.gacha.model.dto.user.LoginResponse;
import com.gacha.model.dto.user.UserDto;
import com.gacha.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.ExpiredJwtException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	
    private final JwtUtil jwtUtil;
    private final UserDao userDao;
    private final RedisTemplate<String, Object> redisTemplate;
    private final String BLACKLIST_PREFIX = "jwt:blacklist:";

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

    	UserDto findUser = userDao.selectByEmail(loginRequest.getEmail());
    	
    	// 유저를 못 찾은 경우
        if (findUser == null) {
            throw new UserException(UserErrorCode.NOT_FOUND);
        }

        // 비밀번호가 일치하지 않는 경우
        if(!BCrypt.checkpw(loginRequest.getPassword(), findUser.getPassword())) {
        	throw new UserException(UserErrorCode.NOT_FOUND);
        }
        
        return LoginResponse.builder()
        		.userId(findUser.getUserId())
        		.accessToken(jwtUtil.generateAccessToken(findUser.getUserId()))
        		.refreshToken(jwtUtil.generateRefreshToken(findUser.getUserId()))
        		.build();
    }

    @Override
    public void logout(String authorization, String refreshToken) {
        // 둘 다 없으면 아무 것도 하지 않음
        if ((authorization == null || authorization.isBlank()) && 
            (refreshToken == null || refreshToken.isBlank())) {
            return;
        }

        // accessToken 처리
        if (authorization != null && !authorization.isBlank()) {
            String accessToken = authorization.substring(7);
            String accessTokenKey = BLACKLIST_PREFIX + accessToken;
            redisTemplate.opsForValue().set(accessTokenKey, "blacklisted");
            redisTemplate.expire(accessTokenKey, jwtUtil.getRemainingExpirationTime(accessToken), TimeUnit.SECONDS);
        }

        // refreshToken 처리
        if (refreshToken != null && !refreshToken.isBlank()) {
            String refreshTokenKey = BLACKLIST_PREFIX + refreshToken;
            redisTemplate.opsForValue().set(refreshTokenKey, "blacklisted");
            redisTemplate.expire(refreshTokenKey, jwtUtil.getRemainingExpirationTime(refreshToken), TimeUnit.SECONDS);
        }
    }

    @Override
    public String reGenerateAccessToken(String refreshToken) {
    	
        // 리프레시 토큰이 없는 경우
        if (refreshToken == null || refreshToken.isBlank()) {
        	log.error("리프레시 토큰이 없음");
        	throw new JwtException(JwtErrorCode.MALFORMED_TOKEN);
        }
        
        // 블랙리스트 토큰 확인
        if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + refreshToken))) {
        	log.error("만료되거나 블랙리스트에 있는 리프레시 토큰");
        	throw new JwtException(JwtErrorCode.BLACKLISTED_TOKEN);
        }

        // 유효한 리프레시 토큰이라면 userId 기반으로 다시 발급
        try {
            Integer userId = jwtUtil.extractUserId(refreshToken);
            return jwtUtil.generateAccessToken(userId);
        } catch (ExpiredJwtException e) {
            log.error("리프레시 토큰이 만료됨");
            throw new JwtException(JwtErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (Exception e) {
            log.error("리프레시 토큰 검증 실패: {}", e.getMessage());
            throw new JwtException(JwtErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
