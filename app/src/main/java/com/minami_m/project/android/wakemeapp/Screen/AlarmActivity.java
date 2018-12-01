package com.minami_m.project.android.wakemeapp.Screen;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.minami_m.project.android.wakemeapp.R;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
    }

//    private View.OnClickListener showTimePicker() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                int minute = calendar.get(Calendar.MINUTE);
//                TimePickerDialog dialog = new TimePickerDialog(
//                        AlarmScheduleActivity.this,
//                        new TimePickerDialog.OnTimeSetListener() {
//                            @Override
//                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                String AM_PM = "AM";
//                                String zeroMin = "";
//                                if (hourOfDay >= 12) {
//                                    AM_PM = "PM";
//                                    if (hourOfDay >= 13) {
//                                        hourOfDay -= 12;
//                                    }
//                                } else if (hourOfDay == 0) {
//                                    hourOfDay = 12;
//                                }
//                                if (minute < 10) {
//                                    zeroMin = "0";
//                                }
//                                time.setText(hourOfDay + ":" + zeroMin + minute);
//                                time.setAlpha(1);
//                                textViewAMOrPM.setText(AM_PM);
//                                textViewAMOrPM.setAlpha(1);
//
//                            }
//                        },
//                        hour,
//                        minute,
//                        false);
//                dialog.show();
//            }
//        };
//    }
}
