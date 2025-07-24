package com.sangsangplus.productservice.service.external;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.security.Key;
import java.nio.charset.StandardCharsets;

@Service
public class UserService {

    @Value("${user-service.base-url:http://user-service}")
    private String userServiceBaseUrl;
    
    @Value("${jwt.secret:your-secret-key-for-jwt-token-must-be-at-least-256-bits}")
    private String jwtSecret;

    private final RestTemplate restTemplate;

    public UserService() {
        this.restTemplate = new RestTemplate();
    }
    
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Long validateTokenAndGetUserId(String token) {
        try {
            // JWT 토큰 파싱 및 검증
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            // sub 필드에서 이메일 추출, 이메일을 해시해서 userId로 사용
            String email = claims.getSubject();
            if (email != null) {
                // 이메일을 해시해서 고유한 userId 생성
                return Math.abs((long) email.hashCode() % 100000) + 1;
            }
            return null;
        } catch (Exception e) {
            // JWT 파싱 실패 시 null 반환
            return null;
        }
    }

    public boolean isAdmin(String token) {
        try {
            // JWT 토큰에서 role 정보 추출
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            String role = claims.get("role", String.class);
            return "ADMIN".equals(role);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isAdmin(Long userId) {
        // 하위 호환성을 위해 유지, 실제로는 토큰 기반 isAdmin(String token) 사용 권장
        return false;
    }

    public String getUserEmail(String token) {
        try {
            // JWT 토큰에서 이메일 정보 추출
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            return claims.getSubject(); // sub 필드가 이메일
        } catch (Exception e) {
            return "unknown@example.com";
        }
    }

    public String getUserName(String token) {
        try {
            // JWT 토큰에서 이름 정보 추출
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            return claims.get("name", String.class);
        } catch (Exception e) {
            return "Unknown User";
        }
    }
    
    // 하위 호환성을 위한 메서드들
    public String getUserEmail(Long userId, String token) {
        return getUserEmail(token);
    }

    public String getUserName(Long userId, String token) {
        return getUserName(token);
    }
}