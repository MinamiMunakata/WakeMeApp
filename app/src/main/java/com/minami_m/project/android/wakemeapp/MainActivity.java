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
    ImageButton button;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    List<String> chatRoomIDs;
    List<ChatRoomCard> chatRoomCards;
    RecyclerView recyclerView;
    CardRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        chatRoomIDs = new ArrayList<>();
        chatRoomCards = new ArrayList<>();
        button = findViewById(R.id.semicircle_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(SearchFriendActivity.class);
            }
        });
        User yukako = new User("123456", "Yukako", "yukako.@gmail.com");
        User nagisa = new User("789012", "Nagisa", "nagisa.@gmail.com");
        User natsumi = new User("345678", "Natsumi", "natsumi.@gmail.com");
        ChatRoomCard yukakoCard = new ChatRoomCard(yukako);
        ChatRoomCard nagisaCard = new ChatRoomCard(nagisa);
        ChatRoomCard natsumiCard = new ChatRoomCard(natsumi);
        chatRoomCards.add(yukakoCard);
        chatRoomCards.add(nagisaCard);
        chatRoomCards.add(natsumiCard);
        chatRoomCards.add(yukakoCard);
        chatRoomCards.add(nagisaCard);
        chatRoomCards.add(natsumiCard);
        chatRoomCards.add(yukakoCard);
        chatRoomCards.add(nagisaCard);
        chatRoomCards.add(natsumiCard);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        adapter = new CardRecyclerAdapter(chatRoomCards);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (chatRoomIDs.size() > 0) {
        }

    }

    @Override
    public void launchActivity(Class nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }
}
