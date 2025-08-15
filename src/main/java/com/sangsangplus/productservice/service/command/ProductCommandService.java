package com.sangsangplus.productservice.service.command;

import com.sangsangplus.productservice.domain.entity.Product;
import com.sangsangplus.productservice.dto.request.ProductCreateRequest;
import com.sangsangplus.productservice.dto.request.ProductUpdateRequest;
import com.sangsangplus.productservice.dto.response.ProductResponse;
import com.sangsangplus.productservice.event.EventPublisher;
import com.sangsangplus.productservice.event.ProductCreatedEvent;
import com.sangsangplus.productservice.event.ProductDeletedEvent;
import com.sangsangplus.productservice.event.ProductUpdatedEvent;
import com.sangsangplus.productservice.exception.ProductNotFoundException;
import com.sangsangplus.productservice.exception.UnauthorizedException;
import com.sangsangplus.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class ProductCommandService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductCommandService.class);
    
    private final ProductRepository productRepository;
    private final EventPublisher eventPublisher;
    
    public ProductCommandService(ProductRepository productRepository,
                                EventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }
    
    // 상품 생성
    public ProductResponse createProduct(UUID userId, String token, ProductCreateRequest request) {
        logger.info("=== CREATE PRODUCT SERVICE START ===");
        logger.info("User ID: {}", userId);
        logger.info("Request: {}", request);
        
        // Product 엔티티 생성
        Product product = new Product(
            userId,
            request.getName(),
            request.getDescription(),
            request.getCategory(),
            request.getPrice(),
            request.getBrand(),
            request.getSource(),
            request.getStatus(),
            request.getMetadata()
        );
        
        logger.info("Created product entity: {}", product);
        
        // 저장
        Product savedProduct = productRepository.save(product);
        
        // 이벤트 발행
        eventPublisher.publishProductCreated(new ProductCreatedEvent(
            savedProduct.getProductId(),
            savedProduct.getUserId(),
            savedProduct.getName(),
            savedProduct.getCategory(),
            savedProduct.getPrice()
        ));
        
        return toProductResponse(savedProduct);
    }
    
    // 상품 수정
    public ProductResponse updateProduct(UUID userId, Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findByProductIdAndUserId(productId, userId)
            .orElseThrow(() -> new UnauthorizedException("You don't have permission to update this product"));
        
        // 상품 정보 업데이트
        product.updateProduct(
            request.getName(),
            request.getDescription(),
            request.getCategory(),
            request.getPrice(),
            request.getBrand() != null ? request.getBrand() : product.getBrand(),
            request.getStatus() != null ? request.getStatus() : product.getStatus()
        );
        
        Product updatedProduct = productRepository.save(product);
        
        // 이벤트 발행
        eventPublisher.publishProductUpdated(new ProductUpdatedEvent(
            updatedProduct.getProductId(),
            updatedProduct.getUserId(),
            updatedProduct.getName(),
            updatedProduct.getCategory(),
            updatedProduct.getPrice()
        ));
        
        return toProductResponse(updatedProduct);
    }
    
    // 상품 삭제
    public void deleteProduct(UUID userId, Long productId) {
        Product product = productRepository.findByProductIdAndUserId(productId, userId)
            .orElseThrow(() -> new UnauthorizedException("You don't have permission to delete this product"));
        
        productRepository.delete(product);
        
        // 이벤트 발행
        eventPublisher.publishProductDeleted(new ProductDeletedEvent(
            productId,
            userId
        ));
    }
    
    // 관리자용 - 상품 수정
    public ProductResponse adminUpdateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        product.updateProduct(
            request.getName(),
            request.getDescription(),
            request.getCategory(),
            request.getPrice(),
            request.getBrand() != null ? request.getBrand() : product.getBrand(),
            request.getStatus() != null ? request.getStatus() : product.getStatus()
        );
        
        return toProductResponse(productRepository.save(product));
    }
    
    // 관리자용 - 상품 삭제
    public void adminDeleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        productRepository.delete(product);
    }
    
    // Entity를 Response DTO로 변환
    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
            .productId(product.getProductId())
            .userId(product.getUserId())
            .name(product.getName())
            .description(product.getDescription())
            .category(product.getCategory())
            .price(product.getPrice())
            .brand(product.getBrand())
            .source(product.getSource())
            .status(product.getStatus())
            .metadata(product.getMetadata())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }
}