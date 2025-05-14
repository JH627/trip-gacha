package com.gacha.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import static io.jsonwebtoken.Jwts.SIG.RS256;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.private.key.path}")
    private String privateKeyPath;
    private PrivateKey privateKey;

    @Value("${jwt.public.key.path}")
    private String publicKeyPath;
    private PublicKey publicKey;

    @Value("${jwt.access-token.expiration}")
    private long jwtAccessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long jwtRefreshTokenExpiration;

    /**
     * Key 파일 읽어 실제 데이터 추출.
     *
     * @param file Key 파일
     * @return BASE64 디코딩된 Key 데이터.
     * @throws IOException
     */
    private byte[] readKeyBytes(ClassPathResource keyFile) throws IOException {

        String keyContent = new String(keyFile.getInputStream().readAllBytes());

        // PEM 형식의 헤더와 푸터 제거
        keyContent = keyContent.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s", "");

        // Base64 디코딩
        return java.util.Base64.getDecoder().decode(keyContent);
    }

    @PostConstruct
    public void init() throws Exception {

        // RSA 형식 지정
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Private Key Load (PKCS#8 형식)
        byte[] privateKeyBytes = readKeyBytes(new ClassPathResource(privateKeyPath));
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        privateKey = keyFactory.generatePrivate(privateKeySpec);

        // Public Key Load (X.509 형식)
        byte[] publicKeyBytes = readKeyBytes(new ClassPathResource(publicKeyPath));
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        publicKey = keyFactory.generatePublic(publicKeySpec);

    }

    /**
     * Token Claims 추출.
     *
     * @param token Json Web Token
     * @return All Claims
     */
    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 사용자 ID 추출
     * 
     * @param token Json Web Token
     * @return 사용자 ID
     */
    public Integer extractUserId(String token) {
        return extractAllClaims(token).get("userId", Integer.class);
    }
    
    /**
     * 만료 일자 추출
     * 
     * @param token Json Web Token
     * @return 만료 일자
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * 만료 시간 추출
     * 
     * @param token Json Web Token
     * @return 만료 시간 (초)
     */
    public long getRemainingExpirationTime(String token) {
        Date now = new Date();
        return (extractExpiration(token).getTime() - now.getTime()) / 1000;
    }

    /**
     * 만료 여부
     * 
     * @param token JWT token
     * @return 만료 여부
     */
    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 토큰 검증 (서명 및 만료시간만 검증)
     * 
     * @param token JWT token
     * @throws JwtException 토큰이 유효하지 않을 경우 발생하는 예외
     */
    public void validateToken(String token) {
        // 토큰 검증 - 실패 시 관련 예외가 발생함
        Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token);

    }

    public String generateAccessToken(Integer userId) {
        String jwtId = UUID.randomUUID().toString();

        Date now = new Date();

        return Jwts.builder()
                .claim("userId", userId)
                .subject("ACCESS TOKEN")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtAccessTokenExpiration))
                .id(jwtId)
                .signWith(privateKey, RS256)
                .compact();
    }

    public String generateRefreshToken(Integer userId) {
        String jwtId = UUID.randomUUID().toString();

        Date now = new Date();

        return Jwts.builder()
                .claim("userId", userId)
                .subject("REFRESH TOKEN")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtRefreshTokenExpiration))
                .id(jwtId)
                .signWith(privateKey, RS256)
                .compact();
    }

    public String extractJTI(String token) {
        return extractAllClaims(token).getId();
    }
    
//    권한 필요시 Claim에 추가해야 함
//    public String extractRole(String token) {
//        return extractAllClaims(token).get("Role", String.class);
//    }
}
