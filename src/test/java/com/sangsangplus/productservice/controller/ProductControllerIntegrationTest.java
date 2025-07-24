package com.sangsangplus.productservice.controller;

import com.sangsangplus.productservice.domain.entity.Product;
import com.sangsangplus.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebMvc
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private ProductRepository productRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 테스트 데이터 생성
        productRepository.deleteAll(); // 기존 데이터 삭제
        
        // 테스트 상품 생성
        Product product1 = new Product(1L, "user1@example.com", "사용자1", 
            "맛있는 사과", "신선하고 달콤한 사과입니다.", "과일", new BigDecimal("5000.00"));
        Product product2 = new Product(2L, "user2@example.com", "사용자2", 
            "노트북 판매", "사용한지 1년된 노트북입니다.", "전자제품", new BigDecimal("800000.00"));
        Product product3 = new Product(1L, "user1@example.com", "사용자1", 
            "운동화", "거의 새 것 같은 운동화입니다.", "의류", new BigDecimal("120000.00"));
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
    }

    @Test
    void contextLoads() {
        // 컨텍스트가 정상적으로 로드되는지 확인
    }

    @Test
    void testGetExistingProduct() throws Exception {
        // 존재하는 상품 조회 (테스트 데이터에 ID 1번 상품이 있음)
        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(1))
            .andExpect(jsonPath("$.title").value("맛있는 사과"))
            .andExpect(jsonPath("$.category").value("과일"));
    }

    @Test
    void testGetNonExistingProduct() throws Exception {
        // 존재하지 않는 상품 조회
        mockMvc.perform(get("/api/products/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Product Not Found"))
            .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
    }

    @Test
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalElements").value(3)) // 테스트 데이터 3개
            .andExpect(jsonPath("$.content[0].title").exists());
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        mockMvc.perform(get("/api/products/category/과일"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].category").value("과일"));
    }

    @Test
    void testSearchProducts() throws Exception {
        mockMvc.perform(get("/api/products/search").param("keyword", "사과"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].title").value("맛있는 사과"));
    }

    @Test
    void testGetProductsByUser() throws Exception {
        mockMvc.perform(get("/api/products/user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].userId").value(1));
    }

    @Test
    void testGetRecentProducts() throws Exception {
        mockMvc.perform(get("/api/products/recent").param("limit", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/api/products/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[*]").isNotEmpty());
    }
}