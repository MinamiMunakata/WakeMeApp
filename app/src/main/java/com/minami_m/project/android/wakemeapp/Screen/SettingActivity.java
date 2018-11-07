package com.minami_m.project.android.wakemeapp.Screen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.minami_m.project.android.wakemeapp.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.FirebaseStorageHelper;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Screen.SignIn.SignInActivity;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SettingActivity extends AppCompatActivity implements ActivityChangeListener {
    private static final String TAG = "SettingActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView profileIcon;
    private TextView profileName;
    private final int PICK_IMAGE_REQUEST = 6789;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        profileIcon = findViewById(R.id.setting_profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        profileName = findViewById(R.id.setting_profile_name);
        Toolbar toolbar = findViewById(R.id.my_toolbar_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

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
        profileName.setText(currentUser.getDisplayName());
        FirebaseRealtimeDatabaseHelper.USERS_REF.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String path = dataSnapshot.child("icon").getValue(String.class);
                // TODO: set default image
                Picasso.get().load(path).error(R.drawable.user_icon).into(profileIcon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
                Log.i(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // TODO: on setting page...? just logout?
            case R.id.setting_menu:
                Log.i(TAG, "onOptionsItemSelected: 1234567 setting!");
                return true;
            case R.id.logout_menu:
                Log.i(TAG, "onOptionsItemSelected: 1234567 logout!");
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
