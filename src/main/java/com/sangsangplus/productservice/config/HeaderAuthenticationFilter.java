package com.sangsangplus.productservice.config;

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
import java.util.UUID;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        
        logger.info("=== HEADER AUTH FILTER START ===");
        logger.info("Request: " + request.getMethod() + " " + request.getRequestURI());
        logger.info("Remote Address: " + request.getRemoteAddr());
        
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        String userRole = request.getHeader(USER_ROLE_HEADER);
        String authHeader = request.getHeader("Authorization");
        
        logger.info("X-User-Id header: [" + userIdHeader + "]");
        logger.info("X-User-Role header: [" + userRole + "]");
        logger.info("Authorization header: [" + (authHeader != null ? "Bearer " + authHeader.substring(0, Math.min(50, authHeader.length())) + "..." : "null") + "]");
        
        // 모든 헤더 로깅 (디버깅용)
        logger.info("All headers:");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if ("authorization".equalsIgnoreCase(headerName) && headerValue != null && headerValue.length() > 50) {
                headerValue = headerValue.substring(0, 50) + "...";
            }
            logger.info("  " + headerName + ": " + headerValue);
        }
        
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                UUID userId = UUID.fromString(userIdHeader);
                
                // Default to USER role if not specified
                if (userRole == null || userRole.isEmpty()) {
                    userRole = "USER";
                }
                
                // Add ROLE_ prefix if not present
                if (!userRole.startsWith("ROLE_")) {
                    userRole = "ROLE_" + userRole;
                }
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userId, 
                        null, 
                        Collections.singletonList(new SimpleGrantedAuthority(userRole))
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Successfully authenticated user: " + userId + " with role: " + userRole);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid UUID format for user ID: " + userIdHeader);
            }
        } else {
            logger.info("No X-User-Id header found - proceeding without authentication");
        }
        
        logger.info("=== HEADER AUTH FILTER END ===\n");
        filterChain.doFilter(request, response);
    }
}