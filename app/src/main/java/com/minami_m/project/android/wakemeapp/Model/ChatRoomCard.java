package com.minami_m.project.android.wakemeapp.Model;

public class ChatRoomCard {
    private String chatRoomId;
    private String receiverId;
    private String receiverName;
    private String receiverIcon;
    private String receiverStatus;
    private boolean isReceiverSleeping;

    public ChatRoomCard() {
    }

    public ChatRoomCard(String chatRoomId,
                        String receiverId,
                        String receiverName,
                        String receiverIcon,
                        String receiverStatus,
                        boolean isReceiverSleeping) {
        this.chatRoomId = chatRoomId;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverIcon = receiverIcon;
        this.receiverStatus = receiverStatus;
        this.isReceiverSleeping = isReceiverSleeping;
    }

    public ChatRoomCard(String chatRoomId, User receiver) {
        this.chatRoomId = chatRoomId;
        this.receiverId = receiver.getId();
        this.receiverName = receiver.getName();
        this.receiverIcon = receiver.getIcon();
        this.receiverStatus = receiver.getStatus();
        this.isReceiverSleeping = receiver.isSleeping();
    }

    public ChatRoomCard(User receiver) {
        this.receiverId = receiver.getId();
        this.receiverName = receiver.getName();
        this.receiverIcon = receiver.getIcon();
        this.receiverStatus = receiver.getStatus();
        this.isReceiverSleeping = receiver.isSleeping();
    }

    public void updateReceiver(User receiver) {
        this.receiverStatus = receiver.getStatus();
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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
}
