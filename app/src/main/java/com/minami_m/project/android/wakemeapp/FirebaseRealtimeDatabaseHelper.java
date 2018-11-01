package com.minami_m.project.android.wakemeapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.Model.ChatRoom;
import com.minami_m.project.android.wakemeapp.Model.ChatRoomCard;
import com.minami_m.project.android.wakemeapp.Model.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRealtimeDatabaseHelper {
    private static final String TAG = "RealtimeDatabaseHelper";
    public static final FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();
    public static final DatabaseReference USERS_REF = FIREBASE_DATABASE.getReference("Users");
    public static final DatabaseReference MESSAGES_REF = FIREBASE_DATABASE.getReference("Messages");
    public static final DatabaseReference CHAT_ROOMS_REF = FIREBASE_DATABASE.getReference("ChatRooms");
    public static final DatabaseReference FRIEND_ID_LIST_REF = FIREBASE_DATABASE.getReference("FriendIDList");
    public static final DatabaseReference CHAT_ROOM_ID_LIST_REF = FIREBASE_DATABASE.getReference("ChatRoomIDList");
    public static final DatabaseReference RECEIVER_PATH_REF = FIREBASE_DATABASE.getReference("ReceiverPaths");

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

    public static void updateStatusWithLoginTime(String userId, final long loginTime) {
        final String status = StatusGenerator.formattedStatus(loginTime);
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/" + userId + "/lastLogin", loginTime);
        childUpdates.put("/Users/" + userId + "/status", status);
        RECEIVER_PATH_REF.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot path: dataSnapshot.getChildren()) {
                    childUpdates.put(path.getValue() + "/lastLogin", loginTime);
                    childUpdates.put(path.getValue() + "/status", status);
                }
                FIREBASE_DATABASE.getReference().updateChildren(
                        childUpdates,
                        new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError,
                                           @NonNull DatabaseReference databaseReference) {
                        showResult(databaseError);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getMessage());
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });
//        USERS_REF.child(currentUserId).child("lastLogin").setValue(loginTime);
//        USERS_REF.child(currentUserId).child("status").setValue(StatusGenerator.formattedStatus(loginTime));
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

    public static void createChatRoom(User mUser, User friend) {
        Log.i(TAG, "createChatRoom: 123456789 -------- 1");
        // (1) create a ChatRoom Obj and save it.
        String chatRoomId = CHAT_ROOMS_REF.push().getKey();
        String[] memberIds = {mUser.getId(), friend.getId()};
        List<String> memberIDList = Arrays.asList(memberIds);
        ChatRoom chatRoom = new ChatRoom(chatRoomId, memberIDList);
        Log.i(TAG, "createChatRoom: 123456789 " + chatRoom);
        CHAT_ROOMS_REF.child(chatRoomId).setValue(chatRoom);

        // (2) Save ChatRoom ID.
        CHAT_ROOM_ID_LIST_REF.child(mUser.getId()).child(chatRoomId)
                .setValue(friend, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                showResult(databaseError);
            }
        });
        CHAT_ROOM_ID_LIST_REF.child(friend.getId()).child(chatRoomId)
                .setValue(mUser, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                showResult(databaseError);
            }
        });

        // (3) Save Receiver Path.
        RECEIVER_PATH_REF.child(mUser.getId()).push().setValue("/ChatRoomIDList/" + friend.getId() + "/" + chatRoomId);
        RECEIVER_PATH_REF.child(friend.getId()).push().setValue("/ChatRoomIDList/" + mUser.getId() + "/" + chatRoomId);

    }

    public static void showResult(DatabaseError databaseError) {
        if (databaseError != null) {
            System.out.println("Data could not be saved " + databaseError.getMessage());
        } else {
            System.out.println("Data saved successfully.");
        }
    }

    public static void readUserData(String id, final RealtimeDatabaseCallback callback) {
        USERS_REF.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.retrieveUserData(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showResult(databaseError);
            }
        });
    }


}


