package com.minami_m.project.android.wakemeapp.ChatRoom;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minami_m.project.android.wakemeapp.FormattedDateGenerator;
import com.minami_m.project.android.wakemeapp.Model.Message;
import com.minami_m.project.android.wakemeapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "--Adapter--123456";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<Message> mMessageList;
    private String currentUserId;
    private String receiverIcon;

    public MessageListAdapter(List<Message> messageList, String receiverId, String receiverIcon) {
        this.mMessageList = messageList;
        this.currentUserId = receiverId;
        this.receiverIcon = receiverIcon;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.sent_message,parent,false);
            return new SentMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.received_message, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
        Log.i(TAG, "onCreateViewHolder: 123456789 Invalid ViewType");
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageViewHolder)holder)
                        .bind(message, mMessageList.size() == position + 1);
                Log.i(TAG, "onBindViewHolder: position " + position);
                Log.i(TAG, "onBindViewHolder: is Last? " + (mMessageList.size() == position + 1));
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageViewHolder)holder).bind(message, receiverIcon);
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView textView, seenHolder;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.sent_message_text);
            seenHolder = itemView.findViewById(R.id.sent_message_seen);

        }

        public void bind(Message message, boolean isLast) {
            textView.setText(message.getText());
            Log.i(TAG, "bind: " + message.getText());
            Log.i(TAG, "bind: " + isLast);
            Log.i(TAG, "bind: is Seen " + message.getIsSeen());
            if (isLast && message.getIsSeen()) {
                // TODO: fix
                seenHolder.setText("Seen");
            } else {
                seenHolder.setText(FormattedDateGenerator.generateTimestamp(message.getCreatedAt()));
            }
        }
    }

    private class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private CircleImageView imageView;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.received_bubble);
            imageView = itemView.findViewById(R.id.receiver_bubble_icon);
        }

        public void bind(Message message, String receiverIcon) {
            textView.setText(message.getText());
            Picasso.get().load(receiverIcon).into(imageView);
            Log.i(TAG, "bind: " + message.getText());
            Log.i(TAG, "bind: is Seen " + message.getIsSeen());
        }
    }
}
