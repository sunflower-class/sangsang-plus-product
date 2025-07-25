package com.sangsangplus.productservice.config;

import com.sangsangplus.productservice.service.external.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    public JwtAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        
        logger.info("JWT Filter processing request: " + request.getMethod() + " " + request.getRequestURI());
        String token = extractToken(request);
        logger.info("Extracted token: " + (token != null ? "Present" : "Null"));
        
        if (token != null) {
            try {
                // UserService로 토큰 검증 및 사용자 정보 가져오기
                Long userId = userService.validateTokenAndGetUserId(token);
                boolean isAdmin = userService.isAdmin(token);
                
                if (userId != null) {
                    String role = isAdmin ? "ROLE_ADMIN" : "ROLE_USER";
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userId, 
                            null, 
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                        );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 토큰이 유효하지 않은 경우 인증 정보 없이 진행
                logger.error("Token validation failed: " + e.getMessage(), e);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        // Authorization 헤더에서 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 쿠키에서 access_token 추출
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
}