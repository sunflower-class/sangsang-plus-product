package com.sangsangplus.productservice.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageAddedEvent extends BaseEvent {
    private Long productId;
    private Long imageId;
    private String url;
    private String altText;
    private Integer displayOrder;
    
    public ProductImageAddedEvent(Long productId, Long imageId, String url, 
                                 String altText, Integer displayOrder) {
        super("PRODUCT_IMAGE_ADDED");
        this.productId = productId;
        this.imageId = imageId;
        this.url = url;
        this.altText = altText;
        this.displayOrder = displayOrder;
    }
}