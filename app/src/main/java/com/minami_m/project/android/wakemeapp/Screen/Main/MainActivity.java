package com.minami_m.project.android.wakemeapp.Screen.Main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.minami_m.project.android.wakemeapp.Common.Listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.Common.Handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.Screen.ChatRoom.ChatRoomActivity;
import com.minami_m.project.android.wakemeapp.Common.Listener.ChatRoomCardClickListener;
import com.minami_m.project.android.wakemeapp.Common.Helper.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.Model.ChatRoomCard;
import com.minami_m.project.android.wakemeapp.Model.User;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Screen.SearchFriend.SearchFriendActivity;
import com.minami_m.project.android.wakemeapp.Screen.SettingActivity;
import com.minami_m.project.android.wakemeapp.Screen.SignIn.SignInActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityChangeListener, ChatRoomCardClickListener {
    public static final String TAG = "---- MainActivity ----";
    public static final String CHAT_ROOM_ID = "ChatRoomID";
    public static final String RECEIVER_ICON = "ReceiverIcon";
    public static final String RECEIVER_NAME = "ReceiverName";
    public static final String RECEIVER_ID = "ReceiverId";
    private ImageView button;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<ChatRoomCard> chatRoomCards;
    private RecyclerView recyclerView;
    private CardRecyclerAdapter adapter;
    private TextView goodMorning;
    private TextView currentUserName;
//    private ProgressBar progressBar;
    private  ImageView loadingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        goodMorning = findViewById(R.id.good_morning);
        FontStyleHandler.setFont(this, goodMorning, true, true);
        currentUserName = findViewById(R.id.current_user_name);
        FontStyleHandler.setFont(this, currentUserName, true, true);
        try {
            currentUserName.setText(String.format("%s!",currentUser.getDisplayName()));
        } catch (Exception e) {
            launchActivity(SignInActivity.class);
        }
        chatRoomCards = new ArrayList<>();
        button = findViewById(R.id.semicircle_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(SearchFriendActivity.class);
            }
        });
//        progressBar = findViewById(R.id.progressbar_main);
        loadingImage = findViewById(R.id.loading_img);
        Glide.with(this).load(R.raw.loading).into(loadingImage);
//        progressBar.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CardRecyclerAdapter(chatRoomCards, this);
        recyclerView.setAdapter(adapter);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                progressBar.setVisibility(View.VISIBLE);
                loadingImage.setVisibility(View.VISIBLE);
                chatRoomCards.clear();
                for (DataSnapshot chatRoomIdSnapshot: dataSnapshot.getChildren()) {
                    User receiver = chatRoomIdSnapshot.getValue(User.class);
                    FirebaseRealtimeDatabaseHelper.updateStatusWithLoginTime(receiver.getId(), receiver.getLastLogin());
                    ChatRoomCard roomCard = new ChatRoomCard(chatRoomIdSnapshot.getKey(), receiver);
                    chatRoomCards.add(roomCard);
                    adapter.notifyDataSetChanged();
                }
                Log.i(TAG, "onDataChange: 123456789 Firebase: connected?");
//                progressBar.setVisibility(View.GONE);
                loadingImage.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                progressBar.setVisibility(View.GONE);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            launchActivity(SignInActivity.class);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_menu:
                launchActivity(MainActivity.class);
                return true;
            case R.id.my_page_menu:
                launchActivity(SettingActivity.class);
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
        Bundle data = new Bundle();
        data.putString(CHAT_ROOM_ID, roomCard.getChatRoomId());
        data.putString(RECEIVER_ICON, roomCard.getReceiver().getIcon());
        data.putString(RECEIVER_NAME, roomCard.getReceiver().getName());
        data.putString(RECEIVER_ID, roomCard.getReceiver().getId());
        intent.putExtras(data);
        startActivity(intent);
//        finish();
    }
}
