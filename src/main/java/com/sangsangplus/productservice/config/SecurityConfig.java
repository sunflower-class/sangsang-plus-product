package com.sangsangplus.productservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final HeaderAuthenticationFilter headerAuthenticationFilter;

    public SecurityConfig(HeaderAuthenticationFilter headerAuthenticationFilter) {
        this.headerAuthenticationFilter = headerAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers(
                    "/health",
                    "/actuator/health",
                    "/api/products/health",
                    "/api/products",
                    "/api/products/{productId}",
                    "/api/products/user/{userId}",
                    "/api/products/category/{category}",
                    "/api/products/search",
                    "/api/products/categories",
                    "/api/products/recent",
                    // Swagger UI endpoints with /api/products prefix
                    "/api/products/swagger-ui.html",
                    "/api/products/swagger-ui/**",
                    "/api/products/v3/api-docs/**",
                    "/api/products/swagger-resources/**",
                    "/api/products/webjars/**",
                    // Also allow without prefix for local testing
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                // Authenticated endpoints
                .requestMatchers(
                    "/api/products/my/**"
                ).authenticated()
                // Admin endpoints
                .requestMatchers(
                    "/api/products/admin/**"
                ).hasRole("ADMIN")
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(headerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}