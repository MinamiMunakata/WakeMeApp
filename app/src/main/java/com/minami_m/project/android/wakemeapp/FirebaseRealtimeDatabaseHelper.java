package com.minami_m.project.android.wakemeapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.SearchFriend.SearchFriendActivity;

import javax.security.auth.callback.Callback;

public class FirebaseRealtimeDatabaseHelper {
    private static final String TAG = "RealtimeDatabaseHelper";
    public static final FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();
    public static final DatabaseReference USERS_REF = FIREBASE_DATABASE.getReference("Users");
    public static final DatabaseReference MESSAGES_REF = FIREBASE_DATABASE.getReference("Messages");
    public static final DatabaseReference CHAT_ROOMS_REF = FIREBASE_DATABASE.getReference("ChatRooms");
    public static final DatabaseReference FRIEND_ID_LIST_REF = FIREBASE_DATABASE.getReference("FriendIDList");

    public static FirebaseRealtimeDatabaseHelper newInstance() {
        return new FirebaseRealtimeDatabaseHelper();
    }

    public static void writeNewUser(User newUser) {
        USERS_REF.child(newUser.getId()).setValue(newUser);
    }

    public static void followFriend(final String currentUserId, final String friendId) {
        FRIEND_ID_LIST_REF.child(currentUserId).child(friendId).setValue(true);
        FRIEND_ID_LIST_REF.child(friendId).child(currentUserId).setValue(true);
    }
}


