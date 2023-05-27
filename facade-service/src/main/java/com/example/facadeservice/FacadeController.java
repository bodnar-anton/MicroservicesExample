package com.example.facadeservice;

import com.hazelcast.collection.IQueue;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
public class FacadeController {
    @Autowired
    private DiscoveryClient discoveryClient;
    private final ApplicationConfig applicationConfig;
    HazelcastInstance hz;

    @Autowired
    public FacadeController(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
        this.hz = Hazelcast.newHazelcastInstance();
    }

    @GetMapping("/facade_service")
    public Mono<String> getClient() {
        var loggingWebClient = getRandomLoggingClient();
        var messagesWebClient = getRandomMessagesClient();

        Mono<String> cachedValues = loggingWebClient.get()
                .uri("/log")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> messageTmp = messagesWebClient.get()
                .uri("/message")
                .retrieve()
                .bodyToMono(String.class);

        return cachedValues.zipWith(messageTmp, (cached, message) -> cached + ": " + message).onErrorReturn("Error");
    }

    @PostMapping("/facade_service")
    public Mono<Void> postClient(@RequestBody String text) {
        var loggingWebClient = getRandomLoggingClient();
        var msg = new Message(UUID.randomUUID(), text);
        IQueue<Message> queue = hz.getQueue(applicationConfig.getQueueName());
        queue.add(msg);

        return loggingWebClient.post()
                .uri("/log")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public WebClient getRandomLoggingClient() {
        String loggingServiceID = "logging-service";
        List<ServiceInstance> instances = discoveryClient.getInstances(loggingServiceID);
        int randomIndex = new Random().nextInt(instances.size());
        ServiceInstance randomInstance = instances.get(randomIndex);

        return WebClient.create(randomInstance.getUri().toString());
    }

    public WebClient getRandomMessagesClient() {
        String messagesServiceID = "messages-service";
        List<ServiceInstance> instances = discoveryClient.getInstances(messagesServiceID);
        int randomIndex = new Random().nextInt(instances.size());
        ServiceInstance randomInstance = instances.get(randomIndex);

        return WebClient.create(randomInstance.getUri().toString());
    }
}
