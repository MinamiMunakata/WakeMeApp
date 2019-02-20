package com.minami_m.project.android.wakemeapp.model;

import java.util.List;

public class ChatRoom {

    public static final String TAG = "ChatRoom";
    private String id;
    private List<String> memberList;

    public ChatRoom() {
    }

    public ChatRoom(String chatRoomId, List<String> memberList) {
        this.id = chatRoomId;
        this.memberList = memberList;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<String> memberList) {
        this.memberList = memberList;
    }

    @Override
    public String toString() {
        return String.format("ID: %s, member: %s", id, memberList.toString());
    }
}
