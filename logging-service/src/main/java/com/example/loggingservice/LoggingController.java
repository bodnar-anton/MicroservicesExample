package com.example.loggingservice;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
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

    private HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    private Map<UUID, String> messages = hz.getMap("logging_map");
    Logger logger = Logger.getLogger(LoggingController.class.getName());

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
