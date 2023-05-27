package com.example.loggingservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {

    @Value("${map.name}")
    private String map_name;

    public String getMapName() {
        return map_name;
    }
}
