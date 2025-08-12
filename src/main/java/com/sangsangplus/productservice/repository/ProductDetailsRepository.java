package com.sangsangplus.productservice.repository;

import com.sangsangplus.productservice.domain.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {
    List<ProductDetails> findByProductIdOrderByDisplayOrderAsc(Long productId);
    Optional<ProductDetails> findByDetailId(Long detailId);
    void deleteByProductId(Long productId);
    void deleteByDetailId(Long detailId);
    boolean existsByProductId(Long productId);
    boolean existsByDetailId(Long detailId);
    
    @Query("SELECT COUNT(pd) FROM ProductDetails pd WHERE pd.productId = :productId")
    long countByProductId(Long productId);
    
    @Query("SELECT COALESCE(MAX(pd.displayOrder), 0) FROM ProductDetails pd WHERE pd.productId = :productId")
    Integer findMaxDisplayOrderByProductId(Long productId);
}