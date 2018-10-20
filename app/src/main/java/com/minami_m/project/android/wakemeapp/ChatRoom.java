package com.minami_m.project.android.wakemeapp;

import android.net.Uri;

public class ChatRoom {
    private String id;
    private User sender;
    private User receiver;
    private Dialog dialog;

    private class Dialog {
        private String receiverName;
        private Uri recieverIcon;
        private String receiverStatus;
        private boolean isReceiverSleeping;

        public Dialog(String receiverName, Uri recieverIcon, String receiverStatus,
                      boolean isReceiverSleeping) {
            this.receiverName = receiverName;
            this.recieverIcon = recieverIcon;
            this.receiverStatus = receiverStatus;
            this.isReceiverSleeping = isReceiverSleeping;
        }
    }

    public ChatRoom() {
    }

    public ChatRoom(String id, User sender, User receiver) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.dialog = new Dialog(receiver.getName(), receiver.getAvatar(),
                receiver.getStatus(), receiver.isSleeping());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(ChatRoom chatRoom) {
        this.dialog = new Dialog(chatRoom.receiver.getName(), chatRoom.receiver.getAvatar(),
                chatRoom.receiver.getStatus(), chatRoom.receiver.isSleeping());
    }
}
