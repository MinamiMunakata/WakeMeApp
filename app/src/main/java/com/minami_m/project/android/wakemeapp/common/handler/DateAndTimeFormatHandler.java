package com.minami_m.project.android.wakemeapp.common.handler;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateAndTimeFormatHandler {
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static String generateStatus(long time) {
        long now = new Date().getTime();
        long diff = now - time;
        if (diff < HOUR) {
            int min = (int) (diff / MINUTE);
            return String.format(Locale.US, "Active %dm ago", min);
        } else if (diff < DAY) {
            int hour = (int) ((now - time) / HOUR);
            return String.format(Locale.US, "Active %dh ago", hour);

        } else {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d 'at' HH:mm", Locale.US);
            return String.format("Last seen %s", dateFormatter.format(new Date(time)));
        }
    }

    public static String generateTimestamp(long createdAt) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.US);
        return String.valueOf(dateFormatter.format(createdAt));
    }

    public static boolean isToday(long createdAt) {
        return createdAt >= getStartOfToday().getTimeInMillis();
    }

    @NonNull
    private static Calendar getStartOfToday() {
        Calendar startOfDay = Calendar.getInstance();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0);
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);
        return startOfDay;
    }

    public static boolean isYesterday(long createdAt) {
        return createdAt >= getStartOfYesterday().getTimeInMillis()
                && createdAt < getStartOfToday().getTimeInMillis();
    }

    @NonNull
    private static Calendar getStartOfYesterday() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);
        return yesterday;
    }

    public static boolean isSameDay(long previousTime, long currentTime) {
        Calendar previous = Calendar.getInstance();
        previous.setTimeInMillis(previousTime);
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(currentTime);
        return previous.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)
                && previous.get(Calendar.YEAR) == current.get(Calendar.YEAR);

    }

    public static String generateDateOfChat(long createdAt) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d',' yyyy", Locale.US);
        return String.valueOf(formatter.format(createdAt));
    }

    public static Map<String, String> generateFormattedAlarmTime(int hourOfDay, int minute) {
        Map<String, String> formattedAlarmTime = new HashMap<>();
        String AM_PM = "AM";
        String zeroMin = "";
        String zeroHour = "";
        if (hourOfDay >= 12) {
            AM_PM = "PM";
            if (hourOfDay >= 13) {
                hourOfDay -= 12;
            }
        } else if (hourOfDay == 0) {
            hourOfDay = 12;
        } else if (hourOfDay < 10) {
            zeroHour = "0";
        }
        if (minute < 10) {
            zeroMin = "0";
        }
        formattedAlarmTime.put(
                "time",
                String.format(Locale.US, "%s%d:%s%d", zeroHour, hourOfDay, zeroMin, minute));
        formattedAlarmTime.put("am_pm", AM_PM);
        formattedAlarmTime.put("full time",
                String.format("%s %s",
                        formattedAlarmTime.get("time"),
                        AM_PM.toLowerCase()));
        return formattedAlarmTime;
    }
}
