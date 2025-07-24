package com.sangsangplus.productservice.service;

import com.sangsangplus.productservice.service.external.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private final String jwtSecret = "your-secret-key-for-jwt-token-must-be-at-least-256-bits";

    @BeforeEach
    void setUp() {
        userService = new UserService();
        ReflectionTestUtils.setField(userService, "jwtSecret", jwtSecret);
    }

    @Test
    void testValidateTokenAndGetUserId() {
        // JWT 토큰 생성
        String token = createTestToken("test@example.com", "테스트유저", "USER");
        
        // 토큰에서 사용자 ID 추출
        Long userId = userService.validateTokenAndGetUserId(token);
        
        assertNotNull(userId);
        assertTrue(userId > 0);
    }

    @Test
    void testIsAdminWithUserRole() {
        String token = createTestToken("user@example.com", "일반유저", "USER");
        
        boolean isAdmin = userService.isAdmin(token);
        
        assertFalse(isAdmin);
    }

    @Test
    void testIsAdminWithAdminRole() {
        String token = createTestToken("admin@example.com", "관리자", "ADMIN");
        
        boolean isAdmin = userService.isAdmin(token);
        
        assertTrue(isAdmin);
    }

    @Test
    void testGetUserEmail() {
        String email = "test@example.com";
        String token = createTestToken(email, "테스트유저", "USER");
        
        String extractedEmail = userService.getUserEmail(token);
        
        assertEquals(email, extractedEmail);
    }

    @Test
    void testGetUserName() {
        String name = "테스트유저";
        String token = createTestToken("test@example.com", name, "USER");
        
        String extractedName = userService.getUserName(token);
        
        assertEquals(name, extractedName);
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.jwt.token";
        
        Long userId = userService.validateTokenAndGetUserId(invalidToken);
        boolean isAdmin = userService.isAdmin(invalidToken);
        
        assertNull(userId);
        assertFalse(isAdmin);
    }

    private String createTestToken(String email, String name, String role) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
            .setSubject(email)
            .claim("name", name)
            .claim("role", role)
            .claim("emailVerified", false)
            .claim("createdAt", "2025-07-22T04:17:07.294811")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24시간
            .signWith(key)
            .compact();
    }
}