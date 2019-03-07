package com.minami_m.project.android.wakemeapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.minami_m.project.android.wakemeapp.common.handler.DateAndTimeFormatHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private User receiver;
    private boolean unread;
    private long oversleepTime;
    private String oversleepTimeStatus;


    public ChatRoomCard() {
    }


    public ChatRoomCard(String chatRoomId, User receiver) {
        this.chatRoomId = chatRoomId;
        this.receiver = receiver;
        this.oversleepTime = -1;
        setupOversleepTime(receiver);
        setupStatus(receiver);
    }

    public ChatRoomCard(String chatRoomId, User receiver, boolean unread) {
        this.chatRoomId = chatRoomId;
        this.receiver = receiver;
        this.oversleepTime = -1;
        this.unread = unread;
        setupOversleepTime(receiver);
        setupStatus(receiver);
    }

    private ChatRoomCard(Parcel in) {
        chatRoomId = in.readString();
        receiver = in.readParcelable(User.class.getClassLoader());
        unread = in.readByte() != 0;
        oversleepTime = in.readLong();
        oversleepTimeStatus = in.readString();
    }

    private void setupStatus(User user) {
        oversleepTimeStatus = DateAndTimeFormatHandler.generateStatus(receiver.getLastLogin());
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
                if (wakeUpTime.getIsRepeatModeOn() && mustWakeUpToday(wakeUpTime, Calendar.getInstance())
                        || DateAndTimeFormatHandler.isToday(wakeUpTime.getWakeUpTimeInMillis())) {
                    Calendar timeToWakeUp = Calendar.getInstance();
                    timeToWakeUp.set(Calendar.HOUR_OF_DAY, wakeUpTime.getHourOfDay());
                    timeToWakeUp.set(Calendar.MINUTE, wakeUpTime.getMinute());
                    if (timeToWakeUp.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()
                            || wakeUpTime.getWakeUpTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.US);
                        oversleepTimeStatus = "wants to wake up at " + dateFormatter.format(new Date(timeToWakeUp.getTimeInMillis()));
                    }
                }
            }
        }

    }

    private void setupOversleepTime(User user) {
        WakeUpTime wakeUpTime = user.getWakeUpTime();
        Calendar currentTime = Calendar.getInstance();
        if (wakeUpTime != null) {
            if (wakeUpTime.getIsAlarmOn()) {
                if (wakeUpTime.getIsRepeatModeOn()) {
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
                if (!wakeUpTime.getIsRepeatModeOn()) {
                    calculateOversleepTime(user, currentTime, wakeUpTime.getWakeUpTimeInMillis());
                }
            }
        }
        if (!DateAndTimeFormatHandler.isLessThan24h(oversleepTime)) {
            oversleepTime = -1;
        }
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

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public boolean getUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public long getOversleepTime() {
        return oversleepTime;
    }

    public void setOversleepTime(long oversleepTime) {
        this.oversleepTime = oversleepTime;
    }

    public String getOversleepTimeStatus() {
        return oversleepTimeStatus;
    }

    public void setOversleepTimeStatus(String oversleepTimeStatus) {
        this.oversleepTimeStatus = oversleepTimeStatus;
    }

    @Override
    public String toString() {
        return String.format(
                "\nReceiver: %s\nOversleepTime: %s\nStatus: %s\n%s",
                this.receiver.getName(),
                this.oversleepTime,
                this.oversleepTimeStatus,
                this.unread ? "You have a unread text." : "No new message.");
    }


    @Override
    public int compareTo(@NonNull ChatRoomCard chatRoomCard) {
        if ((int) (chatRoomCard.oversleepTime - this.oversleepTime) == 0) {
            return (chatRoomCard.unread ? 1 : 0) - (this.unread ? 1 : 0);
        }
        return (int) (chatRoomCard.oversleepTime - this.oversleepTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatRoomId);
        dest.writeParcelable(receiver, flags);
        dest.writeByte((byte) (unread ? 1 : 0));
        dest.writeLong(oversleepTime);
        dest.writeString(oversleepTimeStatus);
    }
}
