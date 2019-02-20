package com.minami_m.project.android.wakemeapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class User implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String id;
    private String name;
    private String icon;
    private String email;
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

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        icon = in.readString();
        email = in.readString();
        lastLogin = in.readLong();
        wakeUpTime = in.readParcelable(WakeUpTime.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(icon);
        dest.writeString(email);
        dest.writeLong(lastLogin);
        dest.writeParcelable(wakeUpTime, flags);
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

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
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
                "ID: %s\nName: %s\nEmail: %s\nIcon: %s\nWakeUpTime: %s",
                this.id, this.name, this.email, this.icon, this.wakeUpTime.toString());
    }
}
