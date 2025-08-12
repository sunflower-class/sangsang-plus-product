package com.sangsangplus.productservice.dto.response;

import com.sangsangplus.productservice.domain.entity.ProductDetails;
import java.time.LocalDateTime;

public class ProductDetailsResponse {
    
    private Long detailId;
    private Long productId;
    private String title;
    private String content;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ProductDetailsResponse() {
    }
    
    public ProductDetailsResponse(Long detailId, Long productId, String title, String content, 
                                  Integer displayOrder, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.detailId = detailId;
        this.productId = productId;
        this.title = title;
        this.content = content;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static ProductDetailsResponse from(ProductDetails productDetails) {
        return new ProductDetailsResponse(
            productDetails.getDetailId(),
            productDetails.getProductId(),
            productDetails.getTitle(),
            productDetails.getContent(),
            productDetails.getDisplayOrder(),
            productDetails.getCreatedAt(),
            productDetails.getUpdatedAt()
        );
    }
    
    public Long getDetailId() {
        return detailId;
    }
    
    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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