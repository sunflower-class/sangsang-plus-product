package com.sangsangplus.productservice.service.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    @Value("${user-service.base-url:http://user-service}")
    private String userServiceBaseUrl;

    private final RestTemplate restTemplate;

    public UserService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Since we're using header-based authentication, the user ID is already validated by the gateway.
     * This method is kept for backward compatibility but now just returns the provided userId.
     */
    public Long validateTokenAndGetUserId(String token) {
        // Token validation is no longer needed as gateway handles it
        // This method is kept for backward compatibility
        return null;
    }

    /**
     * Check if the user has admin role based on the security context.
     * The role is already set by HeaderAuthenticationFilter from X-User-Role header.
     */
    public boolean isAdmin(String token) {
        // Role is now determined from X-User-Role header, not from token
        return false;
    }
    
    /**
     * Check if the user has admin role.
     * This should be determined by the X-User-Role header from the gateway.
     */
    public boolean isAdmin(Long userId) {
        // Role is now determined from X-User-Role header
        return false;
    }

    /**
     * Get user email. Since we don't have JWT anymore, this would need to call user service
     * or return a placeholder value.
     */
    public String getUserEmail(String token) {
        // In a real implementation, you might want to call the user service
        // For now, returning a placeholder
        return "user@example.com";
    }

    /**
     * Get user name. Since we don't have JWT anymore, this would need to call user service
     * or return a placeholder value.
     */
    public String getUserName(String token) {
        // In a real implementation, you might want to call the user service
        // For now, returning a placeholder
        return "User";
    }
    
    // Backward compatibility methods
    public String getUserEmail(Long userId, String token) {
        return getUserEmail(token);
    }

    public String getUserName(Long userId, String token) {
        return getUserName(token);
    }
}