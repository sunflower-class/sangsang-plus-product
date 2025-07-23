package com.sangsangplus.productservice.client;

import com.sangsangplus.productservice.dto.external.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${services.user.url:http://localhost:8081}")
    private String userServiceUrl;
    
    public Optional<UserDto> getUserById(Long userId, String token) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(userServiceUrl)
                    .path("/api/users/{id}")
                    .buildAndExpand(userId)
                    .toUriString();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<UserDto> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    entity, 
                    UserDto.class
            );
            
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            log.error("Error fetching user data for id: {}", userId, e);
            return Optional.empty();
        }
    }
    
    public Optional<UserDto> getUserByEmail(String email, String token) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(userServiceUrl)
                    .path("/api/users/email/{email}")
                    .buildAndExpand(email)
                    .toUriString();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<UserDto> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    entity, 
                    UserDto.class
            );
            
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            log.error("Error fetching user data for email: {}", email, e);
            return Optional.empty();
        }
    }
    
    public boolean checkUserExists(String email, String token) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(userServiceUrl)
                    .path("/api/users/{email}/exists")
                    .buildAndExpand(email)
                    .toUriString();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    entity, 
                    Boolean.class
            );
            
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            log.error("Error checking user existence for email: {}", email, e);
            return false;
        }
    }
}