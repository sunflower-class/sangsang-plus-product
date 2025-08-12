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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<ProductDetailsResponse> getProductDetailsList(Long productId) {
        return productDetailsRepository.findByProductIdOrderByDisplayOrderAsc(productId)
                .stream()
                .map(ProductDetailsResponse::from)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductDetailsResponse getProductDetail(Long detailId) {
        ProductDetails productDetails = productDetailsRepository.findByDetailId(detailId)
                .orElseThrow(() -> new ProductNotFoundException("상품 상세 정보를 찾을 수 없습니다: " + detailId));
        return ProductDetailsResponse.from(productDetails);
    }
    
    public ProductDetailsResponse createProductDetail(UUID userId, Long productId, ProductDetailsRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
        
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("상품 상세 정보를 생성할 권한이 없습니다");
        }
        
        // display_order가 지정되지 않았다면 마지막 순서로 설정
        Integer displayOrder = request.getDisplayOrder();
        if (displayOrder == null) {
            Integer maxOrder = productDetailsRepository.findMaxDisplayOrderByProductId(productId);
            displayOrder = (maxOrder != null ? maxOrder : 0) + 1;
        }
        
        ProductDetails productDetails = new ProductDetails(
            productId, 
            request.getTitle(), 
            request.getContent(), 
            displayOrder
        );
        
        ProductDetails saved = productDetailsRepository.save(productDetails);
        return ProductDetailsResponse.from(saved);
    }
    
    public ProductDetailsResponse updateProductDetail(UUID userId, Long detailId, ProductDetailsRequest request) {
        ProductDetails productDetails = productDetailsRepository.findByDetailId(detailId)
                .orElseThrow(() -> new ProductNotFoundException("상품 상세 정보를 찾을 수 없습니다: " + detailId));
        
        Product product = productRepository.findById(productDetails.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productDetails.getProductId()));
        
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("상품 상세 정보를 수정할 권한이 없습니다");
        }
        
        productDetails.setTitle(request.getTitle());
        productDetails.setContent(request.getContent());
        if (request.getDisplayOrder() != null) {
            productDetails.setDisplayOrder(request.getDisplayOrder());
        }
        
        ProductDetails saved = productDetailsRepository.save(productDetails);
        return ProductDetailsResponse.from(saved);
    }
    
    public void deleteProductDetail(UUID userId, Long detailId) {
        ProductDetails productDetails = productDetailsRepository.findByDetailId(detailId)
                .orElseThrow(() -> new ProductNotFoundException("상품 상세 정보를 찾을 수 없습니다: " + detailId));
        
        Product product = productRepository.findById(productDetails.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productDetails.getProductId()));
        
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("상품 상세 정보를 삭제할 권한이 없습니다");
        }
        
        productDetailsRepository.deleteByDetailId(detailId);
    }
    
    public void deleteAllProductDetails(UUID userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
        
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("상품 상세 정보를 삭제할 권한이 없습니다");
        }
        
        productDetailsRepository.deleteByProductId(productId);
    }
    
    // Admin methods
    public ProductDetailsResponse adminCreateProductDetail(Long productId, ProductDetailsRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
        
        Integer displayOrder = request.getDisplayOrder();
        if (displayOrder == null) {
            Integer maxOrder = productDetailsRepository.findMaxDisplayOrderByProductId(productId);
            displayOrder = (maxOrder != null ? maxOrder : 0) + 1;
        }
        
        ProductDetails productDetails = new ProductDetails(
            productId, 
            request.getTitle(), 
            request.getContent(), 
            displayOrder
        );
        
        ProductDetails saved = productDetailsRepository.save(productDetails);
        return ProductDetailsResponse.from(saved);
    }
    
    public ProductDetailsResponse adminUpdateProductDetail(Long detailId, ProductDetailsRequest request) {
        ProductDetails productDetails = productDetailsRepository.findByDetailId(detailId)
                .orElseThrow(() -> new ProductNotFoundException("상품 상세 정보를 찾을 수 없습니다: " + detailId));
        
        productDetails.setTitle(request.getTitle());
        productDetails.setContent(request.getContent());
        if (request.getDisplayOrder() != null) {
            productDetails.setDisplayOrder(request.getDisplayOrder());
        }
        
        ProductDetails saved = productDetailsRepository.save(productDetails);
        return ProductDetailsResponse.from(saved);
    }
    
    public void adminDeleteProductDetail(Long detailId) {
        if (!productDetailsRepository.existsByDetailId(detailId)) {
            throw new ProductNotFoundException("상품 상세 정보를 찾을 수 없습니다: " + detailId);
        }
        
        productDetailsRepository.deleteByDetailId(detailId);
    }
    
    public void adminDeleteAllProductDetails(Long productId) {
        if (!productDetailsRepository.existsByProductId(productId)) {
            throw new ProductNotFoundException("상품 상세 정보를 찾을 수 없습니다: " + productId);
        }
        
        productDetailsRepository.deleteByProductId(productId);
    }
}