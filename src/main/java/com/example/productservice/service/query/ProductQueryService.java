package com.sangsangplus.productservice.service.query;

import com.sangsangplus.productservice.domain.entity.Product;
import com.sangsangplus.productservice.dto.response.PageResponse;
import com.sangsangplus.productservice.dto.response.ProductResponse;
import com.sangsangplus.productservice.exception.ProductNotFoundException;
import com.sangsangplus.productservice.mapper.ProductMapper;
import com.sangsangplus.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductQueryService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    @Cacheable(value = "products", key = "#productId")
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findByIdWithImages(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        return productMapper.toResponse(product);
    }
    
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        Page<ProductResponse> responsePage = productPage.map(productMapper::toResponse);
        
        return PageResponse.of(responsePage);
    }
    
    public PageResponse<ProductResponse> getProductsByUserId(Long userId, Pageable pageable) {
        Page<Product> productPage = productRepository.findByUserId(userId, pageable);
        Page<ProductResponse> responsePage = productPage.map(productMapper::toResponse);
        
        return PageResponse.of(responsePage);
    }
    
    public PageResponse<ProductResponse> getProductsByCategory(String category, Pageable pageable) {
        Page<Product> productPage = productRepository.findByCategory(category, pageable);
        Page<ProductResponse> responsePage = productPage.map(productMapper::toResponse);
        
        return PageResponse.of(responsePage);
    }
    
    public PageResponse<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        Page<Product> productPage = productRepository.searchByKeyword(keyword, pageable);
        Page<ProductResponse> responsePage = productPage.map(productMapper::toResponse);
        
        return PageResponse.of(responsePage);
    }
    
    @Cacheable(value = "categories")
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
    
    public List<ProductResponse> getProductsByUserAndCategory(Long userId, String category) {
        List<Product> products = productRepository.findByUserIdAndCategory(userId, category);
        
        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
}