package com.sangsangplus.productservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JwtTestUtil {
    
    private static final String JWT_SECRET = "your-secret-key-for-jwt-token-must-be-at-least-256-bits";
    
    public static String createTestJwtToken(String email, String name, String role) {
        Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        
        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);
        
        return Jwts.builder()
                .setSubject(email)
                .claim("name", name)
                .claim("role", role)
                .claim("emailVerified", true)
                .claim("createdAt", now.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(key)
                .compact();
    }
    
    public static String createUserToken() {
        return createTestJwtToken("testuser@example.com", "테스트사용자", "USER");
    }
    
    public static String createAdminToken() {
        return createTestJwtToken("admin@example.com", "관리자", "ADMIN");
    }
}