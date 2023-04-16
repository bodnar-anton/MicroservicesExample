package com.example.facadeservice;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class FacadeController {

    WebClient loggingWebClient = WebClient.create("http://localhost:8082");
    WebClient messagesWebClient = WebClient.create("http://localhost:8081");

    @GetMapping("/facade_service")
    public Mono<String> getClient() {

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
        var msg = new Message(UUID.randomUUID(), text);

        return loggingWebClient.post()
                .uri("/log")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), Message.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
