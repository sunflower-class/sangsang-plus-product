package com.sangsangplus.productservice.event.publisher;

import com.sangsangplus.productservice.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String PRODUCT_TOPIC = "product-events";
    
    public void publishProductCreatedEvent(ProductCreatedEvent event) {
        publishEvent(PRODUCT_TOPIC, event.getProductId().toString(), event);
    }
    
    public void publishProductUpdatedEvent(ProductUpdatedEvent event) {
        publishEvent(PRODUCT_TOPIC, event.getProductId().toString(), event);
    }
    
    public void publishProductDeletedEvent(ProductDeletedEvent event) {
        publishEvent(PRODUCT_TOPIC, event.getProductId().toString(), event);
    }
    
    public void publishProductImageAddedEvent(ProductImageAddedEvent event) {
        publishEvent(PRODUCT_TOPIC, event.getProductId().toString(), event);
    }
    
    public void publishProductImageRemovedEvent(ProductImageRemovedEvent event) {
        publishEvent(PRODUCT_TOPIC, event.getProductId().toString(), event);
    }
    
    private void publishEvent(String topic, String key, Object event) {
        CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(topic, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event published successfully: {} with key: {}", 
                        event.getClass().getSimpleName(), key);
            } else {
                log.error("Failed to publish event: {} with key: {}", 
                        event.getClass().getSimpleName(), key, ex);
            }
        });
    }
}