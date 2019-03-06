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
import com.minami_m.project.android.wakemeapp.model.Notification;
import com.minami_m.project.android.wakemeapp.model.User;
import com.minami_m.project.android.wakemeapp.model.WakeUpTime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FBRealTimeDBHelper {
    private static final FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();
    public static final DatabaseReference USERS_REF = FIREBASE_DATABASE.getReference("Users");
    public static final DatabaseReference MESSAGES_REF = FIREBASE_DATABASE.getReference("Messages");
    public static final DatabaseReference FRIEND_ID_LIST_REF = FIREBASE_DATABASE.getReference("FriendIDList");
    private static final DatabaseReference NOTIFICATION_REF = FIREBASE_DATABASE.getReference("Notification");
    private static final DatabaseReference RECEIVER_PATH_REF = FIREBASE_DATABASE.getReference("ReceiverPaths");
    public static final DatabaseReference RECEIVER_REF = FIREBASE_DATABASE.getReference("Receivers");
    private static final String TAG = "RealTimeDatabaseHelper";
    private static final DatabaseReference CHAT_ROOMS_REF = FIREBASE_DATABASE.getReference("ChatRooms");

    public static FBRealTimeDBHelper newInstance() {
        return new FBRealTimeDBHelper();
    }

    public static void writeNewUser(User newUser) {
        USERS_REF.child(newUser.getId()).setValue(newUser, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                showResult(databaseError, "Register new user");
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
                        showResult(databaseError, "Update icon");
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
                        showResult(databaseError, "Turn Off WakeUpTime");
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
                            showResult(databaseError, "Update WakeUpTime");
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

    public static void updateLoginTime(final String userId, final long loginTime) {
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
                                        showResult(databaseError, "Update login.");
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

    // /Receiver/{UID}/{chatRoomId}/receiver/
    //                             /notification/{pushId}
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
            childUpdates.put("/Receivers/" + mUser.getId() + "/" + chatRoomId + "/receiver", friend);
            childUpdates.put("/Receivers/" + friend.getId() + "/" + chatRoomId + "/receiver", mUser);
            // (3) Save Receiver Path.
            childUpdates.put("/ReceiverPaths/" + mUser.getId() + "/" + chatRoomId, "/Receivers/" + friend.getId() + "/" + chatRoomId + "/receiver");
            childUpdates.put("/ReceiverPaths/" + friend.getId() + "/" + chatRoomId, "/Receivers/" + mUser.getId() + "/" + chatRoomId + "/receiver");
            FIREBASE_DATABASE.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    showResult(databaseError, "Create a chatRoom");

                }
            });
        }
    }

    private static void showResult(DatabaseError databaseError, String msg) {
        if (databaseError != null) {
            Log.e(TAG, "showResult: ", databaseError.toException());
        } else {
            Log.i(TAG, "showResult: " + msg);
        }
    }

    // onDataChange cannot return anything because this is a read-only method.
    public static void readUserData(String id, final FBRealTimeDBCallback callback) {
        USERS_REF.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.retrieveUserData(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showResult(databaseError, "Read user data");
            }
        });
    }

    public static void sendNewMessage(final String chatRoomId, final Message message, final String receiverId, final String senderName) {
        String key = MESSAGES_REF.child(chatRoomId).push().getKey();
        if (key != null) {
            message.setId(key);
            MESSAGES_REF.child(chatRoomId).child(key)
                    .setValue(message, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError,
                                               @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.e(TAG, "onComplete: ", databaseError.toException());
                            } else {
                                updateLoginTime(message.getSenderId(), message.getCreatedAt());
                                String pushId = NOTIFICATION_REF.child(receiverId).child(chatRoomId).push().getKey();
                                Notification notification = new Notification(pushId, receiverId, senderName, message.getText());
                                if (pushId != null) {
                                    // Node -> /Receiver/{UID}/{chatRoomId}/notification/{pushId}
                                    RECEIVER_REF.child(receiverId).child(chatRoomId).child("notifications").child(pushId).setValue(notification);
                                }
                            }
                        }
                    });
        }

    }

    public static void deleteNotification(String receiverId, String chatRoomId) {
        RECEIVER_REF.child(receiverId).child(chatRoomId).child("notifications").removeValue();
    }

    public static void updateStatusThatMessageHasSeen(String chatRoomId, Message message) {
        MESSAGES_REF.child(chatRoomId).child(message.getId()).child("isSeen")
                .setValue(message.getIsSeen(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError,
                                           @NonNull DatabaseReference databaseReference) {
                        showResult(databaseError, "Update to 'Seen");
                    }
                });
    }


}


