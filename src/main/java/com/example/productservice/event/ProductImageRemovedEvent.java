package com.sangsangplus.productservice.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageRemovedEvent extends BaseEvent {
    private Long productId;
    private Long imageId;
    
    public ProductImageRemovedEvent(Long productId, Long imageId) {
        super("PRODUCT_IMAGE_REMOVED");
        this.productId = productId;
        this.imageId = imageId;
    }
}