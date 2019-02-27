package com.minami_m.project.android.wakemeapp.common.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFMService";
    //    private static final String WAKE_ME_APP_TOPIC = "messages";
    private String topic;

    public MyFirebaseMessagingService() {
    }

    public MyFirebaseMessagingService(String uId) {
        this.topic = uId;
    }

    private void subscribe() {
        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: DONE");
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Successfully subscribed to \"" + topic + "\"");
                } else {
                    Log.d(TAG, "onComplete: Subscribe failed");
                }
            }
        });
    }

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
        subscribe();


    }

}
