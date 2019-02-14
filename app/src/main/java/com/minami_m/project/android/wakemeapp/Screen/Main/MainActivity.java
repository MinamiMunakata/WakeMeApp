package com.minami_m.project.android.wakemeapp.Screen.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuCompat;
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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.Common.Handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.Common.Helper.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.Common.Listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.Common.Listener.ChatRoomCardClickListener;
import com.minami_m.project.android.wakemeapp.Model.ChatRoomCard;
import com.minami_m.project.android.wakemeapp.Model.User;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Screen.ChatRoom.ChatRoomActivity;
import com.minami_m.project.android.wakemeapp.Screen.MyPage.MyPageActivity;
import com.minami_m.project.android.wakemeapp.Screen.SearchFriend.SearchFriendActivity;
import com.minami_m.project.android.wakemeapp.Screen.SignIn.SignInActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityChangeListener, ChatRoomCardClickListener {
    public static final String TAG = "---- MainActivity ----";
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<ChatRoomCard> chatRoomCards;
    private CardRecyclerAdapter adapter;
    private ImageView loadingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolBar();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        loadingImage = findViewById(R.id.loading_img);
        Glide.with(this).load(R.raw.loading).into(loadingImage);
        setupGreetingHeaderMsg();
        setupAddFriendsButton();
        setupRecyclerView();

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
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingImage.setVisibility(View.VISIBLE);
                chatRoomCards.clear();
                for (DataSnapshot chatRoomIdSnapshot : dataSnapshot.getChildren()) {
                    System.out.println(chatRoomIdSnapshot);
                    User receiver = chatRoomIdSnapshot.getValue(User.class);
                    System.out.println(receiver);
                    FirebaseRealtimeDatabaseHelper.updateStatusWithLoginTime(receiver.getId(), receiver.getLastLogin());
                    ChatRoomCard roomCard = new ChatRoomCard(chatRoomIdSnapshot.getKey(), receiver);
                    chatRoomCards.add(roomCard);
                }
                Log.i(TAG, "onDataChange: 123456789 Firebase: connected?");
                adapter.notifyDataSetChanged();
                loadingImage.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingImage.setVisibility(View.GONE);
                Log.i(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };

        try {
            FirebaseRealtimeDatabaseHelper.CHAT_ROOM_ID_LIST_REF.child(currentUser.getUid())
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
            currentUserName.setText(String.format("%s!", currentUser.getDisplayName()));
        } catch (Exception e) {
            launchActivity(SignInActivity.class);
        }
    }

    private void setupAddFriendsButton() {
        ImageView button = findViewById(R.id.semicircle_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(SearchFriendActivity.class);
            }
        });
    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.my_toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            launchActivity(SignInActivity.class);
        } else {
            FirebaseRealtimeDatabaseHelper.updateStatusWithLoginTime(currentUser.getUid(), new Date().getTime());
            Log.i(TAG, "onStart: " + currentUser.getUid());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
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
                FirebaseAuth.getInstance().signOut();
                launchActivity(SignInActivity.class);
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
