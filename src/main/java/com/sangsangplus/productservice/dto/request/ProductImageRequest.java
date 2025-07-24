package com.sangsangplus.productservice.dto.request;

import jakarta.validation.constraints.*;

public class ProductImageRequest {
    
    @NotBlank(message = "이미지 URL은 필수입니다")
    @Size(max = 500, message = "이미지 URL은 500자를 초과할 수 없습니다")
    private String url;
    
    @Size(max = 255, message = "대체 텍스트는 255자를 초과할 수 없습니다")
    private String altText;
    
    @Min(value = 0, message = "표시 순서는 0 이상이어야 합니다")
    private Integer displayOrder;
    
    // Constructors
    public ProductImageRequest() {}
    
    public ProductImageRequest(String url, String altText, Integer displayOrder) {
        this.url = url;
        this.altText = altText;
        this.displayOrder = displayOrder;
    }
    
    // Getters and Setters
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
}