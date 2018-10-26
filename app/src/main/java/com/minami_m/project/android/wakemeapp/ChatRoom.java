package com.minami_m.project.android.wakemeapp;

import android.util.Log;

import java.util.List;

public class ChatRoom{

    public static final String TAG = "ChatRoom";
    private String id;
    private List<String> memberIDs;
    private Dialog dialog;

    private class Dialog {
        private String receiverName;
        private String recieverIcon;
        private String receiverStatus;
        private boolean isReceiverSleeping;

        public Dialog(String receiverName, String receiverIcon, String receiverStatus,
                      boolean isReceiverSleeping) {
            this.receiverName = receiverName;
            this.recieverIcon = receiverIcon;
            this.receiverStatus = receiverStatus;
            this.isReceiverSleeping = isReceiverSleeping;
        }
    }

    public ChatRoom() {
    }

    public ChatRoom(String id, List<String> memberIDs) {
        this.id = id;
        this.memberIDs = memberIDs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getSender(String currentUserId) {
        for (String id: this.memberIDs) {
            if (id.equals(currentUserId)) {
                FirebaseRealtimeDatabaseHelper.readUserData(id, new RealtimeDatabaseCallback() {
                    @Override
                    public User getUser(User sender) {
                        return sender;
                    }
                });
            }
        }
        Log.i(TAG, "getSender: The current user doesn't belong to this chat room.");
        return new User();
    }

    public User getReceiver(String currentUserId) {
        for (String id: this.memberIDs) {
            if (!id.equals(currentUserId)) {
                FirebaseRealtimeDatabaseHelper.readUserData(id, new RealtimeDatabaseCallback() {
                    @Override
                    public User getUser(User receiver) {
                        return receiver;
                    }
                });
            }
        }
        Log.i(TAG, "getReceiver: The user doesn't belong to this chat room.");
        return new User();
    }

    public Dialog getDialog(String currentUserId) {
        if (this.dialog != null) {
            return this.dialog;
        } else {
            User receiver = getReceiver(currentUserId);
            Dialog newDialog = new Dialog(
                    receiver.getName(),
                    receiver.getAvatar(),
                    receiver.getStatus(),
                    receiver.isSleeping());
            this.dialog = newDialog;
            return this.dialog;
        }
    }
}
