package com.minami_m.project.android.wakemeapp.Model;

public class ChatRoomCard {
    private String receiverId;
    private String receiverName;
    private String receiverIcon;
    private String receiverStatus;
    private boolean isReceiverSleeping;

    public ChatRoomCard() {
    }

    public ChatRoomCard(String receiverId,
                        String receiverName,
                        String receiverIcon,
                        String receiverStatus,
                        boolean isReceiverSleeping) {
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverIcon = receiverIcon;
        this.receiverStatus = receiverStatus;
        this.isReceiverSleeping = isReceiverSleeping;
    }

    public ChatRoomCard(User receiver) {
        this.receiverId = receiver.getId();
        this.receiverName = receiver.getName();
        this.receiverIcon = receiver.getIcon();
        this.receiverStatus = receiver.getStatus();
        this.isReceiverSleeping = receiver.isSleeping();
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
