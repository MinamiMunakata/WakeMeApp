package com.minami_m.project.android.wakemeapp.Screen.Alarm;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.view.MenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.minami_m.project.android.wakemeapp.Common.Handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.Common.Listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Screen.Main.MainActivity;
import com.minami_m.project.android.wakemeapp.Screen.MyPage.MypageActivity;
import com.minami_m.project.android.wakemeapp.Screen.SignIn.SignInActivity;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements ActivityChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = findViewById(R.id.toolbar_alarm);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuCompat.setGroupDividerEnabled(menu,true);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FontStyleHandler.setCustomFontOnMenuItems(menu, this);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_menu:
                launchActivity(MainActivity.class);
                return true;
            case R.id.my_page_menu:
                launchActivity(MypageActivity.class);
                return true;
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                launchActivity(SignInActivity.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void launchActivity(Class nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }
}
