package com.minami_m.project.android.wakemeapp.Common.Helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.Common.RealtimeDatabaseCallback;
import com.minami_m.project.android.wakemeapp.Common.Handler.DateAndTimeFormatHandler;
import com.minami_m.project.android.wakemeapp.Model.Alarm;
import com.minami_m.project.android.wakemeapp.Model.ChatRoom;
import com.minami_m.project.android.wakemeapp.Model.Message;
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

    public static void updateIcon(FirebaseUser currentUser, final String iconUrl) {
        USERS_REF.child(currentUser.getUid()).child("icon").setValue(iconUrl, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                showResult(databaseError);
            }
        });
        final Map<String, Object> childUpdates = new HashMap<>();
        RECEIVER_PATH_REF.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot path: dataSnapshot.getChildren()) {
                    childUpdates.put(path.getValue() + "/icon", iconUrl);
                }
                FIREBASE_DATABASE.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
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
    }

    public static void updateAlarm(FirebaseUser currentUser, final Alarm alarm) {
        if (currentUser != null) {
            USERS_REF.child(currentUser.getUid()).child("alarm").setValue(alarm, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    showResult(databaseError);
                }
            });
            final Map<String, Object> childUpdates = new HashMap<>();
            RECEIVER_PATH_REF.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot path: dataSnapshot.getChildren()) {
                        childUpdates.put(path.getValue() + "/alarm", alarm);
                    }
                    FIREBASE_DATABASE.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
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
        }

    }

    public static void updateStatusWithLoginTime(String userId, final long loginTime) {
        final String status = DateAndTimeFormatHandler.generateStatus(loginTime);
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
//        USERS_REF.child(currentUserId).child("status").setValue(TimeHandler.generateStatus(loginTime));
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
        // (1) create a ChatRoom Obj and save it.
        String chatRoomId = CHAT_ROOMS_REF.push().getKey();
        String[] memberIds = {mUser.getId(), friend.getId()};
        List<String> memberIDList = Arrays.asList(memberIds);
        ChatRoom chatRoom = new ChatRoom(chatRoomId, memberIDList);
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
        RECEIVER_PATH_REF.child(mUser.getId()).child(chatRoomId).setValue("/ChatRoomIDList/" + friend.getId() + "/" + chatRoomId);
        RECEIVER_PATH_REF.child(friend.getId()).child(chatRoomId).setValue("/ChatRoomIDList/" + mUser.getId() + "/" + chatRoomId);

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

    public static void sendNewMessage(String chatRoomId, Message message) {
        String key = MESSAGES_REF.child(chatRoomId).push().getKey();
        message.setId(key);
        MESSAGES_REF.child(chatRoomId).child(key)
                .setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                showResult(databaseError);
            }
        });
    }

    public static void updateStatusThatMessageHasSeen(String chatRoomId, Message message) {
        Log.i(TAG, "updateStatusThatMessageHasSeen: 123456 " + message);
        Log.i(TAG, "updateStatusThatMessageHasSeen: 123456 is updated!");
        MESSAGES_REF.child(chatRoomId).child(message.getId()).child("isSeen")
                .setValue(message.getIsSeen(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                showResult(databaseError);
            }
        });
    }


}


