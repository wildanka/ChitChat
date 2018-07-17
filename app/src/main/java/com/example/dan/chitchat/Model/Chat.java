package com.example.dan.chitchat.Model;

/**
 * Created by DAN on 7/16/2018.
 */

public class Chat {
    private String messages;
    private String name;
    private String photoUrl;

    public Chat() {

    }

    public Chat(String messages, String name, String photoUrl) {
        this.messages = messages;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
