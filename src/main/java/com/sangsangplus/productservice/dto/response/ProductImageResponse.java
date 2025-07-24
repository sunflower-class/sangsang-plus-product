package com.sangsangplus.productservice.dto.response;

import java.time.LocalDateTime;

public class ProductImageResponse {
    private Long imageId;
    private String url;
    private String altText;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    
    // Constructors
    public ProductImageResponse() {}
    
    public ProductImageResponse(Long imageId, String url, String altText, 
                               Integer displayOrder, LocalDateTime createdAt) {
        this.imageId = imageId;
        this.url = url;
        this.altText = altText;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getImageId() {
        return imageId;
    }
    
    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getAltText() {
        return altText;
    }
    
    public void setAltText(String altText) {
        this.altText = altText;
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
}