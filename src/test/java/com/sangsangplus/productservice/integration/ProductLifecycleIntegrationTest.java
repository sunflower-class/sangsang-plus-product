package com.sangsangplus.productservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangsangplus.productservice.domain.entity.Product;
import com.sangsangplus.productservice.dto.request.ProductCreateRequest;
import com.sangsangplus.productservice.dto.response.ProductResponse;
import com.sangsangplus.productservice.repository.ProductRepository;
import com.sangsangplus.productservice.util.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
public class ProductLifecycleIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private MockMvc mockMvc;
    private String userJwtToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        userJwtToken = JwtTestUtil.createUserToken();
        
        // 기존 테스트 데이터 정리
        productRepository.deleteAll();
    }

    @Test
    void testProductLifecycle_CreateAndDelete() throws Exception {
        // Given: 상품 생성 요청 데이터
        ProductCreateRequest createRequest = new ProductCreateRequest();
        createRequest.setTitle("테스트 노트북");
        createRequest.setDescription("통합 테스트용 노트북입니다");
        createRequest.setCategory("전자제품");
        createRequest.setPrice(new BigDecimal("1500000"));

        String createRequestJson = objectMapper.writeValueAsString(createRequest);

        // When: 상품 생성 API 호출
        System.out.println("=== 상품 생성 테스트 시작 ===");
        System.out.println("JWT Token: " + userJwtToken);
        System.out.println("Create Request: " + createRequestJson);

        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + userJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("테스트 노트북"))
                .andExpect(jsonPath("$.category").value("전자제품"))
                .andExpect(jsonPath("$.price").value(1500000))
                .andExpect(jsonPath("$.userEmail").value("testuser@example.com"))
                .andExpect(jsonPath("$.userName").value("테스트사용자"))
                .andReturn();

        // Then: 생성된 상품 정보 확인
        String createResponse = createResult.getResponse().getContentAsString();
        ProductResponse createdProduct = objectMapper.readValue(createResponse, ProductResponse.class);
        Long productId = createdProduct.getId();
        
        System.out.println("생성된 상품 ID: " + productId);
        System.out.println("생성된 상품 정보: " + createResponse);

        // 데이터베이스에서 직접 확인
        Product savedProduct = productRepository.findById(productId).orElse(null);
        assertNotNull(savedProduct, "상품이 데이터베이스에 저장되어야 함");
        assertEquals("테스트 노트북", savedProduct.getTitle());
        assertEquals("testuser@example.com", savedProduct.getUserEmail());

        // When: 생성된 상품 조회
        System.out.println("=== 상품 조회 테스트 ===");
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.title").value("테스트 노트북"))
                .andExpect(jsonPath("$.userEmail").value("testuser@example.com"));

        // When: 상품 삭제 API 호출
        System.out.println("=== 상품 삭제 테스트 시작 ===");
        mockMvc.perform(delete("/api/products/" + productId)
                .header("Authorization", "Bearer " + userJwtToken))
                .andExpect(status().isNoContent());

        // Then: 삭제된 상품 확인
        System.out.println("=== 삭제 확인 테스트 ===");
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isNotFound());

        // 데이터베이스에서 직접 확인
        boolean exists = productRepository.existsById(productId);
        assertFalse(exists, "삭제된 상품은 데이터베이스에 존재하지 않아야 함");
        
        System.out.println("=== 상품 생명주기 테스트 완료 ===");
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // Given: JWT 토큰 없이 상품 생성 시도
        ProductCreateRequest createRequest = new ProductCreateRequest();
        createRequest.setTitle("무단 접근 테스트");
        createRequest.setDescription("인증 없는 요청");
        createRequest.setCategory("테스트");
        createRequest.setPrice(new BigDecimal("100000"));

        String createRequestJson = objectMapper.writeValueAsString(createRequest);

        // When & Then: 인증 없이 상품 생성 시도하면 401 에러
        System.out.println("=== 인증 없는 접근 테스트 ===");
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson))
                .andExpect(status().isUnauthorized());
        
        System.out.println("인증 없는 접근이 정상적으로 차단됨");
    }

    @Test 
    void testInvalidJwtToken() throws Exception {
        // Given: 잘못된 JWT 토큰
        String invalidToken = "invalid.jwt.token";
        
        ProductCreateRequest createRequest = new ProductCreateRequest();
        createRequest.setTitle("잘못된 토큰 테스트");
        createRequest.setDescription("유효하지 않은 JWT 토큰");
        createRequest.setCategory("테스트");
        createRequest.setPrice(new BigDecimal("50000"));

        String createRequestJson = objectMapper.writeValueAsString(createRequest);

        // When & Then: 잘못된 JWT 토큰으로 상품 생성 시도하면 401 에러
        System.out.println("=== 잘못된 JWT 토큰 테스트 ===");
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson))
                .andExpect(status().isUnauthorized());
        
        System.out.println("잘못된 JWT 토큰이 정상적으로 차단됨");
    }
}