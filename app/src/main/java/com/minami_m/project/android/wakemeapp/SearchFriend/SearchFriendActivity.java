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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.Login;
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
import com.minami_m.project.android.wakemeapp.User;
import com.minami_m.project.android.wakemeapp.inputValidationHandler;
import com.squareup.picasso.Picasso;

public class SearchFriendActivity extends AppCompatActivity implements FragmentChangeListener, inputValidationHandler, SearchFriendFragmentListener {
    private Button search_btn;
    private EditText editEmail;
    private static final String TAG = "SearchFriendActivity";
    private FirebaseUser user;
    private String friendId;
    private User friend;
    private static final String DIALOG_TAG = "dialog";

    public void setEditEmail(EditText editEmail) {
        this.editEmail = editEmail;
    }

    // TODO: Implement BACK or CANCEL to input search field again.

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
                if (search_btn.getText().equals(getResources().getString(R.string.search_email))) {
                    Log.i(TAG, "onClick: 12345 no email field?");
                    if (editEmail != null) {
                        Log.i(TAG, "onClick: 12345 success!");
                        if (isValidInput()) {
                            Log.i(TAG, "onClick: " + editEmail.getText().toString());
                            searchFriendByEmail(editEmail.getText().toString());
                        }
                    }
                } else if (search_btn.getText()
                        .equals(getResources().getString(R.string.add_as_friend))) {
                    followNewFriend(user.getUid(), friendId);
                } else {
                    // TODO: fix
                    Log.i(TAG, "onClick: 123456 Something wrong...");
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
                        friendId = userSnapshot.getKey();
                        friend = userSnapshot.getValue(User.class);
                        search_btn.setText(R.string.add_as_friend);
                        replaceFragment(SearchFriendResultFragment.newInstance());
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
                            Toast.makeText(getApplicationContext(),
                                    "Already following the user",
                                    Toast.LENGTH_SHORT).show();
                            replaceFragment(SearchFriendFragment.newInstance());
                            search_btn.setText(R.string.search_email);

                        } else {
                            FirebaseRealtimeDatabaseHelper.followFriend(currentUserId, friendId);
                            SuccessfullyAddedDialog.newInstance()
                                    .show(getSupportFragmentManager(), DIALOG_TAG);
                            // TODO: launch MainActivity
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
//        Slide slide = new Slide();
//        slide.setSlideEdge(Gravity.BOTTOM);
//        set.addTransition(slide);
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


    @Override
    public void showFriend(ImageView iconHolder, TextView nameHolder) {
        if (friend == null) return;
        // TODO: storage url doesn't show the image
        if (friend.getAvatar() != null) Picasso.get().load(friend.getAvatar()).into(iconHolder);
        nameHolder.setText(friend.getName());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        search_btn.setText(R.string.search_email);
    }
}
