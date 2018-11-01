package com.minami_m.project.android.wakemeapp.ChatRoom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.minami_m.project.android.wakemeapp.Model.Message;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<Message> mMessageList;

    public MessageListAdapter(List<Message> messageList) {
        this.mMessageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        // TODO
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    private class SentMessageViewHolder extends RecyclerView.ViewHolder {


        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);


        }
    }
}
