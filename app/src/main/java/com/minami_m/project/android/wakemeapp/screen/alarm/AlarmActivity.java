package com.minami_m.project.android.wakemeapp.screen.alarm;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minami_m.project.android.wakemeapp.CustomBasicTextView;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.DateAndTimeFormatHandler;
import com.minami_m.project.android.wakemeapp.common.handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.common.helper.FBRealTimeDBHelper;
import com.minami_m.project.android.wakemeapp.common.listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.model.WakeUpTime;
import com.minami_m.project.android.wakemeapp.screen.main.MainActivity;
import com.minami_m.project.android.wakemeapp.screen.myPage.MyPageActivity;
import com.minami_m.project.android.wakemeapp.screen.signIn.SignInActivity;

import java.util.Map;

public class AlarmActivity extends AppCompatActivity implements ActivityChangeListener {
    private static final int DEFAULT_HOUR = 7;
    private static final int DEFAULT_MINUTE = 0;
    private Switch mustWakeUpSwitch, repeatSwitch;
    private TextView wakeUpTimeTextView, wakeUpTimeAmPm, repeatInWeek;
    private CustomBasicTextView changeSettingsText;
    private LinearLayout config;
    private GridLayout repeatOptions;
    private WakeUpTime wakeUpTime;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private View[] optionButtons = new View[7];
    private Button alarmButton;
    private NotificationController notifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        setupToolBar();
        initialization();
        currentUser = firebaseAuth.getCurrentUser();
        createWakeUpTime();
        setupSwitches();
        setupWakeUpTime();
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
        setupOptionButtons();
        setupAlarmButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            launchActivity(SignInActivity.class);
        } else {
            notifier = new NotificationController(this);
        }
    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_alarm);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initialization() {
        alarmButton = findViewById(R.id.alarm_button);
        mustWakeUpSwitch = findViewById(R.id.switch_must_wake_up);
        repeatSwitch = findViewById(R.id.switch_repeat);
        wakeUpTimeTextView = findViewById(R.id.wake_up_time);
        wakeUpTimeAmPm = findViewById(R.id.wake_up_time_am_pm);
        repeatOptions = findViewById(R.id.repeat_switch_options);
        repeatInWeek = findViewById(R.id.repeat_in_week);
        config = findViewById(R.id.config_alarm);
        changeSettingsText = findViewById(R.id.change_settings);
    }

    public void createAlarm(WakeUpTime wakeUpTime) {
        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_HOUR, wakeUpTime.getHourOfDay())
                .putExtra(AlarmClock.EXTRA_MINUTES, wakeUpTime.getMinute())
                .putExtra(AlarmClock.EXTRA_MESSAGE, "WakeMeApp");
        if (wakeUpTime.getRepeatIsOn()) {
            alarmIntent.putExtra(AlarmClock.EXTRA_DAYS, wakeUpTime.generateExtraDays());
        }
        if (alarmIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(alarmIntent);
        }
    }

    private void createWakeUpTime() {
        wakeUpTime = new WakeUpTime(DEFAULT_HOUR, DEFAULT_MINUTE);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        if (data != null && data.getParcelable("WakeUpTime") != null) {
            wakeUpTime = data.getParcelable("WakeUpTime");
            if (wakeUpTime != null) {
                Map<String, String> formattedTime = DateAndTimeFormatHandler.generateFormattedAlarmTime(
                        wakeUpTime.getHourOfDay(),
                        wakeUpTime.getMinute());
                wakeUpTimeTextView.setText(formattedTime.get("time"));
                wakeUpTimeAmPm.setText(formattedTime.get("am_pm"));
                mustWakeUpSwitch.setChecked(wakeUpTime.getMustWakeUp());
                repeatSwitch.setChecked(wakeUpTime.getRepeatIsOn());
                if (wakeUpTime.getRepeatIsOn()) repeatOptions.setVisibility(View.VISIBLE);
                repeatInWeek.setText(wakeUpTime.generateAlarmOnDayDescription());
            }
        }
    }


    private void setStyleDependsOnMustWakeUp(boolean on) {
        if (on) {
            wakeUpTimeTextView.setTextColor(getColor(R.color.colorMyAccent));
            wakeUpTimeTextView.setAlpha(1);
            wakeUpTimeAmPm.setTextColor(getColor(R.color.colorMyAccent));
            wakeUpTimeAmPm.setAlpha(1);
            repeatInWeek.setAlpha(1);
        } else {
            wakeUpTimeTextView.setTextColor(getColor(R.color.black));
            wakeUpTimeTextView.setAlpha(0.3f);
            wakeUpTimeAmPm.setTextColor(getColor(R.color.black));
            wakeUpTimeAmPm.setAlpha(0.3f);
            repeatInWeek.setAlpha(0.3f);
        }

    }

    private void setupWakeUpTime() {
        wakeUpTimeTextView.setPaintFlags(wakeUpTimeTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        FontStyleHandler.setFont(this, wakeUpTimeTextView, false, true);
        FontStyleHandler.setFont(this, wakeUpTimeAmPm, false, true);
        if (wakeUpTime.getMustWakeUp()) {
            wakeUpTimeTextView.setTextColor(getColor(R.color.colorMyAccent));
            wakeUpTimeTextView.setAlpha(1);
            wakeUpTimeAmPm.setTextColor(getColor(R.color.colorMyAccent));
            wakeUpTimeAmPm.setAlpha(1);
            repeatInWeek.setAlpha(1);
        }
        wakeUpTimeTextView.setOnClickListener(showTimePicker());
    }

    private void setupSwitches() {
        FontStyleHandler.setFont(this, mustWakeUpSwitch, false, false);
        mustWakeUpSwitch.setOnCheckedChangeListener(toggleMustWakeUp());
        FontStyleHandler.setFont(this, repeatSwitch, false, false);
        repeatSwitch.setOnCheckedChangeListener(toggleRepeat());
    }

    private void setupAlarmButton() {
        FontStyleHandler.setFont(this, alarmButton, false, false);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!wakeUpTime.getMustWakeUp()) {
//                    mustWakeUpSwitch.setChecked(true);
//                }
//                createAlarm(wakeUpTime);
                // TODO: Delete
                notifier.checkIfNotificationIsWorking();
                Map<String, String> timeMap = DateAndTimeFormatHandler
                        .generateFormattedAlarmTime(
                                wakeUpTime.getHourOfDay(),
                                wakeUpTime.getMinute());
                StringBuilder time = new StringBuilder();
                time.append(DateAndTimeFormatHandler.generateDateOfChat(wakeUpTime.getWakeUpTimeInMillis()));
                time.append(" ");
                time.append(timeMap.get("full time"));
                Toast.makeText(getApplicationContext(), time, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private CompoundButton.OnCheckedChangeListener toggleMustWakeUp() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setStyleDependsOnMustWakeUp(isChecked);
                if (isChecked) {
                    wakeUpTime.turnOnMustWakeUp(true);
                    notifier.setAllNotification(wakeUpTime);

                } else {
                    wakeUpTime.turnOffAlarm();
                    repeatSwitch.setChecked(false);
                    notifier.cancelAllNotification();
                }
                FBRealTimeDBHelper.updateWakeUpTIme(currentUser, wakeUpTime);
                repeatInWeek.setText(wakeUpTime.generateAlarmOnDayDescription());

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener toggleRepeat() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wakeUpTime.setRepeatIsOn(isChecked);
                if (isChecked) {
                    if (wakeUpTime.repeatOnDayOfWeekIsAllOff()) {
                        wakeUpTime.turnOnRepeatingOnEveryday();
                    }
                    changeVisibilityForAllOptions();
                    mustWakeUpSwitch.setChecked(true);
                    notifier.setAllNotification(wakeUpTime);
                    repeatOptions.setVisibility(View.VISIBLE);
                } else {
                    repeatOptions.setVisibility(View.GONE);
                    notifier.cancelIfRepeatOff(wakeUpTime);
                }
                FBRealTimeDBHelper.updateWakeUpTIme(currentUser, wakeUpTime);
                repeatInWeek.setText(wakeUpTime.generateAlarmOnDayDescription());

            }
        };
    }

    private void setupOptionButtons() {
        for (int i = 0; i < repeatOptions.getChildCount(); i++) {
            ViewGroup frameLayout = (ViewGroup) repeatOptions.getChildAt(i);
            frameLayout.setTag(i);
            frameLayout.setOnClickListener(toggleRepeatingOptions());
            View optionButton = frameLayout.getChildAt(0);
            if (wakeUpTime.generateRepeatOnDay()[i]) {
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
            if (wakeUpTime.generateRepeatOnDay()[i]) {
                optionButton.setVisibility(View.VISIBLE);
                wakeUpTime.generateRepeatOnDay()[i] = true;
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
                wakeUpTime.toggleRepeatOnDayAt(index);
                int day = convertToDayOfWeek(index);
                if (optionButton.getVisibility() == View.VISIBLE) {
                    optionButton.setVisibility(View.INVISIBLE);
                    notifier.cancelAt(day);
                } else {
                    optionButton.setVisibility(View.VISIBLE);
                    notifier.setNotificationAt(wakeUpTime, day);
                }
                if (wakeUpTime.repeatOnDayOfWeekIsAllOff()) {
                    repeatSwitch.setChecked(false);
                }
                repeatInWeek.setText(wakeUpTime.generateAlarmOnDayDescription());
                FBRealTimeDBHelper.updateWakeUpTIme(currentUser, wakeUpTime);
            }
        };
    }

    private int convertToDayOfWeek(int tag) {
        if (tag == 6) {
            return 1;
        } else {
            return tag + 2;
        }
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
                                wakeUpTimeTextView.setText(formattedTime.get("time"));
                                wakeUpTimeTextView.setAlpha(1);
                                wakeUpTimeAmPm.setText(formattedTime.get("am_pm"));
                                wakeUpTimeAmPm.setAlpha(1);
                                wakeUpTime.setHourOfDay(hourOfDay);
                                wakeUpTime.setMinute(minute);
                                wakeUpTime.turnOnMustWakeUp(true);
                                mustWakeUpSwitch.setChecked(true);
                                repeatInWeek.setText(wakeUpTime.generateAlarmOnDayDescription());
                                FBRealTimeDBHelper.updateWakeUpTIme(currentUser, wakeUpTime);

                            }
                        },
                        wakeUpTime.getHourOfDay(),
                        wakeUpTime.getMinute(),
                        false);
                dialog.show();
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
//        SDK >= 28
//        MenuCompat.setGroupDividerEnabled(menu, true);
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
                launchActivity(MyPageActivity.class);
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
