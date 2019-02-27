package com.minami_m.project.android.wakemeapp.screen.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.common.helper.FBRealTimeDBHelper;
import com.minami_m.project.android.wakemeapp.common.listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.common.listener.ChatRoomCardClickListener;
import com.minami_m.project.android.wakemeapp.common.service.MyFirebaseMessagingService;
import com.minami_m.project.android.wakemeapp.model.ChatRoomCard;
import com.minami_m.project.android.wakemeapp.model.User;
import com.minami_m.project.android.wakemeapp.screen.chatRoom.ChatRoomActivity;
import com.minami_m.project.android.wakemeapp.screen.myPage.MyPageActivity;
import com.minami_m.project.android.wakemeapp.screen.searchFriend.SearchFriendActivity;
import com.minami_m.project.android.wakemeapp.screen.signIn.SignInActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ActivityChangeListener, ChatRoomCardClickListener {
    public static final String TAG = "MainActivity";
    private FirebaseUser currentUser;
    private List<ChatRoomCard> chatRoomCards;
    private CardRecyclerAdapter adapter;
    private ImageView loadingImage;
    private ValueEventListener listener;
    private MyFirebaseMessagingService fcm;
    private ImageView button;
    private boolean isListening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolBar();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        loadingImage = findViewById(R.id.loading_img);
        Glide.with(this).load(R.raw.loading).into(loadingImage);
        setupGreetingHeaderMsg();

    }

    private void setupRecyclerView() {
        chatRoomCards = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CardRecyclerAdapter(chatRoomCards, this);
        recyclerView.setAdapter(adapter);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isListening) {
                    Log.i(TAG, "onDataChange: Access to Firebase.");
                    loadingImage.setVisibility(View.VISIBLE);
                    chatRoomCards.clear();
                    for (DataSnapshot chatRoomIdSnapshot : dataSnapshot.getChildren()) {
                        User receiver = chatRoomIdSnapshot.getValue(User.class);
                        if (receiver != null) {
                            ChatRoomCard roomCard = new ChatRoomCard(chatRoomIdSnapshot.getKey(), receiver);
                            chatRoomCards.add(roomCard);
                            Log.i(TAG, "onDataChange: " + roomCard.toString());
                        }
                    }
                    Collections.sort(chatRoomCards);

                    adapter.notifyDataSetChanged();
                    loadingImage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingImage.setVisibility(View.GONE);
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        };

        try {
            isListening = true;
            FBRealTimeDBHelper.CHAT_ROOM_ID_LIST_REF.child(currentUser.getUid())
                    .addValueEventListener(listener);
        } catch (Exception e) {
            launchActivity(SignInActivity.class);
        }
    }

    private void setupGreetingHeaderMsg() {
        TextView goodMorning = findViewById(R.id.good_morning);
        FontStyleHandler.setFont(this, goodMorning, false, false);
        TextView currentUserName = findViewById(R.id.current_user_name);
        FontStyleHandler.setFont(this, currentUserName, true, true);
        try {
            // Capitalize the first letter of an user name.
            if (currentUser.getDisplayName() != null && currentUser.getDisplayName().length() > 0) {
                StringBuilder displayName = getCapitalizedDisplayName();
                displayName.append("!");
                currentUserName.setText(displayName);
            } else {
                currentUserName.setText(String.format("%s!", currentUser.getDisplayName()));
            }

        } catch (Exception e) {
            launchActivity(SignInActivity.class);
        }
    }

    @NonNull
    private StringBuilder getCapitalizedDisplayName() {
        String[] fullName = Objects.requireNonNull(currentUser.getDisplayName()).split(" ");
        StringBuilder displayName = new StringBuilder();
        if (fullName[0].length() > 0) {
            displayName
                    .append(fullName[0].substring(0, 1).toUpperCase())
                    .append(fullName[0].substring(1));
        } else {
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
        }
        return displayName;
    }

    private void readUserAndSetupButton() {
        button = findViewById(R.id.semicircle_btn);
        if (currentUser != null) {
            isListening = true;
            FBRealTimeDBHelper.USERS_REF.child(currentUser.getUid())
                    .addListenerForSingleValueEvent(generateEventListener(button));
        } else {
            launchActivity(SignInActivity.class);
        }

    }

    @NonNull
    private ValueEventListener generateEventListener(final ImageView button) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isListening) {
                    final User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Update login time.
                        final Map<String, Object> childUpdates = new HashMap<>();
                        final long loginTime = new Date().getTime();
                        childUpdates.put("/Users/" + user.getId() + "/lastLogin", loginTime);
                        FBRealTimeDBHelper.updateLoginTime(currentUser.getUid(), new Date().getTime());
                        // Setup a button to add friends.
                        setupButton(user);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Sorry, sign in again.",
                                Toast.LENGTH_SHORT)
                                .show();

                        launchActivity(SignInActivity.class);
                    }
                }

            }

            private void setupButton(final User user) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), SearchFriendActivity.class);
                        intent.putExtra("User", user);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        };
    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.my_toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            launchActivity(SignInActivity.class);
        } else {
            // Subscribe to a topic
            if (fcm == null) {
                fcm = new MyFirebaseMessagingService(currentUser.getUid());
            }
            // Update a login time
            readUserAndSetupButton();
//            FBRealTimeDBHelper.USERS_REF.child(currentUser.getUid())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                FBRealTimeDBHelper.updateLoginTime(
//                                        currentUser.getUid(),
//                                        new Date().getTime());
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            Log.e(TAG, "onCancelled: ", databaseError.toException());
//                        }
//                    });

            setupRecyclerView();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isListening = false;
        removeListeners();
        loadingImage.setVisibility(View.INVISIBLE);
        Log.i(TAG, "onStop: " + "Disconnect from FB");
    }

    private void removeListeners() {
        FBRealTimeDBHelper.CHAT_ROOM_ID_LIST_REF.child(currentUser.getUid())
                .removeEventListener(listener);
        FBRealTimeDBHelper.USERS_REF.child(currentUser.getUid())
                .removeEventListener(generateEventListener(button));
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
                return true;
            case R.id.my_page_menu:
                launchActivity(MyPageActivity.class);
                return true;
            case R.id.logout_menu:
                isListening = false;
                removeListeners();
                launchActivity(SignInActivity.class);
                FirebaseAuth.getInstance().signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void launchActivity(Class nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }

    @Override
    public void onChatRoomCardClicked(View v, int position) {
        ChatRoomCard roomCard = chatRoomCards.get(position);
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("ChatRoomCard", roomCard);
        startActivity(intent);
    }
}
