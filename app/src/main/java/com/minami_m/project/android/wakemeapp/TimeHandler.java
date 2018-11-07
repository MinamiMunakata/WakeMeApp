package com.minami_m.project.android.wakemeapp;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeHandler {
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static String generateStatus(long time) {
        long now = new Date().getTime();
        long diff = now - time;
        if (diff < HOUR) {
            int min = (int) (diff / MINUTE);
            return String.format(Locale.US,"Active %dm ago", min);
        } else if (diff < DAY) {
            int hour= (int)((now - time) / HOUR);
            return String.format(Locale.US,"Active %dh ago", hour);

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
}
