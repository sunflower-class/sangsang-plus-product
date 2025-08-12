package com.sangsangplus.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductDetailsRequest {
    
    @NotBlank(message = "상품 상세 내용은 필수입니다")
    @Size(max = 100000, message = "상품 상세 내용은 100,000자를 초과할 수 없습니다")
    private String content;
    
    public ProductDetailsRequest() {
    }
    
    public ProductDetailsRequest(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}