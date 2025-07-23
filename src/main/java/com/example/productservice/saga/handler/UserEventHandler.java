package com.sangsangplus.productservice.saga.handler;

import com.sangsangplus.productservice.repository.ProductRepository;
import com.sangsangplus.productservice.saga.SagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventHandler {
    
    private final ProductRepository productRepository;
    private final SagaOrchestrator sagaOrchestrator;
    
    @KafkaListener(topics = "user-events", groupId = "product-service-saga")
    public void handleUserEvent(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        
        if ("USER_DELETED".equals(eventType)) {
            Long userId = ((Number) event.get("userId")).longValue();
            handleUserDeleted(userId);
        }
    }
    
    @Transactional
    public void handleUserDeleted(Long userId) {
        log.info("Starting saga for user deletion: userId={}", userId);
        
        // 해당 사용자의 모든 상품 삭제
        productRepository.deleteAllByUserId(userId);
        
        Map<String, Object> sagaPayload = Map.of(
                "userId", userId,
                "action", "DELETE_USER_PRODUCTS"
        );
        
        sagaOrchestrator.startSaga("USER_DELETION", sagaPayload);
    }
}