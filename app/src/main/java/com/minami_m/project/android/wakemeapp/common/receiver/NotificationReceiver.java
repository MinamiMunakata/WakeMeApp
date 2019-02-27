package com.minami_m.project.android.wakemeapp.common.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.minami_m.project.android.wakemeapp.common.helper.FBRealTimeDBHelper;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.screen.main.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 100;
    private static final String CHANNEL_ID = "alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Received!", Toast.LENGTH_SHORT).show();

        // Create an explicit intent for an Activity in your app
        Intent sendIntent = new Intent(context, MainActivity.class);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, sendIntent, 0);

        NotificationCompat.Builder builder = getNotificationBuilder(context, pendingIntent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        createNotificationChannel(context);
        managerCompat.notify(NOTIFICATION_ID, builder.build());

        // Change 'mustWakeUp' -> false if the notification is only for ONCE.
        String userId = intent.getStringExtra("userId");
        int requestCode = intent.getIntExtra("requestCode", -1);
        if (userId != null && requestCode == 0) {
            FBRealTimeDBHelper.turnOffWakeUpTimeInFB(userId);
        }

    }

    private NotificationCompat.Builder getNotificationBuilder(Context context, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ico_alart)
                .setColor(context.getColor(R.color.colorMyAccent))
                .setContentText(context.getString(R.string.notificationText))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.notificationText)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.notificationText);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
