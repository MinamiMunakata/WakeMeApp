package com.minami_m.project.android.wakemeapp.Screen.ChatRoom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.Common.Listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.Common.Helper.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.Common.Handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.Common.Handler.InputHandler;
import com.minami_m.project.android.wakemeapp.Common.Handler.InputValidationHandler;
import com.minami_m.project.android.wakemeapp.Model.ChatRoomCard;
import com.minami_m.project.android.wakemeapp.Screen.Main.MainActivity;
import com.minami_m.project.android.wakemeapp.Model.Message;
import com.minami_m.project.android.wakemeapp.Model.User;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Screen.MyPage.MyPageActivity;
import com.minami_m.project.android.wakemeapp.Screen.SignIn.SignInActivity;

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
    private String receiverName;
    private String receiverId;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private ImageButton sendButton;
    private EditText editText;
    private TextView title;
    private TextView subtitle;
    private ChatRoomCard chatRoomCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getDataFromMainActivity();
        title = findViewById(R.id.toolbar_title_chat_room);
        FontStyleHandler.setFont(this, title, true, true);
        subtitle = findViewById(R.id.toolbar_subtitle_chat_room);
        FontStyleHandler.setFont(this, subtitle, false, false);
        sendButton = findViewById(R.id.send_button);
        editText = findViewById(R.id.message_text_field);
        FontStyleHandler.setFont(this, editText, false, false);
        mMessageList = new ArrayList<>();
        sendButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        setupToolbar();
        setupRecyclerViewWithAdapter();

    }

    private void setupToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar_chat_room);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        title.setText(receiverName);
        FirebaseRealtimeDatabaseHelper.USERS_REF.child(receiverId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: 1234567 id = " + receiverId);
                Log.i(TAG, "onDataChange: 1234567" + dataSnapshot.getKey());
                String status = dataSnapshot.child("status").getValue(String.class);
                Log.i(TAG, "onDataChange: 1234567 name: " + dataSnapshot.getValue(User.class));
                Log.i(TAG, "onDataChange: 1234567 status: " + status);
                subtitle.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupRecyclerViewWithAdapter() {
        recyclerView = findViewById(R.id.recycler_message_list_view);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        if (currentUser == null) {
            launchActivity(SignInActivity.class);
            finish();
        }

        String currentUserId = null;
        try {
            currentUserId = currentUser.getUid();
        } catch (Exception e) {
            Log.i(TAG, "setupRecyclerViewWithAdapter: " + e.getMessage());
            launchActivity(SignInActivity.class);
        }
        adapter = new MessageListAdapter(mMessageList, currentUserId, receiverIcon);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = adapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (messageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }

            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getDataFromMainActivity() {
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        if (data != null) {
            chatRoomCard = data.getParcelable("ChatRoomCard");
            chatRoomId = chatRoomCard.getChatRoomId();
            receiverName = chatRoomCard.getReceiverName();
            receiverIcon = chatRoomCard.getReceiverIcon();
            receiverId = chatRoomCard.getReceiverId();
        }

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
                    if (!message.getSenderId().equals(currentUser.getUid()) && !message.getIsSeen()) {
                        message.setIsSeen(true);
                        FirebaseRealtimeDatabaseHelper.updateStatusThatMessageHasSeen(chatRoomId, message);
                    }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right);
    }

    @Override
    public void launchActivity(Class nextActivity) {
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
//        InputHandler.hideSoftKeyBoard(this);
        if (isValidInput()) {
            Message message = new Message(
                    (editText.getText().toString() + Html.fromHtml("&#160;&#160;&#160;&#160;&#160;&#160;&#160;")), currentUser.getUid(), new Date().getTime());
            mMessageList.add(message);
            adapter.notifyItemInserted(mMessageList.size() - 1);
            FirebaseRealtimeDatabaseHelper.sendNewMessage(chatRoomId, message);
            adapter.notifyDataSetChanged();
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
