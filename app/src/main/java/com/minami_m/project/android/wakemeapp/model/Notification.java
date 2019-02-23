package com.minami_m.project.android.wakemeapp.model;

/**
 * Data structure.
 * /Notification/{receiverId}/{chatRoomId}/
 */
public class Notification {
    String id; // chatRoomId
    String topic; // == receiver ID.
    String title; // == sender name.
    String body; // == text.


}
