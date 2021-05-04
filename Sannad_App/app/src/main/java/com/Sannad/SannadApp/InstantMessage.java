package com.Sannad.SannadApp;

public class InstantMessage {
    private String message;
    private String author;

    public InstantMessage(String message, String author) {
        this.message = message;
        this.author = author;
    }

    // This is a requirement by Firebase.  when we read the official documentation we see that our instance message model class actually needs to have a no argument constructor
    public InstantMessage() {
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}
