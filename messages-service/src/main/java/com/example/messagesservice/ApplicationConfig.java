package com.example.messagesservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {

    @Value("${queue.name}")
    private String queue_name;

    public String getQueueName() {
        return queue_name;
    }
}
