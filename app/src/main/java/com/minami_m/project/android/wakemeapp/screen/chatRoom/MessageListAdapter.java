package com.minami_m.project.android.wakemeapp.screen.chatRoom;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.DateAndTimeFormatHandler;
import com.minami_m.project.android.wakemeapp.model.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "MessageListAdapter";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private final List<Message> mMessageList;
    private final String currentUserId;
    private final String receiverIcon;

    MessageListAdapter(List<Message> messageList, String receiverId, String receiverIcon) {
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
                    .inflate(R.layout.sent_message, parent, false);
            return new SentMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.received_message, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
        return new RecyclerView.ViewHolder(parent) {
            @Override
            public String toString() {
                return "Invalid login, please try again";
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        boolean isFirstChat = false;
        if (position == 0) {
            isFirstChat = true;
        } else {
            Message previousMessage = mMessageList.get(position - 1);
            if (!DateAndTimeFormatHandler.isSameDay(previousMessage.getCreatedAt(), message.getCreatedAt())) {
                isFirstChat = true;
            }
        }


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageViewHolder) holder)
                        .bind(message, mMessageList.size() == position + 1, isFirstChat);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageViewHolder) holder).bind(message, receiverIcon, isFirstChat);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private void setDate(TextView dateView, Message message, boolean isFirstChat) {
        if (isFirstChat) {
            dateView.setVisibility(View.VISIBLE);
            if (DateAndTimeFormatHandler.isToday(message.getCreatedAt())) {
                dateView.setText(R.string.today);
            } else if (DateAndTimeFormatHandler.isYesterday(message.getCreatedAt())) {
                dateView.setText(R.string.yesterday);
            } else {
                dateView.setText(DateAndTimeFormatHandler.generateDateOfChat(message.getCreatedAt()));
            }
        } else {
            dateView.setVisibility(View.GONE);
        }
    }

    private class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView dateView;
        private final TextView seenHolder;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.sender_date);
            textView = itemView.findViewById(R.id.sent_message_text);
            seenHolder = itemView.findViewById(R.id.sent_message_seen);

        }

        void bind(Message message, boolean isLast, boolean isFirstChat) {
            textView.setText(message.getText());
            setDate(dateView, message, isFirstChat);
            if (isLast && message.getIsSeen()) {
                seenHolder.setText(R.string.seen);
            } else {
                seenHolder.setText(DateAndTimeFormatHandler.generateTimestamp(message.getCreatedAt()));
            }
        }

    }

    private class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView dateView;
        private final CircleImageView imageView;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.receiver_date);
            textView = itemView.findViewById(R.id.received_bubble);
            imageView = itemView.findViewById(R.id.receiver_bubble_icon);
        }

        void bind(Message message, String receiverIcon, boolean isFirstChat) {
            setDate(dateView, message, isFirstChat);
            textView.setText(message.getText());
            if (receiverIcon != null) {
                Picasso.get()
                        .load(receiverIcon)
                        .error(R.drawable.ico_default_avatar)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.ico_default_avatar);
            }
        }
    }
}
