package com.minami_m.project.android.wakemeapp.Screen.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.minami_m.project.android.wakemeapp.Common.Handler.DateAndTimeFormatHandler;
import com.minami_m.project.android.wakemeapp.Model.WakeUpTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;

public class NotificationController {
    private static final String TAG = "Notifier";
    private static final int REQUEST_CODE_ONCE = 0;
    private static final long INTERVAL_WEEK = AlarmManager.INTERVAL_DAY * 7;
    private Context context;
    private String userId;

    // TODO: userId
    public NotificationController(Context context, String userId) {
        this.context = context;
        this.userId = userId;
    }

    public void setAllNotification(WakeUpTime wakeUpTime) {
        Log.i(TAG, "setAllNotification: " + wakeUpTime);
        if (wakeUpTime.getMustWakeUp()) {
            if (wakeUpTime.getRepeatIsOn()) {
                ArrayList<Integer> extraDays = wakeUpTime.generateExtraDays();
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
//        Calendar time = generateWakeUpTime(wakeUpTime);
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(wakeUpTime.getWakeUpTimeInMillis());
        // Search for coming day of week
        while (time.get(Calendar.DAY_OF_WEEK) != day) {
            time.add(Calendar.DATE, 1);
        }
        Map<String, String> formattedTime = DateAndTimeFormatHandler
                .generateFormattedAlarmTime(
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE));
        Log.i(TAG, "setNotificationAt: " + DateAndTimeFormatHandler.generateDateOfChat(time.getTimeInMillis()) + formattedTime.get("full time"));
        // Create Intent
        PendingIntent sender = generateSender(day, PendingIntent.FLAG_UPDATE_CURRENT);
        // Let know AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), INTERVAL_WEEK, sender);
        }
    }

    private void setNotificationOnce(WakeUpTime wakeUpTime) {
        // Set Time
//        Calendar time = generateWakeUpTime(wakeUpTime);

        // Create Intent
        // TODO: Put Extra
        PendingIntent sender = generateSender(REQUEST_CODE_ONCE, PendingIntent.FLAG_UPDATE_CURRENT);
        // Let know AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, wakeUpTime.getWakeUpTimeInMillis(), sender);
        }
    }

    public void cancelAt(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent sender = generateSender(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
        sender.cancel();
        if (alarmManager != null) {
            alarmManager.cancel(sender);
        }
    }

    public void cancelIfRepeatOff(WakeUpTime wakeUpTime) {
        if (!wakeUpTime.getRepeatIsOn()) {
            for (int day : wakeUpTime.generateExtraDays()) {
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
            if (alarmManager != null) {
                alarmManager.cancel(sender);
            }
        }
    }

    private PendingIntent generateSender(Integer requestCode, int flagUpdateCurrent) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("userId", userId);
        intent.putExtra("requestCode", requestCode);
        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                flagUpdateCurrent);
    }

//    @NonNull
//    private Calendar generateWakeUpTime(WakeUpTime wakeUpTime) {
//        Calendar time = Calendar.getInstance();
//        time.set(Calendar.HOUR_OF_DAY, wakeUpTime.getHourOfDay());
//        time.set(Calendar.MINUTE, wakeUpTime.getMinute());
//        if (time.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
//            time.add(Calendar.DATE, 1); // Tomorrow
//        }
//        return time;
//    }
}
