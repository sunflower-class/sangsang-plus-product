package com.sangsangplus.productservice.service;

import com.sangsangplus.productservice.domain.entity.Product;
import com.sangsangplus.productservice.dto.request.ProductCreateRequest;
import com.sangsangplus.productservice.dto.response.ProductResponse;
import com.sangsangplus.productservice.event.publisher.EventPublisher;
import com.sangsangplus.productservice.mapper.ProductMapper;
import com.sangsangplus.productservice.repository.ProductRepository;
import com.sangsangplus.productservice.service.command.ProductCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductMapper productMapper;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private ProductCommandService productCommandService;
    
    private ProductCreateRequest createRequest;
    private Product product;
    private ProductResponse productResponse;
    
    @BeforeEach
    void setUp() {
        createRequest = ProductCreateRequest.builder()
                .title("Test Product")
                .description("Test Description")
                .category("Electronics")
                .price(new BigDecimal("99.99"))
                .build();
        
        product = Product.builder()
                .productId(1L)
                .userId(1L)
                .title("Test Product")
                .description("Test Description")
                .category("Electronics")
                .price(new BigDecimal("99.99"))
                .build();
        
        productResponse = ProductResponse.builder()
                .productId(1L)
                .userId(1L)
                .title("Test Product")
                .description("Test Description")
                .category("Electronics")
                .price(new BigDecimal("99.99"))
                .build();
    }
    
    @Test
    void createProduct_Success() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        
        // When
        ProductResponse result = productCommandService.createProduct(1L, createRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Product");
        
        verify(productRepository, times(1)).save(any(Product.class));
        verify(eventPublisher, times(1)).publishProductCreatedEvent(any());
    }
}