package com.example.facadeservice;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    public UUID id;
    public String txt;

    public Message(UUID id, String txt) {
        if (id == null || txt == null) {
            throw new IllegalArgumentException("id and txt cannot be null");
        }

        this.id = id;
        this.txt = txt;
    }

    @Override
    public String toString() {
        return String.format("id: %s; text: %s", this.id.toString(), this.txt);
    }
}
