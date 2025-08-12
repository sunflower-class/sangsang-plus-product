package com.sangsangplus.productservice.dto.response;

import com.sangsangplus.productservice.domain.entity.ProductDetails;
import java.time.LocalDateTime;

public class ProductDetailsResponse {
    
    private Long productId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ProductDetailsResponse() {
    }
    
    public ProductDetailsResponse(Long productId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.productId = productId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static ProductDetailsResponse from(ProductDetails productDetails) {
        return new ProductDetailsResponse(
            productDetails.getProductId(),
            productDetails.getContent(),
            productDetails.getCreatedAt(),
            productDetails.getUpdatedAt()
        );
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}