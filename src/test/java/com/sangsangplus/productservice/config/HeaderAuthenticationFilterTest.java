package com.sangsangplus.productservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HeaderAuthenticationFilterTest {

    private HeaderAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new HeaderAuthenticationFilter();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testSuccessfulAuthentication_WithUserRole() throws Exception {
        // Given
        when(request.getHeader("X-User-Id")).thenReturn("550e8400-e29b-41d4-a716-446655440001");
        when(request.getHeader("X-User-Role")).thenReturn("USER");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/products");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), auth.getPrincipal());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testSuccessfulAuthentication_WithAdminRole() throws Exception {
        // Given
        when(request.getHeader("X-User-Id")).thenReturn("550e8400-e29b-41d4-a716-446655440099");
        when(request.getHeader("X-User-Role")).thenReturn("ADMIN");
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/api/products/admin/123");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440099"), auth.getPrincipal());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testAuthentication_WithoutRoleHeader() throws Exception {
        // Given - no X-User-Role header
        when(request.getHeader("X-User-Id")).thenReturn("550e8400-e29b-41d4-a716-446655440001");
        when(request.getHeader("X-User-Role")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/products");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), auth.getPrincipal());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testNoAuthentication_WithoutHeaders() throws Exception {
        // Given - no headers
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-User-Role")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/products");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void testInvalidUserId_NoAuthentication() throws Exception {
        // Given - invalid user ID
        when(request.getHeader("X-User-Id")).thenReturn("invalid-id");
        when(request.getHeader("X-User-Role")).thenReturn("USER");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/products");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }
}