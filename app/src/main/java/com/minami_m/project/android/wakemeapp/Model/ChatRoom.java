package com.minami_m.project.android.wakemeapp.Model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.minami_m.project.android.wakemeapp.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.RealtimeDatabaseCallback;

import java.util.List;

public class ChatRoom{

    public static final String TAG = "ChatRoom";
    private String id;
    private List<String> memberIDs;
    private Dialog dialog;
    private User receiver;

    private class Dialog {
        private String receiverName;
        private String recieverIcon;
        private String receiverStatus;
        private boolean isReceiverSleeping;

        public Dialog(User receiver) {
            this.receiverName = receiver.getName();
            this.recieverIcon = receiver.getAvatar();
            this.receiverStatus = receiver.getStatus();
            this.isReceiverSleeping = receiver.isSleeping();
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

    public void setReceiver(User receiver) {
        this.receiver = receiver;
        this.dialog = new Dialog(receiver);
    }

//    public void setDialog(String currentUser) {
//        this.dialog = dialog;
//    }

//    public Dialog getDialog(String currentUserId) {
//        if (this.dialog != null) {
//            return this.dialog;
//        } else {
//            init(currentUserId);
//            return this.dialog;
//        }
//    }
//
//    public void init(String currentUserId) {
//        this.receiver = getReceiver(currentUserId);
//        this.dialog = new Dialog(this.receiver);
//    }
//
//    public void updateDialog(User receiver) {
//        dialog.receiverName = receiver.getName();
//        dialog.recieverIcon = receiver.getAvatar();
//        dialog.receiverStatus = receiver.getStatus();
//        dialog.isReceiverSleeping = receiver.isSleeping();
//    }


}
