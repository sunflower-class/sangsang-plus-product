package com.sangsangplus.productservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sangsangplus.productservice.domain.entity.Product;
import com.sangsangplus.productservice.dto.request.ProductCreateRequest;
import com.sangsangplus.productservice.dto.response.ProductResponse;
import com.sangsangplus.productservice.repository.ProductRepository;
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
import java.util.UUID;

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
    private static final UUID TEST_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID ADMIN_USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440099");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        // 기존 테스트 데이터 정리
        productRepository.deleteAll();
    }

    @Test
    void testProductLifecycle_CreateAndDelete() throws Exception {
        // Given: 상품 생성 요청 데이터
        ProductCreateRequest createRequest = new ProductCreateRequest();
        createRequest.setName("테스트 노트북");
        createRequest.setDescription("통합 테스트용 노트북입니다");
        createRequest.setCategory("전자제품");
        createRequest.setPrice(new BigDecimal("1500000"));

        String createRequestJson = objectMapper.writeValueAsString(createRequest);

        // When: 상품 생성 API 호출
        System.out.println("=== 상품 생성 테스트 시작 ===");
        System.out.println("User ID: " + TEST_USER_ID);
        System.out.println("Create Request: " + createRequestJson);

        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("X-User-Id", TEST_USER_ID.toString())
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("테스트 노트북"))
                .andExpect(jsonPath("$.category").value("전자제품"))
                .andExpect(jsonPath("$.price").value(1500000))
                .andReturn();

        // Then: 생성된 상품 정보 확인
        String createResponse = createResult.getResponse().getContentAsString();
        ProductResponse createdProduct = objectMapper.readValue(createResponse, ProductResponse.class);
        Long productId = createdProduct.getProductId();
        
        System.out.println("생성된 상품 ID: " + productId);
        System.out.println("생성된 상품 정보: " + createResponse);

        // 데이터베이스에서 직접 확인
        Product savedProduct = productRepository.findById(productId).orElse(null);
        assertNotNull(savedProduct, "상품이 데이터베이스에 저장되어야 함");
        assertEquals("테스트 노트북", savedProduct.getName());
        assertEquals(TEST_USER_ID, savedProduct.getUserId());

        // When: 생성된 상품 조회
        System.out.println("=== 상품 조회 테스트 ===");
        mockMvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.name").value("테스트 노트북"))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()));

        // When: 상품 삭제 API 호출
        System.out.println("=== 상품 삭제 테스트 시작 ===");
        mockMvc.perform(delete("/api/products/" + productId)
                .header("X-User-Id", TEST_USER_ID.toString())
                .header("X-User-Role", "USER"))
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
        // Given: X-User-Id 헤더 없이 상품 생성 시도
        ProductCreateRequest createRequest = new ProductCreateRequest();
        createRequest.setName("무단 접근 테스트");
        createRequest.setDescription("인증 없는 요청");
        createRequest.setCategory("테스트");
        createRequest.setPrice(new BigDecimal("100000"));

        String createRequestJson = objectMapper.writeValueAsString(createRequest);

        // When & Then: 인증 없이 상품 생성 시도하면 403 에러
        System.out.println("=== 인증 없는 접근 테스트 ===");
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson))
                .andExpect(status().isForbidden());
        
        System.out.println("인증 없는 접근이 정상적으로 차단됨");
    }

    @Test 
    void testAdminAccess() throws Exception {
        // Given: 일반 사용자가 상품 생성
        ProductCreateRequest createRequest = new ProductCreateRequest();
        createRequest.setName("관리자 테스트 상품");
        createRequest.setDescription("관리자 권한 테스트용");
        createRequest.setCategory("테스트");
        createRequest.setPrice(new BigDecimal("200000"));

        String createRequestJson = objectMapper.writeValueAsString(createRequest);

        // 일반 사용자로 상품 생성
        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("X-User-Id", TEST_USER_ID.toString())
                .header("X-User-Role", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequestJson))
                .andExpect(status().isCreated())
                .andReturn();

        ProductResponse createdProduct = objectMapper.readValue(
            createResult.getResponse().getContentAsString(), 
            ProductResponse.class
        );
        Long productId = createdProduct.getProductId();

        // When & Then: 관리자가 상품 삭제
        System.out.println("=== 관리자 권한 테스트 ===");
        mockMvc.perform(delete("/api/products/admin/" + productId)
                .header("X-User-Id", ADMIN_USER_ID.toString())
                .header("X-User-Role", "ADMIN"))
                .andExpect(status().isNoContent());

        // 삭제 확인
        boolean exists = productRepository.existsById(productId);
        assertFalse(exists, "관리자가 삭제한 상품은 존재하지 않아야 함");
        
        System.out.println("관리자 권한 테스트 완료");
    }
}