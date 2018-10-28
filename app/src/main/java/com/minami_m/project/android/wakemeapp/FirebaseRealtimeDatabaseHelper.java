package com.minami_m.project.android.wakemeapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.Model.ChatRoom;
import com.minami_m.project.android.wakemeapp.Model.ChatRoomCard;
import com.minami_m.project.android.wakemeapp.Model.User;

import java.util.Arrays;
import java.util.List;

public class FirebaseRealtimeDatabaseHelper {
    private static final String TAG = "RealtimeDatabaseHelper";
    public static final FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();
    public static final DatabaseReference USERS_REF = FIREBASE_DATABASE.getReference("Users");
    public static final DatabaseReference MESSAGES_REF = FIREBASE_DATABASE.getReference("Messages");
    public static final DatabaseReference CHAT_ROOMS_REF = FIREBASE_DATABASE.getReference("ChatRooms");
    public static final DatabaseReference FRIEND_ID_LIST_REF = FIREBASE_DATABASE.getReference("FriendIDList");
    public static final DatabaseReference CHAT_ROOM_ID_LIST_REF = FIREBASE_DATABASE.getReference("ChatRoomIDList");

    public static FirebaseRealtimeDatabaseHelper newInstance() {
        return new FirebaseRealtimeDatabaseHelper();
    }

    public static void writeNewUser(User newUser) {
        USERS_REF.child(newUser.getId()).setValue(newUser, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                showResult(databaseError);
            }
        });
    }

    public static void followFriend(final String currentUserId, final String friendId) {
        FRIEND_ID_LIST_REF.child(currentUserId).child(friendId).setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                showResult(databaseError);
            }
        });
        FRIEND_ID_LIST_REF.child(friendId).child(currentUserId).setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                showResult(databaseError);
            }
        });
    }

    // TODO: fix data structure

    public static void createChatRoom(User mUser, User friend) {
        String chatRoomId = CHAT_ROOMS_REF.push().getKey();
        String[] memberIds = {mUser.getId(), friend.getId()};
        List<String> memberIDList = Arrays.asList(memberIds);
        ChatRoom chatRoom = new ChatRoom(chatRoomId, memberIDList);
        CHAT_ROOMS_REF.child(chatRoomId).setValue(chatRoom);
        //TODO

        CHAT_ROOM_ID_LIST_REF.child(mUser.getId()).child(chatRoomId)
                .setValue(new ChatRoomCard(chatRoomId, friend), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                showResult(databaseError);
            }
        });
        CHAT_ROOM_ID_LIST_REF.child(friend.getId()).child(chatRoomId)
                .setValue(new ChatRoomCard(chatRoomId, mUser), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                showResult(databaseError);
            }
        });
    }

    public static void showResult(DatabaseError databaseError) {
        if (databaseError != null) {
            System.out.println("Data could not be saved " + databaseError.getMessage());
        } else {
            System.out.println("Data saved successfully.");
        }
    }

    public static void readUserData(String id, final RealtimeDatabaseCallback callback) {
        USERS_REF.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.getUser(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showResult(databaseError);
            }
        });
    }


}


