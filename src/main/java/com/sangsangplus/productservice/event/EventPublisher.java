package com.sangsangplus.productservice.event;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    
    private final EventHubProducerClient eventHubProducerClient;
    private final ObjectMapper objectMapper;
    
    public EventPublisher(EventHubProducerClient eventHubProducerClient, ObjectMapper objectMapper) {
        this.eventHubProducerClient = eventHubProducerClient;
        this.objectMapper = objectMapper;
    }
    
    public void publishProductCreated(ProductCreatedEvent event) {
        try {
            String eventData = objectMapper.writeValueAsString(event);
            EventData eventDataMessage = new EventData(eventData);
            eventDataMessage.getProperties().put("eventType", "ProductCreated");
            
            eventHubProducerClient.send(java.util.Collections.singletonList(eventDataMessage));
        } catch (Exception e) {
            // 로깅만 하고 비즈니스 로직은 계속 진행
            System.err.println("Failed to publish ProductCreated event: " + e.getMessage());
        }
    }
    
    public void publishProductUpdated(ProductUpdatedEvent event) {
        try {
            String eventData = objectMapper.writeValueAsString(event);
            EventData eventDataMessage = new EventData(eventData);
            eventDataMessage.getProperties().put("eventType", "ProductUpdated");
            
            eventHubProducerClient.send(java.util.Collections.singletonList(eventDataMessage));
        } catch (Exception e) {
            System.err.println("Failed to publish ProductUpdated event: " + e.getMessage());
        }
    }
    
    public void publishProductDeleted(ProductDeletedEvent event) {
        try {
            String eventData = objectMapper.writeValueAsString(event);
            EventData eventDataMessage = new EventData(eventData);
            eventDataMessage.getProperties().put("eventType", "ProductDeleted");
            
            eventHubProducerClient.send(java.util.Collections.singletonList(eventDataMessage));
        } catch (Exception e) {
            System.err.println("Failed to publish ProductDeleted event: " + e.getMessage());
        }
    }
}