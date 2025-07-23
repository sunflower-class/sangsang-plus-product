package com.sangsangplus.productservice.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponse {
    private Long imageId;
    private String url;
    private String altText;
    private Integer displayOrder;
    private LocalDateTime createdAt;
}