package com.minami_m.project.android.wakemeapp.Model;

import java.util.Date;

public class User {
    private String id;
    private String name;
    private String nickName;
    private String icon;
    private String email;
    private String status;
    private boolean isSleeping;
    private long lastLogin;
    private long alarmTime;

    public User() {
    }

    public User(String id, String name, String email, String icon) {
        this.id = id;
        this.name = name;
        this.nickName = name;
        this.icon = icon;
        this.email = email;
        this.lastLogin = new Date().getTime();
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.nickName = name;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public boolean isSleeping() {
        return isSleeping;
    }

    public void setSleeping(boolean sleeping) {
        isSleeping = sleeping;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String info = String.format(
                "Name: %s\nEmail: %s\nIcon: %s\nStatus: %s\n",
                this.name, this.email, this.icon, this.status);
        return info;
    }


}
