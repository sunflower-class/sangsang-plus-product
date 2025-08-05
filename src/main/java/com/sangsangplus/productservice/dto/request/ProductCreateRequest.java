package com.sangsangplus.productservice.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductCreateRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
    private String title;
    
    private String description;
    
    @NotBlank(message = "카테고리는 필수입니다")
    @Size(max = 100, message = "카테고리는 100자를 초과할 수 없습니다")
    private String category;
    
    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    @Digits(integer = 8, fraction = 2, message = "가격 형식이 올바르지 않습니다")
    private BigDecimal price;
    
    private List<ProductImageRequest> images;
    
    // Constructors
    public ProductCreateRequest() {}
    
    public ProductCreateRequest(String title, String description, String category, 
                               BigDecimal price, List<ProductImageRequest> images) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.images = images;
    }
    
    // Getters and Setters
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
    
    public List<ProductImageRequest> getImages() {
        return images;
    }
    
    public void setImages(List<ProductImageRequest> images) {
        this.images = images;
    }
    
    @Override
    public String toString() {
        return "ProductCreateRequest{" +
               "title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", category='" + category + '\'' +
               ", price=" + price +
               ", images=" + (images != null ? images.size() + " images" : "null") +
               '}';
    }
}