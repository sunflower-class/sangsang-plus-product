package com.sangsangplus.productservice.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDeletedEvent extends BaseEvent {
    private Long productId;
    private Long userId;
    
    public ProductDeletedEvent(Long productId, Long userId) {
        super("PRODUCT_DELETED");
        this.productId = productId;
        this.userId = userId;
    }
}