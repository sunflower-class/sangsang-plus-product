package com.sangsangplus.productservice.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_images")
public class ProductImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false, length = 500)
    private String url;
    
    @Column(name = "alt_text", length = 255)
    private String altText;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    // Constructors
    public ProductImage() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ProductImage(Product product, String url, String altText, Integer displayOrder) {
        this.product = product;
        this.url = url;
        this.altText = altText;
        this.displayOrder = displayOrder;
        this.createdAt = LocalDateTime.now();
    }
    
    // 비즈니스 메서드
    public void updateImage(String url, String altText, Integer displayOrder) {
        this.url = url;
        this.altText = altText;
        this.displayOrder = displayOrder;
    }
    
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public Long getImageId() {
        return imageId;
    }
    
    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}