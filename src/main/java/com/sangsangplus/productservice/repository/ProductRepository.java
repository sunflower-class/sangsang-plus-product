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
    
    // 사용자별 상품 조회
    Page<Product> findByUserId(Long userId, Pageable pageable);
    
    // 카테고리별 상품 조회
    Page<Product> findByCategory(String category, Pageable pageable);
    
    // 사용자와 카테고리로 상품 조회
    List<Product> findByUserIdAndCategory(Long userId, String category);
    
    // 제목 또는 설명으로 검색 (대소문자 구분 없음)
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 사용자 ID와 상품 ID로 조회 (권한 체크용)
    Optional<Product> findByProductIdAndUserId(Long productId, Long userId);
    
    // 모든 카테고리 목록 조회
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();
    
    // 가격 범위로 조회
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") Double minPrice, 
                                   @Param("maxPrice") Double maxPrice, 
                                   Pageable pageable);
    
    // 최근 생성된 상품 조회
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findRecentProducts(Pageable pageable);
    
    // 사용자별 상품 개수
    Long countByUserId(Long userId);
    
    // 카테고리별 상품 개수
    Long countByCategory(String category);
}