package com.sangsangplus.productservice.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventHubConfig {

    @Value("${azure.eventhub.connection-string}")
    private String connectionString;

    @Value("${azure.eventhub.name:product-events}")
    private String eventHubName;

    @Bean
    public EventHubProducerClient eventHubProducerClient() {
        return new EventHubClientBuilder()
            .connectionString(connectionString, eventHubName)
            .buildProducerClient();
    }
}