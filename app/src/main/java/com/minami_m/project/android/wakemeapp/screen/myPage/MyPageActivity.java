package com.minami_m.project.android.wakemeapp.screen.myPage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.DateAndTimeFormatHandler;
import com.minami_m.project.android.wakemeapp.common.handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.common.helper.FBRealTimeDBHelper;
import com.minami_m.project.android.wakemeapp.common.helper.FBStorageHelper;
import com.minami_m.project.android.wakemeapp.common.listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.model.WakeUpTime;
import com.minami_m.project.android.wakemeapp.screen.alarm.AlarmActivity;
import com.minami_m.project.android.wakemeapp.screen.main.MainActivity;
import com.minami_m.project.android.wakemeapp.screen.signIn.SignInActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

//import com.minami_m.project.android.wakemeapp.Screen.Alarm.AlarmActivity;

public class MyPageActivity extends AppCompatActivity implements ActivityChangeListener {
    private static final String TAG = "SettingActivity";
    private static final int NAME = 77, EMAIL = 78, PW = 79;
    private final int PICK_IMAGE_REQUEST = 6789;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView profileIcon;
    private TextView profileName;
    private Uri filePath;
    private TextView displayName, email, pw, errorMsg;
    private EditText displayNameTextField, emailTextField, pwTextField, timer_box;
    private WakeUpTime wakeUpTime;
    private ValueEventListener listener;
    private boolean isListening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        errorMsg = findViewById(R.id.my_page_error);
        profileIcon = findViewById(R.id.setting_profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        setupUserInfo();
        Toolbar toolbar = findViewById(R.id.my_toolbar_setting);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
        setupUserInfoCategoryName();
        setupTimeInfo();
    }

    private void setupTimeInfo() {
        timer_box = findViewById(R.id.wake_up_time);
        timer_box.setInputType(InputType.TYPE_NULL);
        timer_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                if (wakeUpTime != null) {
                    intent.putExtra("WakeUpTime", wakeUpTime);
                }
                startActivity(intent);
                overridePendingTransition(R.animator.slide_out_left, R.animator.slide_in_right);
            }
        });
        timer_box.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                    if (wakeUpTime != null) {
                        intent.putExtra("WakeUpTime", wakeUpTime);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.animator.slide_out_left, R.animator.slide_in_right);
                }
            }
        });
        FontStyleHandler.setFont(this, timer_box, false, true);
    }

    private void setupUserInfo() {
        profileName = findViewById(R.id.setting_profile_name);
        FontStyleHandler.setFont(this, profileName, true, true);
        displayNameTextField = findViewById(R.id.edit_profile_name);
        FontStyleHandler.setFont(this, displayNameTextField, false, false);
        displayNameTextField.setOnEditorActionListener(getOnEditorActionListener(NAME));
        emailTextField = findViewById(R.id.edit_profile_email);
        FontStyleHandler.setFont(this, emailTextField, false, false);
        emailTextField.setOnEditorActionListener(getOnEditorActionListener(EMAIL));
        pwTextField = findViewById(R.id.edit_profile_pw);
        FontStyleHandler.setFont(this, pwTextField, false, false);
        pwTextField.setOnEditorActionListener(getOnEditorActionListener(PW));
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Capitalize the first letter of an user name.
            if (currentUser.getDisplayName() != null && currentUser.getDisplayName().length() > 0) {
                String[] fullName = currentUser.getDisplayName().split(" ");
                StringBuilder displayName = new StringBuilder();
                for (int i = 0; i < fullName.length; i++) {
                    String name = fullName[i];
                    if (name.length() > 0) {
                        displayName
                                .append(name.substring(0, 1).toUpperCase())
                                .append(name.substring(1));
                    }
                    if (i < fullName.length - 1) {
                        displayName.append(" ");
                    }
                }
                profileName.setText(displayName);
            } else {
                profileName.setText(currentUser.getDisplayName());
            }
            displayNameTextField.setText(currentUser.getDisplayName());
            emailTextField.setText(currentUser.getEmail());
        }

    }

    @NonNull
    private TextView.OnEditorActionListener getOnEditorActionListener(final int field) {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    switch (field) {
                        case NAME:
                            // TODO: input validation
                            updateName(textView.getText().toString());
                            return true;
                        case EMAIL:
                            updateEmail(textView.getText().toString());
                            return true;
                        case PW:
                            updatePassword(textView.getText().toString());
                            return true;
                    }

                }
                return false;
            }
        };
    }

    private void setupUserInfoCategoryName() {
        displayName = findViewById(R.id.profile_nickname);
        email = findViewById(R.id.profile_email);
        pw = findViewById(R.id.profile_pw);
        FontStyleHandler.setFont(this, displayName, false, true);
        FontStyleHandler.setFont(this, email, false, true);
        FontStyleHandler.setFont(this, pw, false, true);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileIcon.setImageBitmap(bitmap);
                uploadImage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            FBStorageHelper.ICON_REF(currentUser).putFile(filePath)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toast("Failed... " + e.getMessage());
                            Log.i(TAG, "onFailure: " + e.getMessage());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            toast("Successfully Uploaded");
                            FBStorageHelper.ICON_REF(currentUser).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadIconURL = uri.toString();
                                    System.out.println("DL -> " + downloadIconURL);
                                    FBRealTimeDBHelper.updateIcon(currentUser, downloadIconURL);
                                }
                            });
//                            FBStorageHelper.ICON_REF(currentUser).getDownloadUrl()
//                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Uri> task) {
//                                            String downloadIconURL = task.getResult().toString();
//                                            System.out.println();
//                                            FBRealTimeDBHelper.updateIcon(currentUser, downloadIconURL);
//                                        }
//                                    });

                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            launchActivity(SignInActivity.class);
        }
        try {
            setIconAndAlarmTimeFromFB();
        } catch (Exception e) {
            launchActivity(SignInActivity.class);
            finish();
        }

    }

    private void setIconAndAlarmTimeFromFB() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isListening) {
                    setupIcon(dataSnapshot);
                    wakeUpTime = dataSnapshot.child("wakeUpTime").getValue(WakeUpTime.class);
                    setupWakeUpTime();
                }
            }

            private void setupWakeUpTime() {
                if (wakeUpTime != null) {
                    if (wakeUpTime.getMustWakeUp()) {
                        if (!wakeUpTime.getRepeatIsOn() &&
                                wakeUpTime.getWakeUpTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                            wakeUpTime.setMustWakeUp(false);
                        } else {
                            timer_box.setTextColor(getColor(R.color.colorMyAccent));
                            timer_box.setAlpha(1);
                        }
                    } else {
                        timer_box.setTextColor(getColor(R.color.black));
                        timer_box.setAlpha(0.3f);
                    }
                    Map<String, String> formattedTime = DateAndTimeFormatHandler
                            .generateFormattedAlarmTime(
                                    wakeUpTime.getHourOfDay(),
                                    wakeUpTime.getMinute());
                    timer_box.setText(formattedTime.get("full time"));
                }
            }

            private void setupIcon(@NonNull DataSnapshot dataSnapshot) {
                String path = dataSnapshot.child("icon").getValue(String.class);
                if (path == null) {
                    profileIcon.setImageResource(R.drawable.ico_default_avator);
                } else {
                    Picasso.get().load(path).error(R.drawable.ico_default_avator).into(profileIcon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        };
        isListening = true;
        FBRealTimeDBHelper.USERS_REF.child(currentUser.getUid()).addValueEventListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
//        SDK >= 28
//        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu parentMenu) {
        FontStyleHandler.setCustomFontOnMenuItems(parentMenu, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_menu:
                launchActivity(MainActivity.class);
                return true;
            case R.id.my_page_menu:
                return true;
            case R.id.logout_menu:
                isListening = false;
                FBRealTimeDBHelper.USERS_REF.child(currentUser.getUid()).removeEventListener(listener);
                FirebaseAuth.getInstance().signOut();
                launchActivity(SignInActivity.class);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void launchActivity(Class nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void toggleEditMode(View view) {
        switch (view.getId()) {
            case R.id.name_editor:
                removeFocus(displayNameTextField);
                setEditable(displayNameTextField);
                displayNameTextField.setText(currentUser.getDisplayName());
                break;
            case R.id.email_editor:
                removeFocus(emailTextField);
                setEditable(emailTextField);
                emailTextField.setText(currentUser.getEmail());
                break;
            case R.id.pw_editor:
                removeFocus(pwTextField);
                setEditable(pwTextField);
                pwTextField.setText("");
                break;
            default:
                break;
        }
    }

    private void setEditable(EditText editText) {
        editText.setEnabled(!editText.isEnabled());
        if (editText.isEnabled()) {
            editText.requestFocus();
        } else {
            errorMsg.setText("");
        }
    }

    private void removeFocus(EditText editText) {
        EditText[] editTexts = new EditText[]{displayNameTextField, emailTextField, pwTextField};
        for (EditText et : editTexts) {
            if (editText != et) {
                et.setEnabled(false);
            }
        }
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_ENTER:
//                switch (getCurrentFocus().getId()) {
//                    case R.id.edit_profile_name:
//                        updateName(((EditText) getCurrentFocus()).getText().toString());
//                        break;
//                    case R.id.edit_profile_email:
//                        updateEmail(((EditText) getCurrentFocus()).getText().toString());
//                        break;
//                    case R.id.edit_profile_pw:
//                        updatePassword(((EditText) getCurrentFocus()).getText().toString());
//                        break;
//                    default:
//                        System.out.println(getCurrentFocus().getId());
//                        System.out.println(displayNameTextField.getId());
//                        break;
//                }
//                InputHandler.hideSoftKeyBoard(this);
//                return true;
//            default:
//                return super.onKeyUp(keyCode, event);
//        }
//    }

    public void updatePassword(final String input) {
        currentUser.updatePassword(input).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    pwTextField.setEnabled(false);
                    toast("Password is successfully updated.");
                } else {
                    toast("Failed to update");
                    if (task.getException() != null) {
                        errorMsg.setText(task.getException().getMessage());
                    }
                    Log.e(TAG, "onComplete: ", task.getException());
                }
            }
        });
    }

    public void updateEmail(final String input) {
        currentUser.updateEmail(input).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    emailTextField.setEnabled(false);
                    saveEmailToFB(input);
                } else {
                    toast("Failed to update");
                    if (task.getException() != null) {
                        errorMsg.setText(task.getException().getMessage());
                    }
                    Log.e(TAG, "onComplete: ", task.getException());
                }
            }
        });
    }

    private void saveEmailToFB(final String input) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/" + currentUser.getUid() + "/email", input);
        DatabaseReference receiverPathRef = database.getReference("ReceiverPaths");
        receiverPathRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot path : dataSnapshot.getChildren()) {
                    childUpdates.put(path.getValue() + "/email", input);
                }
                database.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            toast("Failed to update.");
                            errorMsg.setText(databaseError.getMessage());
                            Log.e(TAG, "onComplete: ", databaseError.toException());
                        } else {
                            toast("Email is successfully updated.");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }

    // TODO: Check validation.
    public void updateName(final String input) {
        UserProfileChangeRequest nameUpdateRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(input)
                .build();
        currentUser.updateProfile(nameUpdateRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            displayNameTextField.setEnabled(false);
                            saveNameToFB(input);
                        } else {
                            toast("Failed to update.");
                            if (task.getException() != null) {
                                errorMsg.setText(task.getException().getMessage());
                            }
                            Log.e(TAG, "onComplete: ", task.getException());
                        }
                    }
                });


    }

    private void saveNameToFB(final String input) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/" + currentUser.getUid() + "/name", input);
        DatabaseReference receiverPathRef = database.getReference("ReceiverPaths");
        receiverPathRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot path : dataSnapshot.getChildren()) {
                    childUpdates.put(path.getValue() + "/name", input);
                }
                database.getReference().updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            toast("Failed to update.");
                            errorMsg.setText(databaseError.getMessage());
                            Log.e(TAG, "onComplete: ", databaseError.toException());
                        } else {
                            profileName.setText(input);
                            toast("User name is successfully updated.");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        errorMsg.setText("");
        isListening = false;
        FBRealTimeDBHelper.USERS_REF.child(currentUser.getUid()).removeEventListener(listener);

    }
}
