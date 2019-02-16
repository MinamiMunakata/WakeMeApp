package com.minami_m.project.android.wakemeapp.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.minami_m.project.android.wakemeapp.Common.Handler.DateAndTimeFormatHandler;

import java.util.Calendar;
import java.util.Locale;

public class ChatRoomCard implements Parcelable, Comparable<ChatRoomCard> {

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
    private String chatRoomId;
    //    private User receiver;
    private String receiverId;
    private String receiverName;
    private String receiverIcon;
    private String receiverStatus;
    private long oversleepTime;
    private String oversleepTimeStatus;
//    private boolean isReceiverSleeping;

    public ChatRoomCard() {
    }


    public ChatRoomCard(String chatRoomId, User receiver) {
        this.chatRoomId = chatRoomId;
//        this.receiver = receiver;
        this.receiverId = receiver.getId();
        this.receiverName = receiver.getName();
        this.receiverIcon = receiver.getIcon();
        this.receiverStatus = receiver.getStatus();
        this.oversleepTimeStatus = receiver.getStatus();
//        this.isReceiverSleeping = receiver.getIsSleeping();
        this.oversleepTime = -1;
        setupOversleepTime(receiver);
        setupStatus(receiver);
    }

    protected ChatRoomCard(Parcel in) {
        chatRoomId = in.readString();
        receiverId = in.readString();
        receiverName = in.readString();
        receiverIcon = in.readString();
        receiverStatus = in.readString();
        oversleepTime = in.readLong();
        oversleepTimeStatus = in.readString();
//        isReceiverSleeping = in.readByte() != 0;
    }

    private void setupStatus(User user) {
        final int SECOND = 1000;
        final int MINUTE = 60 * SECOND;
        final int HOUR = 60 * MINUTE;
        final int DAY = 24 * HOUR;
        WakeUpTime wakeUpTime = user.getWakeUpTime();
        if (wakeUpTime != null) {
            if (oversleepTime > 0) {
                if (oversleepTime < HOUR) {
                    int min = (int) (oversleepTime / MINUTE);
                    oversleepTimeStatus = String.format(Locale.US, "has overslept %dm", min);
                } else if (oversleepTime < DAY) {
                    int hour = (int) (oversleepTime / HOUR);
                    oversleepTimeStatus = String.format(Locale.US, "has overslept %dh", hour);
                }
            } else {
                if (wakeUpTime.getRepeatIsOn() && mustWakeUpToday(wakeUpTime, Calendar.getInstance())
                        || DateAndTimeFormatHandler.isToday(wakeUpTime.getWakeUpTimeInMillis())) {
                    oversleepTimeStatus = "is already awake!";
                }
            }
        }

    }

    // TODO: check if an user has overslept
    // TODO: Parcel
    private void setupOversleepTime(User user) {
        WakeUpTime wakeUpTime = user.getWakeUpTime();
        Calendar currentTime = Calendar.getInstance();
        if (wakeUpTime != null) {
            if (wakeUpTime.getMustWakeUp()) {
                if (wakeUpTime.getRepeatIsOn()) {
                    if (mustWakeUpToday(wakeUpTime, currentTime)) {
                        Calendar time = Calendar.getInstance();
                        time.set(Calendar.HOUR_OF_DAY, wakeUpTime.getHourOfDay());
                        time.set(Calendar.MINUTE, wakeUpTime.getMinute());
                        long wakeUpTimeInMillis = time.getTimeInMillis();
                        calculateOversleepTime(user, currentTime, wakeUpTimeInMillis);
                    }
                } else {
                    calculateOversleepTime(user, currentTime, wakeUpTime.getWakeUpTimeInMillis());
                }
            } else {
                if (!wakeUpTime.getRepeatIsOn()) {
                    calculateOversleepTime(user, currentTime, wakeUpTime.getWakeUpTimeInMillis());
                }
            }
        }
        System.out.println(oversleepTime);
    }

    private void calculateOversleepTime(User user, Calendar currentTime, long wakeUpTimeInMillis) {
        if (user.getLastLogin() < wakeUpTimeInMillis &&
                currentTime.getTimeInMillis() > wakeUpTimeInMillis) {
            oversleepTime = currentTime.getTimeInMillis() - wakeUpTimeInMillis;
        }
    }

    private boolean mustWakeUpToday(WakeUpTime wakeUpTime, Calendar currentTime) {
        for (Integer day : wakeUpTime.generateExtraDays()) {
            if (day == currentTime.get(Calendar.DAY_OF_WEEK)) {
                return true;
            }
        }
        return false;
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

    public long getOversleepTime() {
        return oversleepTime;
    }

    public void setOversleepTime(long oversleepTime) {
        this.oversleepTime = oversleepTime;
    }

    //    public boolean getIsReceiverSleeping() {
//        return isReceiverSleeping;
//    }
//
//    public void setIsReceiverSleeping(boolean receiverSleeping) {
//        isReceiverSleeping = receiverSleeping;
//    }

//    public User getReceiver() {
//        return receiver;
//    }

//    public void setReceiver(User receiver) {
//        this.receiver = receiver;
//    }


    public String getOversleepTimeStatus() {
        return oversleepTimeStatus;
    }

    public void setOversleepTimeStatus(String oversleepTimeStatus) {
        this.oversleepTimeStatus = oversleepTimeStatus;
    }

    @Override
    public String toString() {
        return String.format(
                "\nReceiver: %s\nOversleepTime: %s\nStatus: %s",
                this.receiverName,
                this.oversleepTime,
                this.oversleepTimeStatus);
    }

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
        dest.writeLong(oversleepTime);
        dest.writeString(oversleepTimeStatus);
//        dest.writeByte((byte) (isReceiverSleeping ? 1 : 0));
    }

    @Override
    public int compareTo(@NonNull ChatRoomCard chatRoomCard) {
        return (int) (chatRoomCard.oversleepTime - this.oversleepTime);
    }
}
