package com.example.messagesservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagesController {

    @GetMapping("/message")
    public String getMessage() {
        return "Test message from the message service";
    }
}

