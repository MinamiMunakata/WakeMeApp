package com.minami_m.project.android.wakemeapp.screen.chatRoom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.DateAndTimeFormatHandler;
import com.minami_m.project.android.wakemeapp.common.handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.common.handler.InputHandler;
import com.minami_m.project.android.wakemeapp.common.handler.InputValidationHandler;
import com.minami_m.project.android.wakemeapp.common.helper.FBRealTimeDBHelper;
import com.minami_m.project.android.wakemeapp.common.listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.model.ChatRoomCard;
import com.minami_m.project.android.wakemeapp.model.Message;
import com.minami_m.project.android.wakemeapp.screen.main.MainActivity;
import com.minami_m.project.android.wakemeapp.screen.myPage.MyPageActivity;
import com.minami_m.project.android.wakemeapp.screen.signIn.SignInActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatRoomActivity
        extends AppCompatActivity
        implements ActivityChangeListener, View.OnClickListener, InputValidationHandler {
    public static final String TAG = "--ChatRoomActivity--";
    private List<Message> mMessageList;
    private MessageListAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private EditText editText;
    private TextView title;
    private TextView subtitle;
    private ChatRoomCard chatRoomCard;
    private ValueEventListener listener;

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getDataFromMainActivity();
        title = findViewById(R.id.toolbar_title_chat_room);
        FontStyleHandler.setFont(this, title, true, true);
        subtitle = findViewById(R.id.toolbar_subtitle_chat_room);
        FontStyleHandler.setFont(this, subtitle, false, false);
        ImageButton sendButton = findViewById(R.id.send_button);
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // Capitalize the first letter of an user name.
        String rName = chatRoomCard.getReceiver().getName();
        if (rName != null && rName.length() > 0) {
            String[] fullName = rName.split(" ");
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
            title.setText(displayName);
        } else {
            title.setText(rName);
        }
        String status = DateAndTimeFormatHandler.generateStatus(chatRoomCard.getReceiver().getLastLogin());
        subtitle.setText(status);
    }

    private void setupRecyclerViewWithAdapter() {
        recyclerView = findViewById(R.id.recycler_message_list_view);
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
        adapter = new MessageListAdapter(mMessageList, currentUserId, chatRoomCard.getReceiver().getIcon());
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
        }
        if (chatRoomCard == null) {
            launchActivity(SignInActivity.class);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            launchActivity(SignInActivity.class);
        }
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessageList.clear();
                for (DataSnapshot messageSnapShot : dataSnapshot.getChildren()) {
                    Message message = messageSnapShot.getValue(Message.class);
                    if (message != null) {
                        if (!message.getSenderId().equals(currentUser.getUid()) && !message.getIsSeen()) {
                            message.setIsSeen(true);
                            FBRealTimeDBHelper.updateStatusThatMessageHasSeen(chatRoomCard.getChatRoomId(), message);
                        }
                        mMessageList.add(message);
                    }
                    adapter.notifyDataSetChanged();
                    updateReceiverStatus(message);
                    recyclerView.smoothScrollToPosition(mMessageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };

        FBRealTimeDBHelper.MESSAGES_REF.child(chatRoomCard.getChatRoomId())
                .addValueEventListener(listener);
        // TODO: Delete notification
        FBRealTimeDBHelper.deletNotification(currentUser.getUid(), chatRoomCard.getChatRoomId());
    }

    private void updateReceiverStatus(Message message) {
        Message lastMessage = mMessageList.get(mMessageList.size() - 1);
        if (lastMessage.getSenderId().equals(chatRoomCard.getReceiver().getId())
                && lastMessage.getCreatedAt() > chatRoomCard.getReceiver().getLastLogin()) {
            String status = DateAndTimeFormatHandler.generateStatus(message.getCreatedAt());
            subtitle.setText(status);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FBRealTimeDBHelper.MESSAGES_REF.child(chatRoomCard.getChatRoomId())
                .removeEventListener(listener);
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
        if (isValidInput()) {
            Message message = new Message(
                    editText.getText().toString()
                            + fromHtml("&#160;&#160;&#160;&#160;&#160;&#160;&#160;"),
                    currentUser.getUid(),
                    new Date().getTime()
            );
            mMessageList.add(message);
            adapter.notifyItemInserted(mMessageList.size() - 1);
            FBRealTimeDBHelper.sendNewMessage(chatRoomCard.getChatRoomId(), message, chatRoomCard.getReceiver().getId(), currentUser.getDisplayName());
            adapter.notifyDataSetChanged();
            editText.setText("");
        } else {
            Log.i(TAG, "onClick: Invalid input to send a message.");
        }
    }

    // TODO: check validation
    @Override
    public boolean isValidInput() {
        return InputHandler.isValidFormName(editText);
    }
}
