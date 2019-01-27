package com.minami_m.project.android.wakemeapp.Screen.Alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.minami_m.project.android.wakemeapp.Model.WakeUpTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class NotificationController {
    private Context context;
    private static final int REQUEST_CODE_ONCE = 0;
    private static final long INTERVAL_WEEK = AlarmManager.INTERVAL_DAY * 7;

    public static NotificationController newInstance(Context context) {
        return new NotificationController(context);
    }

    private NotificationController(Context context) {
        this.context = context;
    }

    public void setNotification(WakeUpTime wakeUpTime) {
        if (wakeUpTime.getMustWakeUp()) {
            if (wakeUpTime.getRepeatIsOn()) {
                ArrayList<Integer> extraDays = wakeUpTime.extraDays();
                for (Integer day: extraDays) {
                    // Create time
                    Calendar time = generateWakeUpTime(wakeUpTime);
                    // Search for coming day of week
                    while (time.get(Calendar.DAY_OF_WEEK) != day) {
                        time.add(Calendar.DATE, 1);
                    }
                    // Create Intent
                    PendingIntent sender = generateSender(day, PendingIntent.FLAG_UPDATE_CURRENT);
                    // Let know AlarmManager
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), INTERVAL_WEEK, sender);
                    /* TODO: Canceling */
                    alarmManager.cancel(sender);
                    sender.cancel();
                }

            } else {
                // Set Time
                Calendar time = generateWakeUpTime(wakeUpTime);
                // Create Intent
                PendingIntent sender = generateSender(REQUEST_CODE_ONCE, PendingIntent.FLAG_ONE_SHOT);
                // Let know AlarmManager
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), sender);
                /* TODO: Canceling */
                alarmManager.cancel(sender);
                sender.cancel();

            }
        }
        // TODO: Remove it later
        //checking if alarm is working with pendingIntent
        for (int requestCode = 0; requestCode <= 7; requestCode++) {
            PendingIntent sender = generateSender(requestCode, PendingIntent.FLAG_NO_CREATE);

            boolean isWorking = (sender != null);//just changed the flag
            Log.d("Notification Controller", "boolean:" + isWorking);
            Log.d("Notification Controller", "Request Code: " + requestCode);
            Log.d("Notification Controller", "alarm is " + (isWorking ? "" : "not") + " working...");
        }

    }

    private PendingIntent generateSender(Integer requestCode, int flagUpdateCurrent) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                flagUpdateCurrent);
    }

    @NonNull
    private Calendar generateWakeUpTime(WakeUpTime wakeUpTime) {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, wakeUpTime.getHourOfDay());
        time.set(Calendar.MINUTE, wakeUpTime.getMinute());
        if (time.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
            time.add(Calendar.DATE, 1); // Tomorrow
        }
        return time;
    }
}
