package com.minami_m.project.android.wakemeapp.common.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.model.ChatRoom;
import com.minami_m.project.android.wakemeapp.model.Message;
import com.minami_m.project.android.wakemeapp.model.User;
import com.minami_m.project.android.wakemeapp.model.WakeUpTime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FBRealTimeDBHelper {
    public static final FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();
    public static final DatabaseReference USERS_REF = FIREBASE_DATABASE.getReference("Users");
    public static final DatabaseReference MESSAGES_REF = FIREBASE_DATABASE.getReference("Messages");
    public static final DatabaseReference FRIEND_ID_LIST_REF = FIREBASE_DATABASE.getReference("FriendIDList");
    public static final DatabaseReference CHAT_ROOM_ID_LIST_REF = FIREBASE_DATABASE.getReference("ChatRoomIDList");
    private static final String TAG = "RealTimeDatabaseHelper";
    private static final DatabaseReference CHAT_ROOMS_REF = FIREBASE_DATABASE.getReference("ChatRooms");
    private static final DatabaseReference RECEIVER_PATH_REF = FIREBASE_DATABASE.getReference("ReceiverPaths");

    public static FBRealTimeDBHelper newInstance() {
        return new FBRealTimeDBHelper();
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
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/" + currentUser.getUid() + "/icon", iconUrl);
        RECEIVER_PATH_REF.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot path : dataSnapshot.getChildren()) {
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
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }

    public static void turnOffWakeUpTimeInFB(String userId) {
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/" + userId + "/wakeUpTime/mustWakeUp", false);
        RECEIVER_PATH_REF.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot path : dataSnapshot.getChildren()) {
                    childUpdates.put(path.getValue() + "/wakeUpTime/mustWakeUp", false);
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
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }

    public static void updateWakeUpTIme(FirebaseUser currentUser, final WakeUpTime wakeUpTime) {
        if (currentUser != null) {
            final Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/Users/" + currentUser.getUid() + "/wakeUpTime", wakeUpTime);
            RECEIVER_PATH_REF.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot path : dataSnapshot.getChildren()) {
                        childUpdates.put(path.getValue() + "/wakeUpTime", wakeUpTime);
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
                    Log.e(TAG, "onCancelled: ", databaseError.toException());
                }
            });
        }

    }

    public static void updateLoginTime(String userId, final long loginTime) {
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/" + userId + "/lastLogin", loginTime);
        RECEIVER_PATH_REF.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot path : dataSnapshot.getChildren()) {
                            childUpdates.put(path.getValue() + "/lastLogin", loginTime);
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
                        Log.e(TAG, "onCancelled: ", databaseError.toException());
                    }
                });
    }

    public static void followFriend(final User currentUser, final User friendUser) {
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/FriendIDList/" + currentUser.getId() + "/" + friendUser.getId(), true);
        childUpdates.put("/FriendIDList/" + friendUser.getId() + "/" + currentUser.getId(), true);
        FIREBASE_DATABASE.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "onComplete: ", databaseError.toException());
                } else {
                    createChatRoom(currentUser, friendUser);
                }
            }
        });
    }

    private static void createChatRoom(User mUser, User friend) {
        final Map<String, Object> childUpdates = new HashMap<>();
        // (1) create a ChatRoom Obj and save it.
        String chatRoomId = CHAT_ROOMS_REF.push().getKey();
        if (chatRoomId != null) {
            String[] memberIds = {mUser.getId(), friend.getId()};
            List<String> memberIDList = Arrays.asList(memberIds);
            ChatRoom chatRoom = new ChatRoom(chatRoomId, memberIDList);
            childUpdates.put("/ChatRooms/" + chatRoomId, chatRoom);

            // (2) Save ChatRoom ID.
            childUpdates.put("/ChatRoomIDList/" + mUser.getId() + "/" + chatRoomId, friend);
            childUpdates.put("/ChatRoomIDList/" + friend.getId() + "/" + chatRoomId, mUser);
            // (3) Save Receiver Path.
            childUpdates.put("/ReceiverPaths/" + mUser.getId() + "/" + chatRoomId, "/ChatRoomIDList/" + friend.getId() + "/" + chatRoomId);
            childUpdates.put("/ReceiverPaths/" + friend.getId() + "/" + chatRoomId, "/ChatRoomIDList/" + mUser.getId() + "/" + chatRoomId);
            FIREBASE_DATABASE.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    showResult(databaseError);

                }
            });
        }


    }

    private static void showResult(DatabaseError databaseError) {
        if (databaseError != null) {
            Log.e(TAG, "showResult: ", databaseError.toException());
        } else {
            Log.i(TAG, "showResult: Data saved successfully.");
        }
    }

    public static void readUserData(String id, final FBRealTimeDBCallback callback) {
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
        if (key != null) {
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

    }

    public static void updateStatusThatMessageHasSeen(String chatRoomId, Message message) {
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


