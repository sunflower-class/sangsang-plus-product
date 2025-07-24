package com.sangsangplus.productservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductCreatedEvent {
    private Long productId;
    private Long userId;
    private String title;
    private String category;
    private BigDecimal price;
    private LocalDateTime createdAt;
    
    public ProductCreatedEvent() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ProductCreatedEvent(Long productId, Long userId, String title, 
                              String category, BigDecimal price) {
        this.productId = productId;
        this.userId = userId;
        this.title = title;
        this.category = category;
        this.price = price;
        this.createdAt = LocalDateTime.now();
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "ProductCreatedEvent{" +
               "productId=" + productId +
               ", userId=" + userId +
               ", title='" + title + '\'' +
               ", category='" + category + '\'' +
               ", price=" + price +
               ", createdAt=" + createdAt +
               '}';
    }
}