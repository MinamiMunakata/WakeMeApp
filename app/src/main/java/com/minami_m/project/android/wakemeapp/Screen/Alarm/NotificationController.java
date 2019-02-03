package com.minami_m.project.android.wakemeapp.Screen.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.minami_m.project.android.wakemeapp.Model.WakeUpTime;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationController {
    private static final int REQUEST_CODE_ONCE = 0;
    private static final long INTERVAL_WEEK = AlarmManager.INTERVAL_DAY * 7;
    private Context context;
    private String userId;

    // TODO: userId
    private NotificationController(Context context) {
        this.context = context;
    }

    // TODO: userId
    public static NotificationController newInstance(Context context) {
        return new NotificationController(context);
    }

    public void setAllNotification(WakeUpTime wakeUpTime) {
        if (wakeUpTime.getMustWakeUp()) {
            if (wakeUpTime.getRepeatIsOn()) {
                ArrayList<Integer> extraDays = wakeUpTime.extraDays();
                for (Integer day : extraDays) {
                    setNotificationAt(wakeUpTime, day);

                }
                cancelAt(0);

            } else {
                setNotificationOnce(wakeUpTime);

            }
        }
        // TODO: Remove it later
        checkIfNotificationIsWorking();

    }

    public void checkIfNotificationIsWorking() {
        // Checking if alarm is working with pendingIntent
        for (int requestCode = 0; requestCode <= 7; requestCode++) {
            PendingIntent sender = generateSender(requestCode, PendingIntent.FLAG_NO_CREATE);
            boolean isWorking = (sender != null);//just changed the flag
            Log.d("Notification Controller", "Request Code: " + requestCode);
            Log.d("Notification Controller", "alarm is " + (isWorking ? "" : "not") + " working...");
        }
    }

    public void setNotificationAt(WakeUpTime wakeUpTime, Integer day) {
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
    }

    private void setNotificationOnce(WakeUpTime wakeUpTime) {
        // Set Time
        Calendar time = generateWakeUpTime(wakeUpTime);
        // Create Intent
        // TODO: Put Extra
        PendingIntent sender = generateSender(REQUEST_CODE_ONCE, PendingIntent.FLAG_UPDATE_CURRENT);
        // Let know AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), sender);
    }

    public void cancelAt(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent sender = generateSender(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
        sender.cancel();
        alarmManager.cancel(sender);
    }

    public void cancelIfRepeatOff(WakeUpTime wakeUpTime) {
        if (!wakeUpTime.getRepeatIsOn()) {
            for (int day : wakeUpTime.extraDays()) {
                cancelAt(day);
            }
            setNotificationOnce(wakeUpTime);
        }
    }


    public void cancelAllNotification() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //checking if alarm is working with pendingIntent
        for (int requestCode = 0; requestCode <= 7; requestCode++) {
            PendingIntent sender = generateSender(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
            sender.cancel();
            alarmManager.cancel(sender);
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
