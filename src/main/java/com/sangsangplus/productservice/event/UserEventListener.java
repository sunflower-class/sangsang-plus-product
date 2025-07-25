package com.sangsangplus.productservice.event;

import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class UserEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserEventListener.class);
    
    private final EventHubConsumerClient eventHubConsumerClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;
    private volatile boolean isRunning = false;

    public UserEventListener(EventHubConsumerClient eventHubConsumerClient, ObjectMapper objectMapper) {
        this.eventHubConsumerClient = eventHubConsumerClient;
        this.objectMapper = objectMapper;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    public void startListening() {
        logger.info("Event Hub listener disabled for API testing - skipping user event listener startup");
        
        // Temporarily disabled to prevent pod crashes due to missing consumer group
        // TODO: Re-enable after creating 'product-service-group' consumer group in Azure Event Hub
        /*
        isRunning = true;
        logger.info("Starting User Event Listener...");
        
        CompletableFuture.runAsync(() -> {
            try {
                while (isRunning) {
                    for (PartitionEvent partitionEvent : eventHubConsumerClient.receiveFromPartition("0", 100, EventPosition.latest(), Duration.ofSeconds(5))) {
                        if (isRunning) {
                            handleEvent(partitionEvent);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error in User Event Listener", e);
            }
        }, executorService);
        
        logger.info("User Event Listener started successfully");
        */
    }

    private void handleEvent(PartitionEvent partitionEvent) {
        try {
            String eventData = partitionEvent.getData().getBodyAsString();
            String eventType = (String) partitionEvent.getData().getProperties().get("eventType");
            
            logger.info("Received user event - Type: {}, Data: {}", eventType, eventData);
            
            switch (eventType) {
                case "UserCreated":
                    handleUserCreated(eventData);
                    break;
                case "UserUpdated":
                    handleUserUpdated(eventData);
                    break;
                case "UserDeleted":
                    handleUserDeleted(eventData);
                    break;
                default:
                    logger.warn("Unknown event type: {}", eventType);
            }
            
        } catch (Exception e) {
            logger.error("Failed to process user event", e);
        }
    }

    private void handleUserCreated(String eventData) {
        try {
            // UserCreatedEvent userEvent = objectMapper.readValue(eventData, UserCreatedEvent.class);
            logger.info("Processing UserCreated event: {}", eventData);
            
            // 필요한 경우 사용자 정보를 로컬 캐시에 저장하거나 
            // 사용자별 상품 카테고리 초기화 등의 작업 수행
            
        } catch (Exception e) {
            logger.error("Failed to handle UserCreated event", e);
        }
    }

    private void handleUserUpdated(String eventData) {
        try {
            // UserUpdatedEvent userEvent = objectMapper.readValue(eventData, UserUpdatedEvent.class);
            logger.info("Processing UserUpdated event: {}", eventData);
            
            // 사용자 정보 업데이트가 필요한 경우 처리
            // 예: 사용자가 등록한 상품의 사용자 정보 업데이트
            
        } catch (Exception e) {
            logger.error("Failed to handle UserUpdated event", e);
        }
    }

    private void handleUserDeleted(String eventData) {
        try {
            // UserDeletedEvent userEvent = objectMapper.readValue(eventData, UserDeletedEvent.class);
            logger.info("Processing UserDeleted event: {}", eventData);
            
            // 사용자 삭제 시 해당 사용자의 상품들 처리
            // 예: 상품 상태를 비활성화하거나 삭제
            
        } catch (Exception e) {
            logger.error("Failed to handle UserDeleted event", e);
        }
    }

    @PreDestroy
    public void stopListening() {
        logger.info("Stopping User Event Listener...");
        isRunning = false;
        
        try {
            if (eventHubConsumerClient != null) {
                eventHubConsumerClient.close();
            }
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
        } catch (Exception e) {
            logger.error("Error while stopping User Event Listener", e);
        }
        
        logger.info("User Event Listener stopped");
    }
}