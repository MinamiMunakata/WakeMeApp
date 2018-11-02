package com.minami_m.project.android.wakemeapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormattedDateGenerator {
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
            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d 'at' h:m a", Locale.US);
            return String.format("Last seen %s", dateFormatter.format(new Date(time)));
        }
    }

    public static String generateTimestamp(long createdAt) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("h:m", Locale.US);
        return String.valueOf(dateFormatter.format(createdAt));
    }
}
