package com.minami_m.project.android.wakemeapp.common;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFMService";
    private static final String WAKE_ME_APP_TOPIC = "wake_me_app";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.i(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "FCM Notification Message: " + remoteMessage.getNotification().getBody());
            Log.i(TAG, "FCM Data Message: " + remoteMessage.getData());
        }
    }

    /**
     * The Application's current Instance ID token is no longer valid
     * and thus a new one must be requested.
     */

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        Log.i(TAG, "FCM Token: " + token);

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance().subscribeToTopic(WAKE_ME_APP_TOPIC);


    }
}
