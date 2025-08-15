package com.sangsangplus.productservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductResponse {
    private Long productId;
    private UUID userId;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductImageResponse> images;
    private String brand;
    private String source;
    private String status;
    private String metadata;
    
    // Constructors
    public ProductResponse() {}
    
    public ProductResponse(Long productId, UUID userId, String name, String description, 
                          String category, BigDecimal price, LocalDateTime createdAt, 
                          LocalDateTime updatedAt, List<ProductImageResponse> images,
                          String brand, String source, String status, String metadata) {
        this.productId = productId;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.images = images;
        this.brand = brand;
        this.source = source;
        this.status = status;
        this.metadata = metadata;
    }
    
    // Builder pattern
    public static ProductResponseBuilder builder() {
        return new ProductResponseBuilder();
    }
    
    public static class ProductResponseBuilder {
        private Long productId;
        private UUID userId;
        private String name;
        private String description;
        private String category;
        private BigDecimal price;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<ProductImageResponse> images;
        private String brand;
        private String source;
        private String status;
        private String metadata;
        
        public ProductResponseBuilder productId(Long productId) {
            this.productId = productId;
            return this;
        }
        
        public ProductResponseBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        
        
        public ProductResponseBuilder name(String name) {
            this.name = name;
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
        
        public ProductResponseBuilder brand(String brand) {
            this.brand = brand;
            return this;
        }
        
        public ProductResponseBuilder source(String source) {
            this.source = source;
            return this;
        }
        
        public ProductResponseBuilder status(String status) {
            this.status = status;
            return this;
        }
        
        public ProductResponseBuilder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }
        
        public ProductResponse build() {
            return new ProductResponse(productId, userId, name, description, 
                                     category, price, createdAt, updatedAt, images,
                                     brand, source, status, metadata);
        }
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
    
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}