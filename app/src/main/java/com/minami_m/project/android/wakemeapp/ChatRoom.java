package com.minami_m.project.android.wakemeapp;

import java.util.List;

public class ChatRoom {
    private String id;
    private User sender;
    private User receiver;
    private List<Message> messageList;
    private Dialog dialog;

    private class Dialog {
        private String recieverIcon;
        private String receiverName;
        private String receiverStatus;
        private boolean isReceiverSleeping;
    }
}
