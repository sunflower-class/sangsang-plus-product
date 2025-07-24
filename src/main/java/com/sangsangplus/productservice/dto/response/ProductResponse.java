package com.sangsangplus.productservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductResponse {
    private Long productId;
    private Long userId;
    private String userEmail;
    private String userName;
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductImageResponse> images;
    
    // Constructors
    public ProductResponse() {}
    
    public ProductResponse(Long productId, Long userId, String userEmail, String userName,
                          String title, String description, String category, BigDecimal price,
                          LocalDateTime createdAt, LocalDateTime updatedAt, 
                          List<ProductImageResponse> images) {
        this.productId = productId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.images = images;
    }
    
    // Builder pattern
    public static ProductResponseBuilder builder() {
        return new ProductResponseBuilder();
    }
    
    public static class ProductResponseBuilder {
        private Long productId;
        private Long userId;
        private String userEmail;
        private String userName;
        private String title;
        private String description;
        private String category;
        private BigDecimal price;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<ProductImageResponse> images;
        
        public ProductResponseBuilder productId(Long productId) {
            this.productId = productId;
            return this;
        }
        
        public ProductResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public ProductResponseBuilder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }
        
        public ProductResponseBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }
        
        public ProductResponseBuilder title(String title) {
            this.title = title;
            return this;
        }
        
        public ProductResponseBuilder description(String description) {
            this.description = description;
            return this;
        }
        
        public ProductResponseBuilder category(String category) {
            this.category = category;
            return this;
        }
        
        public ProductResponseBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }
        
        public ProductResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public ProductResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public ProductResponseBuilder images(List<ProductImageResponse> images) {
            this.images = images;
            return this;
        }
        
        public ProductResponse build() {
            return new ProductResponse(productId, userId, userEmail, userName, title, 
                                     description, category, price, createdAt, updatedAt, images);
        }
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
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<ProductImageResponse> getImages() {
        return images;
    }
    
    public void setImages(List<ProductImageResponse> images) {
        this.images = images;
    }
}