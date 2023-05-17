package com.example.facadeservice;

import com.hazelcast.collection.IQueue;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
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

    List<WebClient> loggingWebClients = List.of(
            WebClient.create("http://localhost:8083"),
            WebClient.create("http://localhost:8084"),
            WebClient.create("http://localhost:8085")
    );

    List<WebClient> messagesWebClients = List.of(
            WebClient.create("http://localhost:8081"),
            WebClient.create("http://localhost:8082")
    );

    HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    IQueue<Message> queue = hz.getQueue("messages");

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
        queue.add(msg);

        return loggingWebClient.post()
                .uri("/log")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public WebClient getRandomLoggingClient() {
        Random rand = new Random();
        int index = rand.nextInt(loggingWebClients.size());

        return loggingWebClients.get(index);
    }

    public WebClient getRandomMessagesClient() {
        Random rand = new Random();
        int index = rand.nextInt(messagesWebClients.size());

        return messagesWebClients.get(index);
    }

}
