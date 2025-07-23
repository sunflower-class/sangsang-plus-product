package com.sangsangplus.productservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageRequest {
    
    @NotBlank(message = "이미지 URL은 필수입니다")
    @Size(max = 500, message = "URL은 500자를 초과할 수 없습니다")
    private String url;
    
    @Size(max = 255, message = "대체 텍스트는 255자를 초과할 수 없습니다")
    private String altText;
    
    private Integer displayOrder;
}