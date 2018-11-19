package com.minami_m.project.android.wakemeapp.Screen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.minami_m.project.android.wakemeapp.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmScheduleActivity extends AppCompatActivity {
    TextView sun;
    TextView mon;
    TextView tue;
    TextView wed;
    TextView thu;
    TextView fri;
    TextView sat;
    TextView sun_alarm_sign;
    TextView mon_alarm_sign;
    TextView tue_alarm_sign;
    TextView wed_alarm_sign;
    TextView thu_alarm_sign;
    TextView fri_alarm_sign;
    TextView sat_alarm_sign;
    long[] week;
    TextView[] weekdays;
    TextView[] alarmSigns;
    TextView date;
    Map<Integer, Long> weeklyMap;
    long today;
    int indexOfSelectedDay;

    enum Weekday {
        Today,
        Sun,
        Mon,
        Tue,
        Wed,
        Thu,
        Fri,
        Sat
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_schedule);
        setup();
        setupThisWeek();
        setupWeeklyMap();
        for (int i = 1; i < weekdays.length; i++) {
            weekdays[i].setOnClickListener(selectTheDayOfTheWeek());
        }
    }

    private void setup() {
        sun = findViewById(R.id.sun);
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.fri);
        sat = findViewById(R.id.sat);
        weekdays = new TextView[]{null, sun, mon, tue, wed, thu, fri, sat};
        sun_alarm_sign = findViewById(R.id.sun_alarm_sign);
        mon_alarm_sign = findViewById(R.id.mon_alarm_sign);
        tue_alarm_sign = findViewById(R.id.tue_alarm_sign);
        wed_alarm_sign = findViewById(R.id.wed_alarm_sign);
        thu_alarm_sign = findViewById(R.id.thu_alarm_sign);
        fri_alarm_sign = findViewById(R.id.fri_alarm_sign);
        sat_alarm_sign = findViewById(R.id.sat_alarm_sign);
        alarmSigns = new TextView[]{null, sun_alarm_sign, mon_alarm_sign, tue_alarm_sign,
                wed_alarm_sign, thu_alarm_sign, fri_alarm_sign, sat_alarm_sign};
        for (int i = 1; i < alarmSigns.length; i++) {
            alarmSigns[i].setVisibility(View.INVISIBLE);
        }
        date = findViewById(R.id.alarm_date);
    }

    private void setupThisWeek() {
        Calendar cal = Calendar.getInstance();
        week = new long[8];
        for (int i = 0; i < 7; i++) {
            if (i == 0) {
                week[0] = 0;
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                indexOfSelectedDay = dayOfWeek;
                today = cal.getTimeInMillis();
                week[dayOfWeek] = today;
                weekdays[dayOfWeek].setBackground(getDrawable(R.drawable.white_circle));
                date.setText("Today");
            } else {
                cal.add(Calendar.DATE, 1);
                week[cal.get(Calendar.DAY_OF_WEEK)] = cal.getTimeInMillis();
            }
        }
    }

    private void setupWeeklyMap() {
        weeklyMap = new HashMap<>();
        for (int i = 1; i < weekdays.length; i++) {
            weeklyMap.put(weekdays[i].getId(), week[i]);
        }
    }

    private View.OnClickListener selectTheDayOfTheWeek() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long day = weeklyMap.get(v.getId());
                indexOfSelectedDay = Arrays.asList(weekdays).indexOf(v);
                SimpleDateFormat format = new SimpleDateFormat("MMM d");
                if (day == today) {
                    date.setText("Today");
                } else {
                    date.setText(format.format(day));
                }
                for (int i = 1; i < weekdays.length; i++) {
                    weekdays[i].setBackground(null);
                }
                v.setBackground(getDrawable(R.drawable.white_circle));
            }
        };
    }
}
