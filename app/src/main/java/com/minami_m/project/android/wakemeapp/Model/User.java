package com.minami_m.project.android.wakemeapp.Model;

import java.util.Date;

public class User {
    private String id;
    private String name;
    private String icon;
    private String email;
    private String status;
    //    private boolean isSleeping;
    private long lastLogin;
    private WakeUpTime wakeUpTime;

    public User() {
    }

    public User(String id, String name, String email, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.email = email;
        this.lastLogin = new Date().getTime();
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.lastLogin = new Date().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public boolean getIsSleeping() {
//        return isSleeping;
//    }

//    public void setIsSleeping(boolean sleeping) {
//        isSleeping = sleeping;
//    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public WakeUpTime getWakeUpTime() {
        return wakeUpTime;
    }

    public void setWakeUpTime(WakeUpTime wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %s\nName: %s\nEmail: %s\nIcon: %s\nStatus: %s\nWakeUpTime: %s",
                this.id, this.name, this.email, this.icon, this.status, this.wakeUpTime.toString());
    }

}
