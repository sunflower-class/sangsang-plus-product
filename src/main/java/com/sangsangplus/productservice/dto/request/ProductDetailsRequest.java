package com.sangsangplus.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductDetailsRequest {
    
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    private String title;
    
    @NotBlank(message = "상품 상세 내용은 필수입니다")
    @Size(max = 1000000, message = "상품 상세 내용은 1,000,000자를 초과할 수 없습니다")
    private String content;
    
    private Integer displayOrder;
    
    public ProductDetailsRequest() {
    }
    
    public ProductDetailsRequest(String title, String content, Integer displayOrder) {
        this.title = title;
        this.content = content;
        this.displayOrder = displayOrder;
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
}