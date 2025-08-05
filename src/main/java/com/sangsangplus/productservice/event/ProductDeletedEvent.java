package com.sangsangplus.productservice.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProductDeletedEvent {
    private Long productId;
    private UUID userId;
    private LocalDateTime deletedAt;
    
    public ProductDeletedEvent() {
        this.deletedAt = LocalDateTime.now();
    }
    
    public ProductDeletedEvent(Long productId, UUID userId) {
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
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
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