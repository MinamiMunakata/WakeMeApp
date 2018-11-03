package com.minami_m.project.android.wakemeapp.ChatRoom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.InputHandler;
import com.minami_m.project.android.wakemeapp.InputValidationHandler;
import com.minami_m.project.android.wakemeapp.Main.MainActivity;
import com.minami_m.project.android.wakemeapp.Model.Message;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.SignIn.SignInActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatRoomActivity
        extends AppCompatActivity
        implements ActivityChangeListener, View.OnClickListener, InputValidationHandler {
    public static final String TAG = "--ChatRoomActivity--";
    private List<Message> mMessageList;
    private MessageListAdapter adapter;
    private String chatRoomId;
    private String receiverIcon;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private ImageButton sendButton;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        chatRoomId = data.getString(MainActivity.CHAT_ROOM_ID);
        receiverIcon = data.getString(MainActivity.RECEIVER_ICON);
        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
        editText = findViewById(R.id.message_text_field);
        mMessageList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.recycler_message_list_view);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessageListAdapter(mMessageList, currentUser.getUid(), receiverIcon);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            launchActivity(SignInActivity.class);
        }
        FirebaseRealtimeDatabaseHelper.MESSAGES_REF.child(chatRoomId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessageList.clear();
                for (DataSnapshot messageSnapShot: dataSnapshot.getChildren()) {
                    Message message = messageSnapShot.getValue(Message.class);
                    if (!message.getSenderId().equals(currentUser.getUid())) {
                        message.setIsSeen(true);
                        FirebaseRealtimeDatabaseHelper.updateIfMessageHasSeen(chatRoomId, message);
                    }
                    Log.i(TAG, "onDataChange: " + message.toString());
                    mMessageList.add(message);
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
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: 123456789 Clicked!");
        if (isValidInput()) {
            Message message = new Message(
                    editText.getText().toString(), currentUser.getUid(), new Date().getTime());
            FirebaseRealtimeDatabaseHelper.sendNewMessage(chatRoomId, message);
            editText.setText("");
        } else {
            Log.i(TAG, "onClick: 123456789 Invalid input");
        }
    }

    // TODO: check validation
    @Override
    public boolean isValidInput() {
        return InputHandler.isValidFormName(editText);
    }
}
