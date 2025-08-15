package com.sangsangplus.productservice.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductCreateRequest {
    
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
    
    private List<ProductImageRequest> images;
    
    private String brand;
    
    private String source;
    
    private String status;
    
    private String metadata;
    
    // Constructors
    public ProductCreateRequest() {}
    
    public ProductCreateRequest(String name, String description, String category, 
                               BigDecimal price, List<ProductImageRequest> images, 
                               String brand, String source, String status, String metadata) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.images = images;
        this.brand = brand;
        this.source = source;
        this.status = status;
        this.metadata = metadata;
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
    
    public List<ProductImageRequest> getImages() {
        return images;
    }
    
    public void setImages(List<ProductImageRequest> images) {
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
    
    @Override
    public String toString() {
        return "ProductCreateRequest{" +
               "name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", category='" + category + '\'' +
               ", price=" + price +
               ", brand='" + brand + '\'' +
               ", source='" + source + '\'' +
               ", status='" + status + '\'' +
               ", metadata='" + metadata + '\'' +
               ", images=" + (images != null ? images.size() + " images" : "null") +
               '}';
    }
}