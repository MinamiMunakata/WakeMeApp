package com.minami_m.project.android.wakemeapp.Screen.Alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.minami_m.project.android.wakemeapp.Model.WakeUpTime;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class NotificationController {
    private Context context;
    private static final int REQUEST_CODE_ONCE = 0;
    private static final long INTERVAL_WEEK = AlarmManager.INTERVAL_DAY * 7;

    public static NotificationController newInstance(Context context) {
        return new NotificationController(context);
    }

    public NotificationController() {
    }

    public NotificationController(Context context) {
        this.context = context;
    }
//
//    if (hourOfDay >= 12) {
//        AM_PM = "PM";
//        if (hourOfDay >= 13) {
//            hourOfDay -= 12;
//        }
//    } else if (hourOfDay == 0) {
//        hourOfDay = 12;
//    } else if (hourOfDay < 10) {
//        zeroHour = "0";
//    }
//        if (minute < 10) {
//        zeroMin = "0";
//    }
    public void setNotification(WakeUpTime wakeUpTime) {
        // Set Time
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR, wakeUpTime.getHourOfDay());
        time.set(Calendar.MINUTE, wakeUpTime.getMinute());
        if (time.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()) {
            time.add(Calendar.DATE, 1); // Tomorrow
        }
        if (wakeUpTime.getMustWakeUp()) {
            if (wakeUpTime.getRepeatIsOn()) {
                ArrayList<Integer> extraDays = wakeUpTime.extraDays();

                for (Integer day: extraDays) {
                    // TODO: set all alarm
                    System.out.println(day);
                }
//
//                // Create Intent
//                Intent intent = new Intent(context, NotificationReceiver.class);
//                PendingIntent sender = PendingIntent.getBroadcast(
//                        context,
//                        REQUEST_CODE_ONCE,
//                        intent,
//                        0);
//                // Let know AlarmManager
//                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), INTERVAL_WEEK, sender);
            } else {
                System.out.println("Going to send");
                // Create Intent
                Intent intent = new Intent(context, NotificationReceiver.class);
                PendingIntent sender = PendingIntent.getBroadcast(
                        context,
                        REQUEST_CODE_ONCE,
                        intent,
                        0);
                // Let know AlarmManager
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), sender);
            }
        }




    }
}
