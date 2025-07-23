package com.sangsangplus.productservice.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventListener {
    
    private final ObjectMapper objectMapper;
    
    @KafkaListener(topics = "user-events", groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserEvents(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            String eventType = (String) event.get("eventType");
            
            switch (eventType) {
                case "USER_DELETED":
                    handleUserDeleted(event);
                    break;
                case "USER_SUSPENDED":
                    handleUserSuspended(event);
                    break;
                default:
                    log.debug("Unhandled event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing user event: {}", message, e);
        }
    }
    
    private void handleUserDeleted(Map<String, Object> event) {
        Long userId = ((Number) event.get("userId")).longValue();
        log.info("Handling user deleted event for userId: {}", userId);
        // Saga 패턴: 사용자 삭제 시 해당 사용자의 모든 상품 삭제 처리
        // 실제 구현은 서비스 레이어에서 처리
    }
    
    private void handleUserSuspended(Map<String, Object> event) {
        Long userId = ((Number) event.get("userId")).longValue();
        log.info("Handling user suspended event for userId: {}", userId);
        // 사용자 정지 시 상품 숨김 처리 등
    }
}