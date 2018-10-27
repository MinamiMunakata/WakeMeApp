package com.minami_m.project.android.wakemeapp.Model;

import android.util.Log;

import com.minami_m.project.android.wakemeapp.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.RealtimeDatabaseCallback;

import java.util.List;

public class ChatRoom{

    public static final String TAG = "ChatRoom";
    private String id;
    private List<String> memberList;
    private ChatRoomCard chatRoomCard;

    public ChatRoom() {
    }

    public ChatRoom(String chatRoomId, List<String> memberList) {
        this.id = chatRoomId;
        this.memberList = memberList;

    }

    public void setChatRoomCard(User reciever) {
        this.chatRoomCard = new ChatRoomCard(reciever);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


//    public User getReceiver(String currentUserId) {
//        for (String id: this.memberIDs) {
//            if (!id.equals(currentUserId)) {
//                FirebaseRealtimeDatabaseHelper.readUserData(id, new RealtimeDatabaseCallback() {
//                    @Override
//                    public User getUser(User receiver) {
//                        return receiver;
//                    }
//                });
//            }
//        }
//        Log.i(TAG, "getReceiver: The user doesn't belong to this chat room.");
//        return new User();
//    }
//
//    public void setReceiverWithDialog(User receiver) {
//        this.receiver = receiver;
//        this.chatRoomCard = new ChatRoomCard(receiver);
//    }
//
//    public ChatRoomCard getDialog(String currentUserId) {
//        if (this.chatRoomCard != null) {
//            return this.chatRoomCard;
//        } else {
//            this.chatRoomCard = new ChatRoomCard(getReceiver(currentUserId));
//            return this.chatRoomCard;
//        }
//    }


}
