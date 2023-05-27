package com.example.loggingservice;

import com.example.facadeservice.Message;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
public class LoggingController {
    private final ApplicationConfig applicationConfig;
    private final HazelcastInstance hz;
    Logger logger = Logger.getLogger(LoggingController.class.getName());

    @Autowired
    public LoggingController(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.hz = Hazelcast.newHazelcastInstance();
    }

    @GetMapping("/log")
    public String listLog() {
        Map<UUID, String> messages = hz.getMap(applicationConfig.getMapName());

        return messages.values().toString();
    }

    @PostMapping("/log")
    public ResponseEntity<Void> addLog(@RequestBody Message msg) {
        Map<UUID, String> messages = hz.getMap(applicationConfig.getMapName());

        logger.info(msg.toString());
        messages.put(msg.id, msg.txt);
        return ResponseEntity.ok().build();
    }
}
