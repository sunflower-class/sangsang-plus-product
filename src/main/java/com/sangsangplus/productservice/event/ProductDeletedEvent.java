package com.sangsangplus.productservice.event;

import java.time.LocalDateTime;

public class ProductDeletedEvent {
    private Long productId;
    private Long userId;
    private LocalDateTime deletedAt;
    
    public ProductDeletedEvent() {
        this.deletedAt = LocalDateTime.now();
    }
    
    public ProductDeletedEvent(Long productId, Long userId) {
        this.productId = productId;
        this.userId = userId;
        this.deletedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    @Override
    public String toString() {
        return "ProductDeletedEvent{" +
               "productId=" + productId +
               ", userId=" + userId +
               ", deletedAt=" + deletedAt +
               '}';
    }
}