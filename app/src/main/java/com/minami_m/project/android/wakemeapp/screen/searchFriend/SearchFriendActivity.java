package com.minami_m.project.android.wakemeapp.screen.searchFriend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.Fade;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.common.helper.FBRealTimeDBHelper;
import com.minami_m.project.android.wakemeapp.common.listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.common.listener.FragmentChangeListener;
import com.minami_m.project.android.wakemeapp.model.User;
import com.minami_m.project.android.wakemeapp.screen.main.MainActivity;
import com.minami_m.project.android.wakemeapp.screen.myPage.MyPageActivity;
import com.minami_m.project.android.wakemeapp.screen.signIn.SignInActivity;
import com.squareup.picasso.Picasso;

public class SearchFriendActivity extends AppCompatActivity
        implements FragmentChangeListener, SearchFriendFragmentListener, ActivityChangeListener {
    private static final String TAG = "SearchFriendActivity";
    private static final String DIALOG_TAG = "dialog";
    private Button search_btn;
    private EditText editEmail;
    private FirebaseUser currentUser;
    private User friend;
    private User mUser;
    private ImageView loadingImg;


    public void setEditEmail(EditText editEmail) {
        this.editEmail = editEmail;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        TextView toolbarTitle = findViewById(R.id.toolbar_title_add_friend);
        FontStyleHandler.setFont(this, toolbarTitle, true, true);
        setupToolBar();

        search_btn = findViewById(R.id.search_button);
        FontStyleHandler.setFont(this, search_btn, false, true);
        loadingImg = findViewById(R.id.loading_img_search_friends);
        Glide.with(this).load(R.raw.loading).into(loadingImg);
        loadingImg.setVisibility(View.INVISIBLE);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent intent = getIntent();
            Bundle data = intent.getExtras();
            if (data != null && data.getParcelable("User") != null) {
                mUser = data.getParcelable("User");
            } else {
                launchActivity(SignInActivity.class);
            }

        } else {
            launchActivity(SignInActivity.class);
        }

    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_search_friend);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search_btn.getText().equals(getResources().getString(R.string.search_email))) {
                    if (!TextUtils.isEmpty(editEmail.getText())) {
                        searchFriendByEmail(editEmail.getText().toString().trim());
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter email.", Toast.LENGTH_SHORT).show();
                    }
                } else if (search_btn.getText().equals(getResources().getString(R.string.add_as_friend))) {
                    followNewFriend(mUser, friend);
                } else {
                    Log.d(TAG, "onClick: Something wrong with a search button.");
                    launchActivity(SignInActivity.class);
                }

            }
        });
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
            Log.e(TAG, "searchFriendByEmail: ", e);
            launchActivity(SignInActivity.class);
        }
        if (email.equals(userEmail)) {
            Toast.makeText(this, "Enter friend's email address.", Toast.LENGTH_SHORT).show();
            loadingImg.setVisibility(View.INVISIBLE);
            return;
        }
        loadingImg.setVisibility(View.VISIBLE);
        ValueEventListener searchListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean dataExist = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String mEmail = userSnapshot.child("email").getValue(String.class);
                    if (mEmail != null && mEmail.equals(email)) {
                        friend = userSnapshot.getValue(User.class);
                        System.out.println(friend);
                        search_btn.setText(R.string.add_as_friend);
                        dataExist = true;
                        replaceFragment(SearchFriendResultFragment.newInstance());
                    }
                }
                if (!dataExist) {
                    Toast.makeText(getApplicationContext(), R.string.email_not_match, Toast.LENGTH_SHORT).show();
                }
                loadingImg.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
                loadingImg.setVisibility(View.INVISIBLE);
            }
        };
        FBRealTimeDBHelper.USERS_REF.addListenerForSingleValueEvent(searchListener);
    }

    public void followNewFriend(final User mUser, final User friend) {
        FBRealTimeDBHelper.FRIEND_ID_LIST_REF
                .child(mUser.getId())
                .child(friend.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(),
                                    "Search for new friend.",
                                    Toast.LENGTH_SHORT).show();
                            replaceFragment(SearchFriendFragment.newInstance());
                            search_btn.setText(R.string.search_email);

                        } else {
                            FBRealTimeDBHelper.followFriend(mUser, friend);
                            SuccessfullyAddedDialog.newInstance()
                                    .show(getSupportFragmentManager(), DIALOG_TAG);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Log.e(TAG, "onCancelled: ", databaseError.toException());
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
    public void showFriend(ImageView iconHolder, TextView nameHolder) {
        if (friend == null) return;
        if (friend.getName() != null && friend.getName().length() > 0) {
            StringBuilder displayName = capitalizeFirstLetter();
            nameHolder.setText(displayName);
        } else {
            nameHolder.setText(friend.getName());
        }
        if (friend.getIcon() != null) {
            Picasso.get()
                    .load(friend.getIcon())
                    .error(R.drawable.ico_default_avator)
                    .into(iconHolder);
        } else {
            iconHolder.setImageResource(R.drawable.ico_default_avator);
        }
    }

    @NonNull
    private StringBuilder capitalizeFirstLetter() {
        // Capitalize the first letter of an user name.
        String[] fullName = friend.getName().split(" ");
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
        return displayName;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!search_btn.getText().equals(getResources().getString(R.string.search_email))) {
            search_btn.setText(R.string.search_email);
            friend = null;
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

    }


    @Override
    public void launchActivity(Class nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingImg.setVisibility(View.INVISIBLE);
    }
}
