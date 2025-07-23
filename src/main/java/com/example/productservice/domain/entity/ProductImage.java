package com.sangsangplus.productservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    // 비즈니스 메서드
    public void updateImage(String url, String altText, Integer displayOrder) {
        this.url = url;
        this.altText = altText;
        this.displayOrder = displayOrder;
    }
}