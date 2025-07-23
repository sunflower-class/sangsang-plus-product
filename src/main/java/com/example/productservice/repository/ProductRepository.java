package com.sangsangplus.productservice.repository;

import com.sangsangplus.productservice.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findByUserId(Long userId, Pageable pageable);
    
    Page<Product> findByCategory(String category, Pageable pageable);
    
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.productId = :productId")
    Optional<Product> findByIdWithImages(@Param("productId") Long productId);
    
    @Query("SELECT p FROM Product p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    List<Product> findByUserIdAndCategory(Long userId, String category);
    
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findAllCategories();
    
    void deleteByProductIdAndUserId(Long productId, Long userId);
    
    // 사용자 삭제 시 모든 상품 삭제를 위한 메서드
    void deleteAllByUserId(Long userId);
}
