package com.minami_m.project.android.wakemeapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

// TODO: parcel
public class WakeUpTime implements Parcelable {

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

    private boolean mustWakeUp;
    // TODO: -> isRepeatOn
    private boolean repeatIsOn;
    private boolean mon, tue, wed, thu, fri, sat, sun;
    private int hourOfDay, minute;
    private long wakeUpTimeInMillis;

    protected WakeUpTime(Parcel in) {
        mustWakeUp = in.readByte() != 0;
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
        wakeUpTimeInMillis = in.readLong();
    }

    public WakeUpTime() {
    }

    public WakeUpTime(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mustWakeUp ? 1 : 0));
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
        dest.writeLong(wakeUpTimeInMillis);
    }

    public boolean getMustWakeUp() {
        return mustWakeUp;
    }

    public void setMustWakeUp(boolean mustWakeUp) {
        this.mustWakeUp = mustWakeUp;
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

    public long getWakeUpTimeInMillis() {
        return wakeUpTimeInMillis;
    }

    public void setWakeUpTimeInMillis(long wakeUpTimeInMillis) {
        this.wakeUpTimeInMillis = wakeUpTimeInMillis;
    }


    public void turnOnMustWakeUp(boolean mustWakeUp) {
        this.mustWakeUp = mustWakeUp;
        Calendar wakeUpTime = generateTimeToWakeUp();
        this.wakeUpTimeInMillis = wakeUpTime.getTimeInMillis();
    }

    private Calendar generateTimeToWakeUp() {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, getHourOfDay());
        time.set(Calendar.MINUTE, getMinute());
        if (time.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
            time.add(Calendar.DATE, 1); // Tomorrow
        }
        return time;
    }


    @Override
    public String toString() {
        return mustWakeUp ? String.format(Locale.US, "Hour: %d, Minute: %d", hourOfDay, minute) : "OFF";
    }


    private String mustWakeUpOnDayOfWeek() {
        StringBuilder weekStatement = new StringBuilder();
        boolean[] repeatOnDay = generateRepeatOnDay();
        String[] weekdays = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < repeatOnDay.length; i++) {
            if (repeatOnDay[i]) {
                weekStatement.append(weekdays[i]).append(", ");
            }
        }
        if (weekStatement.length() > 0) {
            weekStatement = new StringBuilder(weekStatement.substring(0, weekStatement.length() - 2));
        }
        return weekStatement.toString();
    }

    private String generateTodayOrTomorrow() {
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

    public String generateAlarmOnDayDescription() {
        if (repeatIsOn) {
            return mustWakeUpOnDayOfWeek();
        } else {
            return generateTodayOrTomorrow();
        }
    }

    public void turnOffAlarm() {
        mustWakeUp = false;
        repeatIsOn = false;
        wakeUpTimeInMillis = 0;
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

    public boolean repeatOnDayOfWeekIsAllOff() {
        for (int i = 0; i < generateRepeatOnDay().length; i++) {
            if (generateRepeatOnDay()[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean[] generateRepeatOnDay() {
        return new boolean[]{mon, tue, wed, thu, fri, sat, sun};
    }

    public ArrayList<Integer> generateExtraDays() {
        Integer[] days = new Integer[]{
                Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
                Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
        ArrayList<Integer> extraDays = new ArrayList<>();
        for (int i = 0; i < generateRepeatOnDay().length; i++) {
            if (generateRepeatOnDay()[i]) {
                extraDays.add(days[i]);
            }
        }
        return extraDays;
    }

    public void toggleRepeatOnDayAt(int tag) {
        switch (tag) {
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
                break;
        }
    }

}
