package com.sangsangplus.productservice.service;

import com.sangsangplus.productservice.service.external.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void testValidateTokenAndGetUserId_ReturnsNull() {
        // Since we no longer validate tokens, this should always return null
        Long userId = userService.validateTokenAndGetUserId("any-token");
        
        assertNull(userId);
    }

    @Test
    void testIsAdminWithToken_ReturnsFalse() {
        // Since we no longer validate tokens, this should always return false
        boolean isAdmin = userService.isAdmin("any-token");
        
        assertFalse(isAdmin);
    }

    @Test
    void testIsAdminWithUserId_ReturnsFalse() {
        // Role is now determined by X-User-Role header, not by userId
        boolean isAdmin = userService.isAdmin(123L);
        
        assertFalse(isAdmin);
    }

    @Test
    void testGetUserEmail_ReturnsPlaceholder() {
        // Without JWT, this returns a placeholder value
        String email = userService.getUserEmail("any-token");
        
        assertEquals("user@example.com", email);
    }

    @Test
    void testGetUserName_ReturnsPlaceholder() {
        // Without JWT, this returns a placeholder value
        String name = userService.getUserName("any-token");
        
        assertEquals("User", name);
    }

    @Test
    void testGetUserEmailWithUserId_ReturnsPlaceholder() {
        // Backward compatibility method
        String email = userService.getUserEmail(123L, "any-token");
        
        assertEquals("user@example.com", email);
    }

    @Test
    void testGetUserNameWithUserId_ReturnsPlaceholder() {
        // Backward compatibility method
        String name = userService.getUserName(123L, "any-token");
        
        assertEquals("User", name);
    }
}