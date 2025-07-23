package com.sangsangplus.productservice.security;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtUserDetails {
    private Long userId;  // User 서비스와 연동을 위해 추가
    private String email;
    private String name;
    private String role;
}