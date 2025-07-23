package com.sangsangplus.productservice.event;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreatedEvent extends BaseEvent {
    private Long productId;
    private Long userId;
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private List<ProductImageInfo> images;
    
    public ProductCreatedEvent(Long productId, Long userId, String title, 
                              String description, String category, BigDecimal price, 
                              List<ProductImageInfo> images) {
        super("PRODUCT_CREATED");
        this.productId = productId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.images = images;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductImageInfo {
        private Long imageId;
        private String url;
        private String altText;
        private Integer displayOrder;
    }
}