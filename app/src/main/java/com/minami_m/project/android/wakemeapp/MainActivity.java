package com.minami_m.project.android.wakemeapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.Model.ChatRoomCard;
import com.minami_m.project.android.wakemeapp.Model.User;
import com.minami_m.project.android.wakemeapp.SearchFriend.SearchFriendActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityChangeListener{
    public static final String TAG = "---- MainActivity ----";
    private ImageButton button;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<String> receiverIdList;
    private List<ChatRoomCard> chatRoomCards;
    private RecyclerView recyclerView;
    private CardRecyclerAdapter adapter;
    private TextView currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserName = findViewById(R.id.current_user_name);
        currentUserName.setText(String.format("%s!",currentUser.getDisplayName()));
        receiverIdList = new ArrayList<>();
        chatRoomCards = new ArrayList<>();
        button = findViewById(R.id.semicircle_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(SearchFriendActivity.class);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        adapter = new CardRecyclerAdapter(chatRoomCards);
        recyclerView.setAdapter(adapter);

        FirebaseRealtimeDatabaseHelper.CHAT_ROOM_ID_LIST_REF.child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatRoomCards.clear();
                        for (DataSnapshot chatRoomIdSnapshot: dataSnapshot.getChildren()) {
                            User receiver = chatRoomIdSnapshot.getValue(User.class);
                            Log.i(TAG, "onDataChange: 123456789\n" + receiver);
                            ChatRoomCard roomCard = new ChatRoomCard(chatRoomIdSnapshot.getKey(), receiver);
                            chatRoomCards.add(roomCard);
                            adapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG, "onCancelled: " + databaseError.getMessage());
                    }
                });


    }

    @Override
    public void launchActivity(Class nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }
}
