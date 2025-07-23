package com.sangsangplus.productservice.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestrator {
    
    private final SagaStateRepository sagaStateRepository;
    
    @Transactional
    public void startSaga(String sagaType, Object payload) {
        String sagaId = UUID.randomUUID().toString();
        
        SagaState sagaState = SagaState.builder()
                .sagaId(sagaId)
                .sagaType(sagaType)
                .status(SagaStatus.STARTED)
                .payload(payload.toString())
                .build();
        
        sagaStateRepository.save(sagaState);
        
        log.info("Saga started: {} with ID: {}", sagaType, sagaId);
        
        // 비동기로 Saga 실행
        CompletableFuture.runAsync(() -> executeSaga(sagaId, sagaType, payload))
                .orTimeout(30, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("Saga failed: {}", sagaId, ex);
                    compensateSaga(sagaId);
                    return null;
                });
    }
    
    private void executeSaga(String sagaId, String sagaType, Object payload) {
        try {
            switch (sagaType) {
                case "PRODUCT_ORDER":
                    executeProductOrderSaga(sagaId, payload);
                    break;
                case "USER_DELETION":
                    executeUserDeletionSaga(sagaId, payload);
                    break;
                default:
                    log.warn("Unknown saga type: {}", sagaType);
            }
        } catch (Exception e) {
            log.error("Error executing saga: {}", sagaId, e);
            compensateSaga(sagaId);
        }
    }
    
    private void executeProductOrderSaga(String sagaId, Object payload) {
        // 상품 주문 Saga 구현
        // 1. 재고 확인
        // 2. 결제 처리
        // 3. 주문 생성
        // 4. 알림 발송
    }
    
    private void executeUserDeletionSaga(String sagaId, Object payload) {
        // 사용자 삭제 Saga 구현
        // 1. 사용자의 모든 상품 삭제
        // 2. 관련 이미지 삭제
        // 3. 캐시 무효화
    }
    
    private void compensateSaga(String sagaId) {
        log.info("Compensating saga: {}", sagaId);
        // 보상 트랜잭션 실행
    }
}