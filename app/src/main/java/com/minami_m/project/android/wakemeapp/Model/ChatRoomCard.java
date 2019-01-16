package com.minami_m.project.android.wakemeapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatRoomCard implements Parcelable{
    private String chatRoomId;
//    private User receiver;
    private String receiverId;

    private String receiverName;
    private String receiverIcon;
    private String receiverStatus;
    private boolean isReceiverSleeping;


    protected ChatRoomCard(Parcel in) {
        chatRoomId = in.readString();
        receiverId = in.readString();
        receiverName = in.readString();
        receiverIcon = in.readString();
        receiverStatus = in.readString();
        isReceiverSleeping = in.readByte() != 0;
    }

    public static final Creator<ChatRoomCard> CREATOR = new Creator<ChatRoomCard>() {
        @Override
        public ChatRoomCard createFromParcel(Parcel in) {
            return new ChatRoomCard(in);
        }

        @Override
        public ChatRoomCard[] newArray(int size) {
            return new ChatRoomCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(chatRoomId);
        dest.writeString(receiverId);
        dest.writeString(receiverName);
        dest.writeString(receiverIcon);
        dest.writeString(receiverStatus);
        dest.writeByte((byte) (isReceiverSleeping ? 1 : 0));
    }


    public ChatRoomCard() {
    }

    public ChatRoomCard(String chatRoomId, User receiver) {
        this.chatRoomId = chatRoomId;
//        this.receiver = receiver;
        this.receiverId = receiver.getId();
        this.receiverName = receiver.getName();
        this.receiverIcon = receiver.getIcon();
        this.receiverStatus = receiver.getStatus();
        this.isReceiverSleeping = receiver.getIsSleeping();
    }

    public ChatRoomCard(User receiver) {
//        this.receiver = receiver;
        this.receiverId = receiver.getId();
        this.receiverName = receiver.getName();
        this.receiverIcon = receiver.getIcon();
        this.receiverStatus = receiver.getStatus();
        this.isReceiverSleeping = receiver.getIsSleeping();
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

    public boolean getIsReceiverSleeping() {
        return isReceiverSleeping;
    }

    public void setIsReceiverSleeping(boolean receiverSleeping) {
        isReceiverSleeping = receiverSleeping;
    }

//    public User getReceiver() {
//        return receiver;
//    }

//    public void setReceiver(User receiver) {
//        this.receiver = receiver;
//    }


    @Override
    public String toString() {
        String discription = String.format(
                "\nReceiver: %s\nId: %s\nStatus: %s",
                this.receiverName,
                this.receiverId,
                this.receiverStatus);
        return discription;
    }
}
