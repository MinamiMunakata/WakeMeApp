package com.minami_m.project.android.wakemeapp.model;

import java.util.Date;

public class Message {
    private String id;
    private String text;
    private String senderId;
    private long createdAt;
    private boolean isSeen;

    public Message() {
    }

    public Message(String id, String text, String senderId) {
        this.id = id;
        this.text = text;
        this.senderId = senderId;
        this.createdAt = new Date().getTime();
        this.isSeen = false;
    }

    public Message(String text, String senderId, long createdAt) {
        this.text = text;
        this.senderId = senderId;
        this.createdAt = createdAt;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean seen) {
        this.isSeen = seen;
    }

    @Override
    public String toString() {
        return String.format("\nText: %s\nisSeen: %s\n", this.text, this.isSeen);
    }
}
