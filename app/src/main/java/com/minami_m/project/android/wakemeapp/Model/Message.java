package com.minami_m.project.android.wakemeapp.Model;

import java.util.Date;

public class Message {
    private String id;
    private String text;
    private String author;
    private long createdAt;
    private boolean isSeen;

    public Message() {
    }

    public Message(String id, String text, String author) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.createdAt = new Date().getTime();
        this.isSeen = false;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
