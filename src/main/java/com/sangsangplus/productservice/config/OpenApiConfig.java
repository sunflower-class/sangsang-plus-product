package com.sangsangplus.productservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SangSangPlus Product Service API")
                .description("중고 거래 플랫폼 SangSangPlus의 상품 관리 마이크로서비스 API")
                .version("v2.0.0")
                .contact(new Contact()
                    .name("SangSangPlus Team")
                    .url("https://github.com/sunflower-class/sangsang-plus-product")))
            .servers(List.of(
                new Server()
                    .url("https://oauth.buildingbite.com/api/products")
                    .description("Production Server"),
                new Server()
                    .url("http://localhost:8082/api/products")
                    .description("Local Development Server")
            ))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT 토큰을 사용한 인증 (게이트웨이에서 X-User-Id 헤더로 변환)")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/products/**")
            .build();
    }
}