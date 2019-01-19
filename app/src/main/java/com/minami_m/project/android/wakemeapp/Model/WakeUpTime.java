package com.minami_m.project.android.wakemeapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.minami_m.project.android.wakemeapp.Common.Handler.DateAndTimeFormatHandler;

import java.util.Calendar;
import java.util.Map;

public class WakeUpTime implements Parcelable{
    private boolean mustWakeUp;
    private boolean notificationIsOn;
    private boolean repeatIsOn;
    private boolean mon, tue, wed, thu, fri, sat, sun;
    private int hourOfDay, minute;

    protected WakeUpTime(Parcel in) {
        mustWakeUp = in.readByte() != 0;
        notificationIsOn = in.readByte() != 0;
        repeatIsOn = in.readByte() != 0;
        mon = in.readByte() != 0;
        tue = in.readByte() != 0;
        wed = in.readByte() != 0;
        thu = in.readByte() != 0;
        fri = in.readByte() != 0;
        sat = in.readByte() != 0;
        sun = in.readByte() != 0;
        hourOfDay = in.readInt();
        minute = in.readInt();
    }

    public static final Creator<WakeUpTime> CREATOR = new Creator<WakeUpTime>() {
        @Override
        public WakeUpTime createFromParcel(Parcel in) {
            return new WakeUpTime(in);
        }

        @Override
        public WakeUpTime[] newArray(int size) {
            return new WakeUpTime[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mustWakeUp ? 1 : 0));
        dest.writeByte((byte) (notificationIsOn ? 1 : 0));
        dest.writeByte((byte) (repeatIsOn ? 1 : 0));
        dest.writeByte((byte) (mon ? 1 : 0));
        dest.writeByte((byte) (tue ? 1 : 0));
        dest.writeByte((byte) (wed ? 1 : 0));
        dest.writeByte((byte) (thu ? 1 : 0));
        dest.writeByte((byte) (fri ? 1 : 0));
        dest.writeByte((byte) (sat ? 1 : 0));
        dest.writeByte((byte) (sun ? 1 : 0));
        dest.writeInt(hourOfDay);
        dest.writeInt(minute);
    }



    public WakeUpTime() {
    }

    public WakeUpTime(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean getMustWakeUp() {
        return mustWakeUp;
    }

    public void setMustWakeUp(boolean mustWakeUp) {
        this.mustWakeUp = mustWakeUp;
    }

    public boolean getNotificationIsOn() {
        return notificationIsOn;
    }

    public void setNotificationIsOn(boolean notificationIsOn) {
        this.notificationIsOn = notificationIsOn;
    }

    public boolean getRepeatIsOn() {
        return repeatIsOn;
    }

    public void setRepeatIsOn(boolean repeatIsOn) {
        this.repeatIsOn = repeatIsOn;
    }

    public boolean getMon() {
        return mon;
    }

    public void setMon(boolean mon) {
        this.mon = mon;
    }

    public boolean getTue() {
        return tue;
    }

    public void setTue(boolean tue) {
        this.tue = tue;
    }

    public boolean getWed() {
        return wed;
    }

    public void setWed(boolean wed) {
        this.wed = wed;
    }

    public boolean getThu() {
        return thu;
    }

    public void setThu(boolean thu) {
        this.thu = thu;
    }

    public boolean getFri() {
        return fri;
    }

    public void setFri(boolean fri) {
        this.fri = fri;
    }

    public boolean getSat() {
        return sat;
    }

    public void setSat(boolean sat) {
        this.sat = sat;
    }

    public boolean getSun() {
        return sun;
    }

    public void setSun(boolean sun) {
        this.sun = sun;
    }



    @Override
    public String toString() {
        Map<String, String> formattedTime = DateAndTimeFormatHandler.generateFormattedAlarmTime(
                this.hourOfDay,
                this.minute);
        return formattedTime.get("full time");
    }

    public String mustWakeUpOnDayOfWeek() {
        String weekStatement = "";
        boolean[] repeatOnDay = myGetRepeatOnDay();
        String[] weekdays = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < repeatOnDay.length; i++) {
            if (repeatOnDay[i]) {
                weekStatement += weekdays[i] + ", ";
            }
        }
        if (weekStatement.length() > 0) {
            weekStatement = weekStatement.substring(0, weekStatement.length() - 2);
        }
        return weekStatement;
    }

    public String getTodayOrTomorrow() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Calendar alarmTimeCalendar = Calendar.getInstance();
        alarmTimeCalendar.set(Calendar.HOUR_OF_DAY, getHourOfDay());
        alarmTimeCalendar.set(Calendar.MINUTE, getMinute());
        if (currentTime < alarmTimeCalendar.getTimeInMillis()) {
            return "Today";
        } else {
            return "Tomorrow";
        }
    }

    public String getAlarmOnDayDescription() {
        if (repeatIsOn) {
            return mustWakeUpOnDayOfWeek();
        } else {
            return getTodayOrTomorrow();
        }
    }

    public  void turnOffAlarm() {
        mustWakeUp = false;
        notificationIsOn = false;
        repeatIsOn = false;
    }

    public void turnOnRepeatingOnEveryday() {
        mon = true;
        tue = true;
        wed = true;
        thu = true;
        fri = true;
        sat = true;
        sun = true;
    }

    public boolean checkRepeatOnDayOfWeekIsAllOff() {
        for (int i = 0; i < myGetRepeatOnDay().length; i++) {
            if (myGetRepeatOnDay()[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean[] myGetRepeatOnDay() {
        return new boolean[]{mon, tue, wed, thu, fri, sat, sun};
    }

    public void toggleRepeatOnDayAt(int index) {
        switch (index) {
            case 0:
                mon = !mon;
                break;
            case 1:
                tue = !tue;
                break;
            case 2:
                wed = !wed;
                break;
            case 3:
                thu = !thu;
                break;
            case 4:
                fri = !fri;
                break;
            case 5:
                sat = !sat;
                break;
            case 6:
                sun = !sun;
                break;
            default:
                return;
        }
    }



}
