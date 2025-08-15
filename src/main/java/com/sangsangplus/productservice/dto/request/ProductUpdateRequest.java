package com.sangsangplus.productservice.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductUpdateRequest {
    
    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 255, message = "상품명은 255자를 초과할 수 없습니다")
    private String name;
    
    private String description;
    
    @NotBlank(message = "카테고리는 필수입니다")
    @Size(max = 100, message = "카테고리는 100자를 초과할 수 없습니다")
    private String category;
    
    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    @Digits(integer = 8, fraction = 2, message = "가격 형식이 올바르지 않습니다")
    private BigDecimal price;
    
    private String brand;
    
    private String status;
    
    // Constructors
    public ProductUpdateRequest() {}
    
    public ProductUpdateRequest(String name, String description, String category, BigDecimal price, String brand, String status) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.brand = brand;
        this.status = status;
    }
    
    // Getters and Setters
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
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}