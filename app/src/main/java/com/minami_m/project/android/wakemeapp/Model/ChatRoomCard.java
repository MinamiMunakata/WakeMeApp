package com.minami_m.project.android.wakemeapp.Model;

public class ChatRoomCard {
    private String chatRoomId;
    private User receiver;
    private String receiverName;
    private String receiverIcon;
    private String receiverStatus;
    private boolean isReceiverSleeping;

    public ChatRoomCard() {
    }

    public ChatRoomCard(String chatRoomId, User receiver) {
        this.chatRoomId = chatRoomId;
        this.receiver = receiver;
        this.receiverName = receiver.getName();
        this.receiverIcon = receiver.getIcon();
        this.receiverStatus = receiver.getStatus();
        this.isReceiverSleeping = receiver.isSleeping();
    }

    public ChatRoomCard(User receiver) {
        this.receiver = receiver;
        this.receiverName = receiver.getName();
        this.receiverIcon = receiver.getIcon();
        this.receiverStatus = receiver.getStatus();
        this.isReceiverSleeping = receiver.isSleeping();
    }


    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverIcon() {
        return receiverIcon;
    }

    public void setReceiverIcon(String receiverIcon) {
        this.receiverIcon = receiverIcon;
    }

    public String getReceiverStatus() {
        return receiverStatus;
    }

    public void setReceiverStatus(String receiverStatus) {
        this.receiverStatus = receiverStatus;
    }

    public boolean isReceiverSleeping() {
        return isReceiverSleeping;
    }

    public void setReceiverSleeping(boolean receiverSleeping) {
        isReceiverSleeping = receiverSleeping;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }


}
