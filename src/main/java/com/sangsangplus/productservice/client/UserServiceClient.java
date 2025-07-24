package com.sangsangplus.productservice.client;

import com.sangsangplus.productservice.dto.external.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserServiceClient {
    
    // TODO: 실제로는 RestTemplate이나 WebClient로 User Service 호출
    public UserDto getUser(Long userId, String token) {
        // 임시 구현 - 나중에 실제 User Service 호출로 변경
        return new UserDto(
            userId,
            "user" + userId + "@example.com",
            "User " + userId,
            "010-1234-5678",
            "USER"
        );
    }
}