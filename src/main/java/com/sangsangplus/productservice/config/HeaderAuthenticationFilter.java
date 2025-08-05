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
        
        logger.info("Header Auth Filter processing request: " + request.getMethod() + " " + request.getRequestURI());
        
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        String userRole = request.getHeader(USER_ROLE_HEADER);
        
        logger.info("X-User-Id header: " + userIdHeader);
        logger.info("X-User-Role header: " + userRole);
        
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
        }
        
        filterChain.doFilter(request, response);
    }
}