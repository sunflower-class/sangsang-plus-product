package com.sangsangplus.productservice.service;

import com.sangsangplus.productservice.domain.entity.Product;
import com.sangsangplus.productservice.domain.entity.ProductDetails;
import com.sangsangplus.productservice.dto.request.ProductDetailsRequest;
import com.sangsangplus.productservice.dto.response.ProductDetailsResponse;
import com.sangsangplus.productservice.exception.ProductNotFoundException;
import com.sangsangplus.productservice.exception.UnauthorizedException;
import com.sangsangplus.productservice.repository.ProductDetailsRepository;
import com.sangsangplus.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductDetailsService {
    
    private final ProductDetailsRepository productDetailsRepository;
    private final ProductRepository productRepository;
    
    public ProductDetailsService(ProductDetailsRepository productDetailsRepository, 
                                  ProductRepository productRepository) {
        this.productDetailsRepository = productDetailsRepository;
        this.productRepository = productRepository;
    }
    
    @Transactional(readOnly = true)
    public Optional<ProductDetailsResponse> getProductDetails(Long productId) {
        return productDetailsRepository.findByProductId(productId)
                .map(ProductDetailsResponse::from);
    }
    
    public ProductDetailsResponse createOrUpdateProductDetails(UUID userId, Long productId, ProductDetailsRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
        
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("상품 상세 정보를 수정할 권한이 없습니다");
        }
        
        ProductDetails productDetails = productDetailsRepository.findByProductId(productId)
                .orElse(new ProductDetails(product, request.getContent()));
        
        productDetails.setContent(request.getContent());
        
        ProductDetails saved = productDetailsRepository.save(productDetails);
        return ProductDetailsResponse.from(saved);
    }
    
    public void deleteProductDetails(UUID userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
        
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("상품 상세 정보를 삭제할 권한이 없습니다");
        }
        
        productDetailsRepository.deleteByProductId(productId);
    }
    
    public ProductDetailsResponse adminCreateOrUpdateProductDetails(Long productId, ProductDetailsRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
        
        ProductDetails productDetails = productDetailsRepository.findByProductId(productId)
                .orElse(new ProductDetails(product, request.getContent()));
        
        productDetails.setContent(request.getContent());
        
        ProductDetails saved = productDetailsRepository.save(productDetails);
        return ProductDetailsResponse.from(saved);
    }
    
    public void adminDeleteProductDetails(Long productId) {
        if (!productDetailsRepository.existsByProductId(productId)) {
            throw new ProductNotFoundException("상품 상세 정보를 찾을 수 없습니다: " + productId);
        }
        
        productDetailsRepository.deleteByProductId(productId);
    }
}