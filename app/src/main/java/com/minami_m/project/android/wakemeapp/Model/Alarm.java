package com.minami_m.project.android.wakemeapp.Model;

public class Alarm {
    private boolean alarmIsOn;
    private boolean notificationIsOn;
    private boolean repeatIsOn;
    private boolean mon, tue, wed, thu, fri, sat, sun;
    private int hourOfDay, minute;

    public Alarm() {
    }

    public boolean getAlarmIsOn() {
        return alarmIsOn;
    }

    public void setAlarmIsOn(boolean alarmIsOn) {
        this.alarmIsOn = alarmIsOn;
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
}
