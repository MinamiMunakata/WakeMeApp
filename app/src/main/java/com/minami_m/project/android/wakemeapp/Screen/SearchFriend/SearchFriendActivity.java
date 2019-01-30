package com.minami_m.project.android.wakemeapp.Screen.SearchFriend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.transition.Fade;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.Common.Listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.Common.Helper.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.Common.Handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.Common.Listener.FragmentChangeListener;
import com.minami_m.project.android.wakemeapp.Common.Handler.InputHandler;
import com.minami_m.project.android.wakemeapp.Screen.Main.MainActivity;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Model.User;
import com.minami_m.project.android.wakemeapp.Common.Helper.RealtimeDatabaseCallback;
import com.minami_m.project.android.wakemeapp.Screen.MyPage.MyPageActivity;
import com.minami_m.project.android.wakemeapp.Screen.SignIn.SignInActivity;
import com.minami_m.project.android.wakemeapp.Common.Handler.InputValidationHandler;
import com.squareup.picasso.Picasso;

public class SearchFriendActivity extends AppCompatActivity
        implements FragmentChangeListener, InputValidationHandler,
        SearchFriendFragmentListener, ActivityChangeListener {
    private Button search_btn;
    private EditText editEmail;
    private static final String TAG = "SearchFriendActivity";
    private FirebaseUser currentUser;
    private String friendId;
    private User friend;
    private User mUser;
    private TextView toolbarTitle;
    private static final String DIALOG_TAG = "dialog";

    public void setEditEmail(EditText editEmail) {
        this.editEmail = editEmail;
    }

    // TODO: Implement BACK or CANCEL to input search field again.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        toolbarTitle = findViewById(R.id.toolbar_title_add_friend);
        FontStyleHandler.setFont(this, toolbarTitle, true, true);
        Toolbar toolbar = findViewById(R.id.toolbar_search_friend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        search_btn = findViewById(R.id.search_button);
        FontStyleHandler.setFont(this, search_btn, false, true);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseRealtimeDatabaseHelper.readUserData(currentUser.getUid(), new RealtimeDatabaseCallback() {
                @Override
                public void retrieveUserData(User user) {
                    mUser = user;
                    if (mUser == null) {
                        Log.i(TAG, "onCreate: 123456789 the user doesn't exist.");
                        launchActivity(SignInActivity.class);
                    }
                }
            });

        } else {
            launchActivity(SignInActivity.class);
        }

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
                        } else {
                            Log.i(TAG, "onClick: 123456789 Invalid input...");
                        }
                    }
                } else if (search_btn.getText()
                        .equals(getResources().getString(R.string.add_as_friend))) {
                    followNewFriend(mUser, friend);
                } else {
                    // TODO: fix
                    Log.i(TAG, "onClick: 123456 Something wrong...");
                }

            }
        });
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void searchFriendByEmail(final String email) {
        String userEmail = null;
        try {
            userEmail = currentUser.getEmail();
        } catch (Exception e) {
            Log.i(TAG, "searchFriendByEmail: " + e.getMessage());
            launchActivity(SignInActivity.class);
        }
        if (email.equals(userEmail)) {
            Toast.makeText(this, "Search friend's ID", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: progressbar
        ValueEventListener searchListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean dataExist = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.child("email").getValue().equals(email)) {
                        Log.i(TAG, "onDataChange: " + userSnapshot.getKey());
                        friendId = userSnapshot.getKey();
                        friend = userSnapshot.getValue(User.class);
                        search_btn.setText(R.string.add_as_friend);
                        dataExist = true;
                        replaceFragment(SearchFriendResultFragment.newInstance());
                    }
                }
                if (!dataExist) {
                    Log.i(TAG, "onDataChange: 123456789 no User?");
                    Toast.makeText(getApplicationContext(), R.string.email_not_match, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };
        Log.i(TAG, "searchFriendByEmail: 12345" + FirebaseRealtimeDatabaseHelper.FIREBASE_DATABASE);
        Log.i(TAG, "searchFriendByEmail: 12345" + FirebaseRealtimeDatabaseHelper.USERS_REF);
        FirebaseRealtimeDatabaseHelper.USERS_REF.addListenerForSingleValueEvent(searchListener);
    }

    // TODO
    public void followNewFriend(final User mUser, final User friend) {
        FirebaseRealtimeDatabaseHelper.FRIEND_ID_LIST_REF
                .child(mUser.getId())
                .child(friend.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(),
                                    "Already following the currentUser",
                                    Toast.LENGTH_SHORT).show();
                            replaceFragment(SearchFriendFragment.newInstance());
                            search_btn.setText(R.string.search_email);

                        } else {
                            FirebaseRealtimeDatabaseHelper.followFriend(mUser.getId(), friendId);
                            FirebaseRealtimeDatabaseHelper.createChatRoom(mUser, friend);
                            SuccessfullyAddedDialog.newInstance()
                                    .show(getSupportFragmentManager(), DIALOG_TAG);
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
        if (friend.getIcon() != null) {
            Log.i(TAG, "showFriend: 123456" + friend.getIcon());
            Picasso.get()
                    .load(friend.getIcon()) // TODO: set default image
                    .error(R.drawable.ico_awake)
                    .into(iconHolder);
            nameHolder.setText(friend.getName());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!search_btn.getText().equals(getResources().getString(R.string.search_email))) {
            search_btn.setText(R.string.search_email);
            friend = null;
        } else {
            overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
        }

    }


    @Override
    public void launchActivity(Class nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }
}
