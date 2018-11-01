package com.minami_m.project.android.wakemeapp;

import android.view.View;

import com.minami_m.project.android.wakemeapp.Model.ChatRoomCard;

public interface ChatRoomCardClickListener {
    void onChatRoomCardClicked(View v, int position);
}
