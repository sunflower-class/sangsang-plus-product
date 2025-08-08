package com.sangsangplus.productservice.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventHubConfig {

    // 상품 이벤트 발행용
    @Value("${azure.eventhub.producer.connection-string}")
    private String producerConnectionString;

    @Value("${azure.eventhub.producer.name}")
    private String producerEventHubName;

    // 유저 이벤트 수신용
    @Value("${azure.eventhub.consumer.connection-string}")
    private String consumerConnectionString;

    @Value("${azure.eventhub.consumer.name}")
    private String consumerEventHubName;

    @Value("${azure.eventhub.consumer.consumer-group}")
    private String consumerGroup;

    @Bean
    public EventHubProducerClient eventHubProducerClient() {
        return new EventHubClientBuilder()
            .connectionString(producerConnectionString, producerEventHubName)
            .buildProducerClient();
    }

    @Bean
    public EventHubConsumerClient eventHubConsumerClient() {
        return new EventHubClientBuilder()
            .connectionString(consumerConnectionString, consumerEventHubName)
            .consumerGroup("$Default") // Use default consumer group
            .buildConsumerClient();
    }
}