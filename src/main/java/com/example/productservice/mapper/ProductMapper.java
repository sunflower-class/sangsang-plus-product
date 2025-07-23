package com.sangsangplus.productservice.mapper;

import com.sangsangplus.productservice.domain.entity.Product;
import com.sangsangplus.productservice.domain.entity.ProductImage;
import com.sangsangplus.productservice.dto.response.ProductImageResponse;
import com.sangsangplus.productservice.dto.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(source = "images", target = "images")
    ProductResponse toResponse(Product product);
    
    ProductImageResponse toImageResponse(ProductImage productImage);
    
    List<ProductResponse> toResponseList(List<Product> products);
}