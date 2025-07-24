package com.sangsangplus.productservice.repository;

import com.sangsangplus.productservice.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    // 상품별 이미지 조회 (표시 순서대로)
    List<ProductImage> findByProductProductIdOrderByDisplayOrder(Long productId);
    
    // 상품의 특정 이미지 조회
    Optional<ProductImage> findByImageIdAndProductProductId(Long imageId, Long productId);
    
    // 상품의 이미지 개수
    Long countByProductProductId(Long productId);
    
    // 상품의 모든 이미지 삭제
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.productId = :productId")
    void deleteAllByProductId(@Param("productId") Long productId);
    
    // 표시 순서 업데이트
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.displayOrder = :displayOrder WHERE pi.imageId = :imageId")
    void updateDisplayOrder(@Param("imageId") Long imageId, @Param("displayOrder") Integer displayOrder);
    
    // 상품의 최대 표시 순서 조회
    @Query("SELECT COALESCE(MAX(pi.displayOrder), 0) FROM ProductImage pi WHERE pi.product.productId = :productId")
    Integer findMaxDisplayOrderByProductId(@Param("productId") Long productId);
}