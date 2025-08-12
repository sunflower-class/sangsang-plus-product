package com.sangsangplus.productservice.repository;

import com.sangsangplus.productservice.domain.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {
    Optional<ProductDetails> findByProductId(Long productId);
    void deleteByProductId(Long productId);
    boolean existsByProductId(Long productId);
}