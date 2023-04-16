package com.example.loggingservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@RestController
public class LoggingController {
    Logger logger = Logger.getLogger(LoggingController.class.getName());
    private Map<UUID, String> messages = new ConcurrentHashMap<>();

    @GetMapping("/log")
    public String listLog() {
        return messages.values().toString();
    }

    @PostMapping("/log")
    public ResponseEntity<Void> addLog(@RequestBody Message msg) {
        logger.info(msg.toString());
        messages.put(msg.id, msg.txt);
        return ResponseEntity.ok().build();
    }

}
