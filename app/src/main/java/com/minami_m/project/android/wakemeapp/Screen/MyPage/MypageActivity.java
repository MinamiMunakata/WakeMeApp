package com.minami_m.project.android.wakemeapp.Screen.MyPage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.minami_m.project.android.wakemeapp.Common.Listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.Common.Helper.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.Common.Helper.FirebaseStorageHelper;
import com.minami_m.project.android.wakemeapp.Common.Handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Screen.Alarm.AlarmActivity;
import com.minami_m.project.android.wakemeapp.Screen.Main.MainActivity;
import com.minami_m.project.android.wakemeapp.Screen.SignIn.SignInActivity;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MypageActivity extends AppCompatActivity implements ActivityChangeListener {
    private static final String TAG = "SettingActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView profileIcon;
    private TextView profileName;
    private final int PICK_IMAGE_REQUEST = 6789;
    private Uri filePath;
    private TextView nickname, email, pw;
    private EditText nicknameTextField, emailTextField, pwTextField, timer_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
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
        getSupportActionBar().setTitle(null);
        setupUserInfoCategoryName();
        timer_box = findViewById(R.id.wake_up_time);
        timer_box.setInputType(InputType.TYPE_NULL);
        timer_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.slide_out_left, R.animator.slide_in_right);
            }
        });
        timer_box.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.animator.slide_out_left, R.animator.slide_in_right);
                }
            }
        });
    }

    private void setupUserInfo() {
        profileName = findViewById(R.id.setting_profile_name);
        FontStyleHandler.setFont(this, profileName, true, true);
        nicknameTextField = findViewById(R.id.edit_profile_name);
        FontStyleHandler.setFont(this, nicknameTextField, false, false);
        emailTextField = findViewById(R.id.edit_profile_email);
        FontStyleHandler.setFont(this, emailTextField, false, false);
        pwTextField = findViewById(R.id.edit_profile_pw);
        FontStyleHandler.setFont(this, pwTextField, false, false);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            profileName.setText(currentUser.getDisplayName());
            nicknameTextField.setText(currentUser.getDisplayName());
            emailTextField.setText(currentUser.getEmail());
        }

    }

    private void setupUserInfoCategoryName() {
        nickname = findViewById(R.id.profile_nickname);
        email = findViewById(R.id.profile_email);
        pw = findViewById(R.id.profile_pw);
        FontStyleHandler.setFont(this, nickname, false, true);
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

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            FirebaseStorageHelper.ICON_REF(currentUser).putFile(filePath)
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
                            FirebaseStorageHelper.ICON_REF(currentUser).getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String downloadIconURL = task.getResult().toString();
                                            // TODO: update data at firebase
                                            FirebaseRealtimeDatabaseHelper.updateIcon(currentUser, downloadIconURL);
                                        }
                                    });

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // TODO: Action on progress...
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
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String path = dataSnapshot.child("icon").getValue(String.class);
                // TODO: set default image
                Log.i(TAG, "onDataChange: " + path);
                if (path == null) {
                    profileIcon.setImageResource(R.drawable.ico_default_avator);
                } else {
                    Picasso.get().load(path).error(R.drawable.ico_default_avator).into(profileIcon);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
                Log.i(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };
        try {
            profileName.setText(currentUser.getDisplayName());
            FirebaseRealtimeDatabaseHelper.USERS_REF.child(currentUser.getUid()).addValueEventListener(listener);
        } catch (Exception e) {
            launchActivity(SignInActivity.class);
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuCompat.setGroupDividerEnabled(menu,true);
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
}
