package com.example.messagesservice;

import com.example.facadeservice.Message;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ApplicationConfig applicationConfig;
    private final HazelcastInstance hz;
    private final Map<UUID, String> messages;
    Logger logger = Logger.getLogger(MessagesController.class.getName());

    @Autowired
    public MessagesController(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.hz = Hazelcast.newHazelcastInstance();
        this.messages = new ConcurrentHashMap<>();
    }

    @GetMapping("/message")
    public String getMessage() {
        return messages.values().toString();
    }

    @PostConstruct
    public void consumeMessage() {
        Thread thread = new Thread(() -> {
            while (true) {
                IQueue<Message> queue = hz.getQueue(applicationConfig.getQueueName());

                Message msg = queue.poll();
                if (msg != null) {
                    logger.info("Consumed: " + msg.toString());
                    messages.put(msg.id, msg.txt);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}

