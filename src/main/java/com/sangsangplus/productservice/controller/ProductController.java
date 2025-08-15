package com.sangsangplus.productservice.controller;

import com.sangsangplus.productservice.dto.request.ProductCreateRequest;
import com.sangsangplus.productservice.dto.request.ProductDetailsRequest;
import com.sangsangplus.productservice.dto.request.ProductImageRequest;
import com.sangsangplus.productservice.dto.request.ProductUpdateRequest;
import com.sangsangplus.productservice.dto.response.PageResponse;
import com.sangsangplus.productservice.dto.response.ProductDetailsResponse;
import com.sangsangplus.productservice.dto.response.ProductResponse;
import com.sangsangplus.productservice.service.command.ProductCommandService;
import com.sangsangplus.productservice.service.query.ProductQueryService;
import com.sangsangplus.productservice.service.ProductDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.sangsangplus.productservice.util.UuidValidator;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "상품 관리 API")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductCommandService commandService;
    private final ProductQueryService queryService;
    private final ProductDetailsService productDetailsService;
    
    public ProductController(ProductCommandService commandService, 
                             ProductQueryService queryService,
                             ProductDetailsService productDetailsService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.productDetailsService = productDetailsService;
    }
    
    // Health check endpoint
    @GetMapping("/health")
    @Operation(summary = "헬스체크", description = "서비스 상태를 확인합니다")
    @ApiResponse(responseCode = "200", description = "서비스 정상 작동")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
    
    // Query endpoints - 인증 없이 접근 가능
    @GetMapping
    @Operation(summary = "전체 상품 조회", description = "페이징을 지원하는 전체 상품 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공")
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @Parameter(description = "페이징 정보") @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ProductResponse> response = queryService.getAllProducts(pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = queryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<ProductResponse>> getRecentProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductResponse> response = queryService.getRecentProducts(limit);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ProductResponse> response = queryService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByUser(
            @PathVariable UUID userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ProductResponse> response = queryService.getProductsByUserId(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByCategory(
            @PathVariable String category,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ProductResponse> response = queryService.getProductsByCategory(category, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{productId}")
    @Operation(summary = "특정 상품 조회", description = "상품 ID로 특정 상품의 상세 정보를 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상품 조회 성공"),
        @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "상품 ID") @PathVariable Long productId) {
        ProductResponse response = queryService.getProduct(productId);
        return ResponseEntity.ok(response);
    }
    
    // Command endpoints - 인증 필요
    @PostMapping
    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다 (서버간 통신용)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "상품 등록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(description = "상품 등록 정보") @Valid @RequestBody ProductCreateRequest request,
            HttpServletRequest httpRequest) {
        
        logger.info("=== CREATE PRODUCT REQUEST START ===");
        logger.info("Request body: {}", request);
        
        // 헤더에서 user_id 추출 및 검증 (서버간 통신용)
        String userIdHeader = httpRequest.getHeader("X-User-Id");
        UUID userId = null;
        
        if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
            String trimmedUserId = userIdHeader.trim();
            logger.info("Received X-User-Id header: '{}'", trimmedUserId);
            
            // UUID 형식 검증 및 파싱
            if (UuidValidator.isValidUuid(trimmedUserId)) {
                userId = UuidValidator.parseUuid(trimmedUserId);
                logger.info("Successfully parsed User ID: {}", userId);
            } else {
                logger.error("Invalid UUID format in X-User-Id header: '{}'", trimmedUserId);
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            logger.warn("X-User-Id header is missing or empty");
            // user_id가 없어도 요청을 처리할지, 에러를 반환할지는 비즈니스 요구사항에 따라 결정
            // 현재는 null로 처리하여 서비스에서 판단하도록 함
        }
        
        try {
            ProductResponse response = commandService.createProduct(userId, null, request);
            logger.info("Product created successfully: {}", response);
            logger.info("=== CREATE PRODUCT REQUEST END ===\n");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating product: ", e);
            logger.info("=== CREATE PRODUCT REQUEST END (ERROR) ===\n");
            throw e;
        }
    }
    
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @AuthenticationPrincipal UUID userId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse response = commandService.updateProduct(userId, productId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal UUID userId,
            @PathVariable Long productId) {
        commandService.deleteProduct(userId, productId);
        return ResponseEntity.noContent().build();
    }

    // Product Images Related
    @PostMapping("/{productId}/images")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> addProductImage(
            @AuthenticationPrincipal UUID userId,
            @PathVariable Long productId,
            @Valid @RequestBody ProductImageRequest request) {
        ProductResponse response = commandService.addProductImage(userId, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @DeleteMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> removeProductImage(
            @AuthenticationPrincipal UUID userId,
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        commandService.removeProductImage(userId, productId, imageId);
        return ResponseEntity.noContent().build();
    }
    
    // Product Details Related
    @GetMapping("/{productId}/details")
    @Operation(summary = "상품 상세 정보 목록 조회", description = "상품의 모든 상세 정보를 순서대로 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상품 상세 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    public ResponseEntity<List<ProductDetailsResponse>> getProductDetailsList(
            @Parameter(description = "상품 ID") @PathVariable Long productId) {
        List<ProductDetailsResponse> response = productDetailsService.getProductDetailsList(productId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/details/{detailId}")
    @Operation(summary = "특정 상품 상세 정보 조회", description = "detailId로 특정 상세 정보를 조회합니다")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상품 상세 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "상품 상세 정보를 찾을 수 없음")
    })
    public ResponseEntity<ProductDetailsResponse> getProductDetail(
            @Parameter(description = "상세 정보 ID") @PathVariable Long detailId) {
        ProductDetailsResponse response = productDetailsService.getProductDetail(detailId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{productId}/details")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "상품 상세 정보 생성", description = "상품에 새로운 상세 정보 섹션을 추가합니다",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "상품 상세 정보 생성 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    public ResponseEntity<ProductDetailsResponse> createProductDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "상품 상세 정보") @Valid @RequestBody ProductDetailsRequest request) {
        ProductDetailsResponse response = productDetailsService.createProductDetail(userId, productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/details/{detailId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "상품 상세 정보 수정", description = "특정 상세 정보를 수정합니다",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상품 상세 정보 수정 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "상품 상세 정보를 찾을 수 없음")
    })
    public ResponseEntity<ProductDetailsResponse> updateProductDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @Parameter(description = "상세 정보 ID") @PathVariable Long detailId,
            @Parameter(description = "상품 상세 정보") @Valid @RequestBody ProductDetailsRequest request) {
        ProductDetailsResponse response = productDetailsService.updateProductDetail(userId, detailId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/details/{detailId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "상품 상세 정보 삭제", description = "특정 상세 정보를 삭제합니다",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "상품 상세 정보 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "상품 상세 정보를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteProductDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @Parameter(description = "상세 정보 ID") @PathVariable Long detailId) {
        productDetailsService.deleteProductDetail(userId, detailId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{productId}/details")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "상품의 모든 상세 정보 삭제", description = "상품의 모든 상세 정보를 삭제합니다",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "상품 상세 정보 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteAllProductDetails(
            @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
            @Parameter(description = "상품 ID") @PathVariable Long productId) {
        productDetailsService.deleteAllProductDetails(userId, productId);
        return ResponseEntity.noContent().build();
    }
    
    // My products - 현재 로그인한 사용자의 상품
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PageResponse<ProductResponse>> getMyProducts(
            @AuthenticationPrincipal UUID userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ProductResponse> response = queryService.getProductsByUserId(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my/category/{category}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponse>> getMyProductsByCategory(
            @AuthenticationPrincipal UUID userId,
            @PathVariable String category) {
        List<ProductResponse> response = queryService.getProductsByUserAndCategory(userId, category);
        return ResponseEntity.ok(response);
    }
    
    // Admin endpoints
    @PutMapping("/admin/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> adminUpdateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse response = commandService.adminUpdateProduct(productId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/admin/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDeleteProduct(
            @PathVariable Long productId) {
        commandService.adminDeleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
    
    // Admin Product Details endpoints
    @PostMapping("/admin/{productId}/details")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 상품 상세 정보 생성", description = "관리자 권한으로 상품 상세 정보를 생성합니다",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductDetailsResponse> adminCreateProductDetail(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "상품 상세 정보") @Valid @RequestBody ProductDetailsRequest request) {
        ProductDetailsResponse response = productDetailsService.adminCreateProductDetail(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/admin/details/{detailId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 상품 상세 정보 수정", description = "관리자 권한으로 상품 상세 정보를 수정합니다",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductDetailsResponse> adminUpdateProductDetail(
            @Parameter(description = "상세 정보 ID") @PathVariable Long detailId,
            @Parameter(description = "상품 상세 정보") @Valid @RequestBody ProductDetailsRequest request) {
        ProductDetailsResponse response = productDetailsService.adminUpdateProductDetail(detailId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/admin/details/{detailId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 상품 상세 정보 삭제", description = "관리자 권한으로 특정 상품 상세 정보를 삭제합니다",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> adminDeleteProductDetail(
            @Parameter(description = "상세 정보 ID") @PathVariable Long detailId) {
        productDetailsService.adminDeleteProductDetail(detailId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/admin/{productId}/details")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 상품의 모든 상세 정보 삭제", description = "관리자 권한으로 상품의 모든 상세 정보를 삭제합니다",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> adminDeleteAllProductDetails(
            @Parameter(description = "상품 ID") @PathVariable Long productId) {
        productDetailsService.adminDeleteAllProductDetails(productId);
        return ResponseEntity.noContent().build();
    }
    
}