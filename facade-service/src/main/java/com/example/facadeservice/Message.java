package com.example.facadeservice;

import java.util.UUID;

public class Message {
    public UUID id;
    public String txt;

    public Message(UUID id, String txt) {
        this.id = id;
        this.txt = txt;
    }

    @Override
    public String toString() {
        return String.format("id: %s; text: %s", this.id.toString(), this.txt);
    }
}
