package com.sangsangplus.productservice.service.query;

import com.sangsangplus.productservice.domain.entity.Product;
import com.sangsangplus.productservice.dto.response.PageResponse;
import com.sangsangplus.productservice.dto.response.ProductImageResponse;
import com.sangsangplus.productservice.dto.response.ProductResponse;
import com.sangsangplus.productservice.exception.ProductNotFoundException;
import com.sangsangplus.productservice.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductQueryService {
    
    private final ProductRepository productRepository;
    
    public ProductQueryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // 단일 상품 조회
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        return toProductResponse(product);
    }
    
    // 모든 상품 조회 (페이징)
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        Page<ProductResponse> responsePage = productPage.map(this::toProductResponse);
        return PageResponse.of(responsePage);
    }
    
    // 사용자별 상품 조회
    public PageResponse<ProductResponse> getProductsByUserId(UUID userId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByUserId(userId, pageable);
        Page<ProductResponse> responsePage = productPage.map(this::toProductResponse);
        return PageResponse.of(responsePage);
    }
    
    // 카테고리별 상품 조회
    public PageResponse<ProductResponse> getProductsByCategory(String category, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategory(category, pageable);
        Page<ProductResponse> responsePage = productPage.map(this::toProductResponse);
        return PageResponse.of(responsePage);
    }
    
    // 사용자와 카테고리로 상품 조회
    public List<ProductResponse> getProductsByUserAndCategory(UUID userId, String category) {
        List<Product> products = productRepository.findByUserIdAndCategory(userId, category);
        return products.stream()
            .map(this::toProductResponse)
            .collect(Collectors.toList());
    }
    
    // 키워드로 상품 검색
    public PageResponse<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository.searchByKeyword(keyword, pageable);
        Page<ProductResponse> responsePage = productPage.map(this::toProductResponse);
        return PageResponse.of(responsePage);
    }
    
    // 모든 카테고리 목록 조회
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
    
    // 최근 상품 조회
    public List<ProductResponse> getRecentProducts(int limit) {
        List<Product> products = productRepository.findRecentProducts(
            Pageable.ofSize(limit)
        );
        return products.stream()
            .map(this::toProductResponse)
            .collect(Collectors.toList());
    }
    
    // Entity를 Response DTO로 변환
    private ProductResponse toProductResponse(Product product) {
        List<ProductImageResponse> imageResponses = product.getImages().stream()
            .map(image -> new ProductImageResponse(
                image.getImageId(),
                image.getUrl(),
                image.getAltText(),
                image.getDisplayOrder(),
                image.getCreatedAt()
            ))
            .collect(Collectors.toList());
        
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
            .images(imageResponses)
            .build();
    }
}