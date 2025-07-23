package com.sangsangplus.productservice.repository;

import com.sangsangplus.productservice.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    List<ProductImage> findByProductProductIdOrderByDisplayOrder(Long productId);
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.productId = :productId AND pi.imageId = :imageId")
    Optional<ProductImage> findByProductIdAndImageId(@Param("productId") Long productId, 
                                                      @Param("imageId") Long imageId);
    
    void deleteByImageIdAndProductProductId(Long imageId, Long productId);
    
    @Query("SELECT MAX(pi.displayOrder) FROM ProductImage pi WHERE pi.product.productId = :productId")
    Integer findMaxDisplayOrderByProductId(@Param("productId") Long productId);
}