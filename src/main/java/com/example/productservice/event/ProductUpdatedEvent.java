package com.sangsangplus.productservice.event;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdatedEvent extends BaseEvent {
    private Long productId;
    private Long userId;
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    
    public ProductUpdatedEvent(Long productId, Long userId, String title, 
                              String description, String category, BigDecimal price) {
        super("PRODUCT_UPDATED");
        this.productId = productId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
    }
}