package com.minami_m.project.android.wakemeapp.Screen.Alarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.view.MenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minami_m.project.android.wakemeapp.Common.Handler.DateAndTimeFormatHandler;
import com.minami_m.project.android.wakemeapp.Common.Handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.Common.Helper.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.Common.Listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.CustomBasicTextView;
import com.minami_m.project.android.wakemeapp.Model.Alarm;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Screen.Main.MainActivity;
import com.minami_m.project.android.wakemeapp.Screen.MyPage.MypageActivity;
import com.minami_m.project.android.wakemeapp.Screen.SignIn.SignInActivity;

import java.util.Map;

public class AlarmActivity extends AppCompatActivity implements ActivityChangeListener {
    private Switch alarmSwitch, notificationSwitch, repeatSwitch;
    private TextView wakeUpTime, wakeUpTimeAmPm, repeatInWeek;
    private CustomBasicTextView changeSettingsText;
    private LinearLayout config;
    private GridLayout repeatOptions;
    private Alarm alarm;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private static final int DEFAULT_HOUR = 7;
    private static final int DEFAULT_MINUTE = 0;
    private View[] optionButtons = new View[7];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        currentUser = firebaseAuth.getCurrentUser();
        Toolbar toolbar = findViewById(R.id.toolbar_alarm);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setupSwitches();
        setupWakeUpTime();
        repeatOptions = findViewById(R.id.repeat_switch_options);
        repeatInWeek = findViewById(R.id.repeat_in_week);
        config = findViewById(R.id.config_alarm);
        changeSettingsText = findViewById(R.id.change_settings);
        changeSettingsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (config.getVisibility() != View.GONE) {
                    config.setVisibility(View.GONE);
                } else {
                    config.setVisibility(View.VISIBLE);
                }
            }
        });
        createAlarm();
        setupOptionButtons();
    }

    private void createAlarm() {
        alarm = new Alarm(DEFAULT_HOUR, DEFAULT_MINUTE);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        if (data != null) {
            alarm = data.getParcelable("Alarm");
            System.out.println(alarm);
            if (alarm != null) {
                Map<String, String> formattedTime = DateAndTimeFormatHandler.generateFormattedAlarmTime(
                        alarm.getHourOfDay(),
                        alarm.getMinute());
                wakeUpTime.setText(formattedTime.get("time"));
                wakeUpTimeAmPm.setText(formattedTime.get("am_pm"));
                alarmSwitch.setChecked(alarm.getAlarmIsOn());
                notificationSwitch.setChecked(alarm.getNotificationIsOn());
                repeatSwitch.setChecked(alarm.getRepeatIsOn());
                repeatInWeek.setText(alarm.getAlarmOnDayDescription());
            }
        }
    }



    private void setStyleAsAlarmOn(boolean on) {
        if (on) {
            wakeUpTime.setTextColor(getResources().getColor(R.color.colorMyAccent));
            wakeUpTime.setAlpha(1);
            wakeUpTimeAmPm.setTextColor(getResources().getColor(R.color.colorMyAccent));
            wakeUpTimeAmPm.setAlpha(1);
            repeatInWeek.setAlpha(1);
        } else {
            wakeUpTime.setTextColor(getResources().getColor(R.color.black));
            wakeUpTime.setAlpha(0.3f);
            wakeUpTimeAmPm.setTextColor(getResources().getColor(R.color.black));
            wakeUpTimeAmPm.setAlpha(0.3f);
            repeatInWeek.setAlpha(0.3f);
        }

    }

    private void setupWakeUpTime() {
        wakeUpTime = findViewById(R.id.wake_up_time);
        FontStyleHandler.setFont(this, wakeUpTime, false, true);
        wakeUpTimeAmPm = findViewById(R.id.wake_up_time_am_pm);
        FontStyleHandler.setFont(this, wakeUpTimeAmPm, false, true);
        wakeUpTime.setOnClickListener(showTimePicker());
    }

    private void setupSwitches() {
        alarmSwitch = findViewById(R.id.switch_alarm);
        FontStyleHandler.setFont(this, alarmSwitch, false, false);
        alarmSwitch.setOnCheckedChangeListener(toggleAlarm());
        notificationSwitch = findViewById(R.id.switch_notification);
        FontStyleHandler.setFont(this, notificationSwitch, false, false);
        notificationSwitch.setOnCheckedChangeListener(toggleNotification());
        repeatSwitch = findViewById(R.id.switch_repeat);
        FontStyleHandler.setFont(this, repeatSwitch, false, false);
        repeatSwitch.setOnCheckedChangeListener(toggleRepeat());
    }

    private CompoundButton.OnCheckedChangeListener toggleAlarm() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setStyleAsAlarmOn(isChecked);
                if (isChecked) {
                    alarm.setAlarmIsOn(isChecked);
                } else {
                    alarm.turnOffAlarm();
                    notificationSwitch.setChecked(isChecked);
                    repeatSwitch.setChecked(isChecked);
                }
                FirebaseRealtimeDatabaseHelper.updateAlarm(currentUser, alarm);

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener toggleNotification() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.setNotificationIsOn(isChecked);
                if (isChecked) {
                    alarmSwitch.setChecked(isChecked);
                }
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener toggleRepeat() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.setRepeatIsOn(isChecked);
                if (isChecked) {
                    if (alarm.checkRepeatOnDayOfWeekIsAllOff()) {
                        alarm.turnOnRepeatingOnEveryday();
                    }
                    changeVisibilityForAllOptions();
                    alarmSwitch.setChecked(isChecked);
                    repeatOptions.setVisibility(View.VISIBLE);
                } else {
                    repeatOptions.setVisibility(View.GONE);
                }
                FirebaseRealtimeDatabaseHelper.updateAlarm(currentUser, alarm);
                repeatInWeek.setText(alarm.getAlarmOnDayDescription());

            }
        };
    }

    private void setupOptionButtons() {
        for (int i = 0; i < repeatOptions.getChildCount(); i++) {
            ViewGroup frameLayout = (ViewGroup) repeatOptions.getChildAt(i);
            frameLayout.setTag(i);
            frameLayout.setOnClickListener(toggleRepeatingOptions());
            View optionButton = frameLayout.getChildAt(0);
            if (alarm.myGetRepeatOnDay()[i]) {
                optionButton.setVisibility(View.VISIBLE);
            } else {
                optionButton.setVisibility(View.INVISIBLE);
            }
            optionButtons[i] = optionButton;
        }
    }

    private void changeVisibilityForAllOptions() {
        for (int i = 0; i < repeatOptions.getChildCount(); i++) {
            View optionButton = ((ViewGroup) repeatOptions.getChildAt(i)).getChildAt(0);
            if (alarm.myGetRepeatOnDay()[i]) {
                optionButton.setVisibility(View.VISIBLE);
                alarm.myGetRepeatOnDay()[i] = true;
            } else {
                optionButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    private View.OnClickListener toggleRepeatingOptions() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View frameLayout) {
                int index = (int) frameLayout.getTag();
                View optionButton = optionButtons[index];
                alarm.toggleRepeatOnDayAt(index);
                if (optionButton.getVisibility() == View.VISIBLE) {
                    optionButton.setVisibility(View.INVISIBLE);
                } else {
                    optionButton.setVisibility(View.VISIBLE);
                }
                if (alarm.checkRepeatOnDayOfWeekIsAllOff()) {
                    repeatSwitch.setChecked(false);
                }
                repeatInWeek.setText(alarm.getAlarmOnDayDescription());
                FirebaseRealtimeDatabaseHelper.updateAlarm(currentUser, alarm);
            }
        };
    }

    private View.OnClickListener showTimePicker() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(
                        AlarmActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Map<String, String> formattedTime =
                                        DateAndTimeFormatHandler.generateFormattedAlarmTime(
                                                hourOfDay,
                                                minute);
                                wakeUpTime.setText(formattedTime.get("time"));
                                wakeUpTime.setAlpha(1);
                                wakeUpTimeAmPm.setText(formattedTime.get("am_pm"));
                                wakeUpTimeAmPm.setAlpha(1);
                                alarm.setAlarmIsOn(true);
                                alarm.setHourOfDay(hourOfDay);
                                alarm.setMinute(minute);
                                alarmSwitch.setChecked(true);
                                repeatInWeek.setText(alarm.getAlarmOnDayDescription());
                                FirebaseRealtimeDatabaseHelper.updateAlarm(currentUser, alarm);

                            }
                        },
                        alarm.getHourOfDay(),
                        alarm.getMinute(),
                        false);
                dialog.show();
            }
        };
    }



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
