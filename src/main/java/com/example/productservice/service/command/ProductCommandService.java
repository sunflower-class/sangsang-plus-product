package com.sangsangplus.productservice.service.command;

import com.sangsangplus.productservice.client.UserServiceClient;
import com.sangsangplus.productservice.domain.entity.Product;
import com.sangsangplus.productservice.domain.entity.ProductImage;
import com.sangsangplus.productservice.dto.external.UserDto;
import com.sangsangplus.productservice.dto.request.ProductCreateRequest;
import com.sangsangplus.productservice.dto.request.ProductImageRequest;
import com.sangsangplus.productservice.dto.request.ProductUpdateRequest;
import com.sangsangplus.productservice.dto.response.ProductResponse;
import com.sangsangplus.productservice.event.*;
import com.sangsangplus.productservice.event.publisher.EventPublisher;
import com.sangsangplus.productservice.exception.ProductNotFoundException;
import com.sangsangplus.productservice.exception.UnauthorizedException;
import com.sangsangplus.productservice.mapper.ProductMapper;
import com.sangsangplus.productservice.repository.ProductImageRepository;
import com.sangsangplus.productservice.repository.ProductRepository;
import com.sangsangplus.productservice.security.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductCommandService {
    
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;
    private final EventPublisher eventPublisher;
    private final UserServiceClient userServiceClient;
    
    public ProductResponse createProduct(Long userId, String token, ProductCreateRequest request) {
        // User 서비스에서 사용자 정보 조회
        UserDto userDto = userServiceClient.getUserById(userId, token)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        // Product 엔티티 생성
        Product product = Product.builder()
                .userId(userId)
                .userEmail(userDto.getEmail())
                .userName(userDto.getName())
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
        
        // 이미지 추가
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            int order = 0;
            for (ProductImageRequest imageRequest : request.getImages()) {
                ProductImage image = ProductImage.builder()
                        .url(imageRequest.getUrl())
                        .altText(imageRequest.getAltText())
                        .displayOrder(imageRequest.getDisplayOrder() != null ? 
                                    imageRequest.getDisplayOrder() : order++)
                        .build();
                product.addImage(image);
            }
        }
        
        Product savedProduct = productRepository.save(product);
        
        // 이벤트 발행
        List<ProductCreatedEvent.ProductImageInfo> imageInfos = savedProduct.getImages()
                .stream()
                .map(img -> ProductCreatedEvent.ProductImageInfo.builder()
                        .imageId(img.getImageId())
                        .url(img.getUrl())
                        .altText(img.getAltText())
                        .displayOrder(img.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());
        
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .productId(savedProduct.getProductId())
                .userId(savedProduct.getUserId())
                .title(savedProduct.getTitle())
                .description(savedProduct.getDescription())
                .category(savedProduct.getCategory())
                .price(savedProduct.getPrice())
                .images(imageInfos)
                .build();
        
        eventPublisher.publishProductCreatedEvent(event);
        
        return productMapper.toResponse(savedProduct);
    }
    
    public ProductResponse updateProduct(Long userId, Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        // 권한 검증 - 소유자만 수정 가능
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to update this product");
        }
        
        // 업데이트
        product.updateProduct(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getPrice()
        );
        
        Product updatedProduct = productRepository.save(product);
        
        // 이벤트 발행
        ProductUpdatedEvent event = ProductUpdatedEvent.builder()
                .productId(updatedProduct.getProductId())
                .userId(updatedProduct.getUserId())
                .title(updatedProduct.getTitle())
                .description(updatedProduct.getDescription())
                .category(updatedProduct.getCategory())
                .price(updatedProduct.getPrice())
                .build();
        
        eventPublisher.publishProductUpdatedEvent(event);
        
        return productMapper.toResponse(updatedProduct);
    }
    
    public void deleteProduct(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        // 권한 검증
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to delete this product");
        }
        
        productRepository.delete(product);
        
        // 이벤트 발행
        ProductDeletedEvent event = new ProductDeletedEvent(productId, userId);
        eventPublisher.publishProductDeletedEvent(event);
    }
    
    // 관리자 전용 메서드들
    public ProductResponse adminUpdateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        product.updateProduct(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getPrice()
        );
        
        Product updatedProduct = productRepository.save(product);
        
        // 이벤트 발행
        ProductUpdatedEvent event = ProductUpdatedEvent.builder()
                .productId(updatedProduct.getProductId())
                .userId(updatedProduct.getUserId())
                .title(updatedProduct.getTitle())
                .description(updatedProduct.getDescription())
                .category(updatedProduct.getCategory())
                .price(updatedProduct.getPrice())
                .build();
        
        eventPublisher.publishProductUpdatedEvent(event);
        
        return productMapper.toResponse(updatedProduct);
    }
    
    public void adminDeleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        Long userId = product.getUserId();
        productRepository.delete(product);
        
        // 이벤트 발행
        ProductDeletedEvent event = new ProductDeletedEvent(productId, userId);
        eventPublisher.publishProductDeletedEvent(event);
    }
    
    public ProductResponse addProductImage(Long userId, Long productId, ProductImageRequest request) {
        Product product = productRepository.findByIdWithImages(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        // 권한 검증
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to add image to this product");
        }
        
        // 표시 순서 설정
        Integer displayOrder = request.getDisplayOrder();
        if (displayOrder == null) {
            Integer maxOrder = productImageRepository.findMaxDisplayOrderByProductId(productId);
            displayOrder = (maxOrder != null ? maxOrder + 1 : 0);
        }
        
        ProductImage image = ProductImage.builder()
                .url(request.getUrl())
                .altText(request.getAltText())
                .displayOrder(displayOrder)
                .build();
        
        product.addImage(image);
        Product savedProduct = productRepository.save(product);
        
        // 이벤트 발행
        ProductImageAddedEvent event = ProductImageAddedEvent.builder()
                .productId(productId)
                .imageId(image.getImageId())
                .url(image.getUrl())
                .altText(image.getAltText())
                .displayOrder(image.getDisplayOrder())
                .build();
        
        eventPublisher.publishProductImageAddedEvent(event);
        
        return productMapper.toResponse(savedProduct);
    }
    
    public void removeProductImage(Long userId, Long productId, Long imageId) {
        Product product = productRepository.findByIdWithImages(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        // 권한 검증
        if (!product.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to remove image from this product");
        }
        
        ProductImage imageToRemove = product.getImages().stream()
                .filter(img -> img.getImageId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Image not found with id: " + imageId));
        
        product.removeImage(imageToRemove);
        productRepository.save(product);
        
        // 이벤트 발행
        ProductImageRemovedEvent event = new ProductImageRemovedEvent(productId, imageId);
        eventPublisher.publishProductImageRemovedEvent(event);
    }
}