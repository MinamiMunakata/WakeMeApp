package com.minami_m.project.android.wakemeapp.SearchFriend;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.FragmentChangeListener;
import com.minami_m.project.android.wakemeapp.InputHandler;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.inputValidationHandler;

public class SearchFriendActivity extends AppCompatActivity implements FragmentChangeListener, inputValidationHandler {
    private Button search_btn;
    private EditText editEmail;
    private ValueEventListener searchListner;
    private static final String TAG = "SearchFriendActivity";

    public void setEditEmail(EditText editEmail) {
        this.editEmail = editEmail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        search_btn = findViewById(R.id.search_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: 12345 no email field?");
                if (editEmail != null) {
                    Log.i(TAG, "onClick: 12345 success!");
                    if (isValidInput()) {
                        Log.i(TAG, "onClick: " + editEmail.getText().toString());
                        searchFriendByEmail(editEmail.getText().toString());
                    }

                }
            }
        });
    }

    public void searchFriendByEmail(String email) {
        FirebaseRealtimeDatabaseHelper.USERS_REF.addListenerForSingleValueEvent(searchListner);

        searchListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user: dataSnapshot.getChildren()) {
                    Log.i(TAG, "onDataChange: " + user.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }

    @Override
    public void replaceFragment(Fragment newFragment) {

    }


    @Override
    public boolean isValidInput() {
        return InputHandler.isValidFormEmail(editEmail);
    }
}
