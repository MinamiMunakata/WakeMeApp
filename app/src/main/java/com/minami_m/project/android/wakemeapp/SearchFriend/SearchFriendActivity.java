package com.minami_m.project.android.wakemeapp.SearchFriend;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.FragmentChangeListener;
import com.minami_m.project.android.wakemeapp.InputHandler;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.inputValidationHandler;

public class SearchFriendActivity extends AppCompatActivity implements FragmentChangeListener, inputValidationHandler {
    private Button search_btn;
    private EditText editEmail;
    private static final String TAG = "SearchFriendActivity";
    private FirebaseUser user;

    public void setEditEmail(EditText editEmail) {
        this.editEmail = editEmail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_search_friend);
        search_btn = findViewById(R.id.search_button);
        user = FirebaseAuth.getInstance().getCurrentUser();
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

    public void searchFriendByEmail(final String email) {
        if (email.equals(user.getEmail())) {
            Toast.makeText(this, "Search friend's ID", Toast.LENGTH_SHORT).show();
            return;
        }
        ValueEventListener searchListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.child("email").getValue().equals(email)) {
                        Log.i(TAG, "onDataChange: " + userSnapshot.getKey());
                        replaceFragment(SearchFriendResultFragment.newInstance());
//                        followNewFriend(user.getUid(), userSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FirebaseRealtimeDatabaseHelper.USERS_REF.addListenerForSingleValueEvent(searchListener);
    }

    public void followNewFriend(final String currentUserId, final String friendId) {
        FirebaseRealtimeDatabaseHelper.FRIEND_ID_LIST_REF
                .child(currentUserId)
                .child(friendId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Already following the user", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseRealtimeDatabaseHelper.followFriend(currentUserId, friendId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void replaceFragment(Fragment newFragment) {
        TransitionSet set = new TransitionSet();
        set.addTransition(new Fade());
        newFragment.setEnterTransition(set);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.search_friend_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public boolean isValidInput() {
        return InputHandler.isValidFormEmail(editEmail);
    }

}
