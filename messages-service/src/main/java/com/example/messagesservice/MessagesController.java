package com.example.messagesservice;

import com.example.facadeservice.Message;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
@RestController
public class MessagesController {
    private final HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    private final IQueue<Message> queue = hz.getQueue("messages");
    Logger logger = Logger.getLogger(MessagesController.class.getName());
    private final Map<UUID, String> messages = new ConcurrentHashMap<>();

    @GetMapping("/message")
    public String getMessage() {
        return messages.values().toString();
    }

    @Scheduled(fixedDelay = 1000)
    public void consumeMessage() {
        Message msg = queue.poll();
        if (msg != null) {
            logger.info("Consumed: " + msg.toString());
            messages.put(msg.id, msg.txt);
        }
    }
}

