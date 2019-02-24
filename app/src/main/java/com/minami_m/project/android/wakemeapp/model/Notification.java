package com.minami_m.project.android.wakemeapp.model;

/**
 * Data structure.
 * /Notification/{receiverId}/{chatRoomId}/{pushId}
 */
public class Notification {
    private String id; // == push ID.
    private String topic; // == receiver ID.
    private String title; // == sender name.
    private String body; // == text.

    public Notification() {
    }

    public Notification(String id, String receiverId, String senderName, String text) {
        this.id = id;
        this.topic = receiverId;
        this.title = senderName;
        if (text.length() > 140) {
            this.body = text.substring(0, 140) + "…";
        } else {
            this.body = text;
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
