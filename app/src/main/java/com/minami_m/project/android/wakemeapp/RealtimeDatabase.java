package com.minami_m.project.android.wakemeapp;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealtimeDatabase {
    public static final FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();
    public static final DatabaseReference USERS_REF = FIREBASE_DATABASE.getReference("Users");
    public static final DatabaseReference MESSAGES_REF = FIREBASE_DATABASE.getReference("Messages");
    public static final DatabaseReference CHAT_ROOMS_REF = FIREBASE_DATABASE.getReference("ChatRooms");

}
