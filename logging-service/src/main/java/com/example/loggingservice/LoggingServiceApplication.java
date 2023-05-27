package com.example.loggingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class LoggingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoggingServiceApplication.class, args);
    }

}
