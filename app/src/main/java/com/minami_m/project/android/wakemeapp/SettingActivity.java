package com.minami_m.project.android.wakemeapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.SignIn.SignInActivity;
import com.squareup.picasso.Picasso;

public class SettingActivity extends AppCompatActivity implements ActivityChangeListener {
    private static final String TAG = "SettingActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView profileIcon;
    private TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        profileIcon = findViewById(R.id.setting_profile_icon);
        profileName = findViewById(R.id.setting_profile_name);
        Toolbar toolbar = findViewById(R.id.my_toolbar_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

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
}
