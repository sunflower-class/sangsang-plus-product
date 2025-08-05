package com.sangsangplus.productservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductUpdatedEvent {
    private Long productId;
    private UUID userId;
    private String title;
    private String category;
    private BigDecimal price;
    private LocalDateTime updatedAt;
    
    public ProductUpdatedEvent() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public ProductUpdatedEvent(Long productId, UUID userId, String title, 
                              String category, BigDecimal price) {
        this.productId = productId;
        this.userId = userId;
        this.title = title;
        this.category = category;
        this.price = price;
        this.updatedAt = LocalDateTime.now();
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "ProductUpdatedEvent{" +
               "productId=" + productId +
               ", userId=" + userId +
               ", title='" + title + '\'' +
               ", category='" + category + '\'' +
               ", price=" + price +
               ", updatedAt=" + updatedAt +
               '}';
    }
}