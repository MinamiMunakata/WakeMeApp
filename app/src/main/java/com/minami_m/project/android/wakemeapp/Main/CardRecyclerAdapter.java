package com.minami_m.project.android.wakemeapp.Main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minami_m.project.android.wakemeapp.Model.ChatRoomCard;
import com.minami_m.project.android.wakemeapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> {
    private List<ChatRoomCard> chatRoomCards;

    public CardRecyclerAdapter(List<ChatRoomCard> chatRoomCards) {
        this.chatRoomCards = chatRoomCards;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ChatRoomCard roomCard = chatRoomCards.get(position);
        viewHolder.nameView.setText(roomCard.getReceiverName());
        viewHolder.statusView.setText(roomCard.getReceiverStatus());
        Picasso.get().load(roomCard.getReceiverIcon()).into(viewHolder.iconView);
        if (roomCard.isReceiverSleeping()) {
            viewHolder.alertView.setImageResource(R.drawable.ico_alart);
        } else {
            viewHolder.alertView.setImageResource(R.drawable.ico_awake);
        }

    }

    @Override
    public int getItemCount() {
        return chatRoomCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconView, alertView;
        public TextView nameView, statusView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.card_icon);
            alertView = itemView.findViewById(R.id.card_alart);
            nameView = itemView.findViewById(R.id.card_name);
            statusView = itemView.findViewById(R.id.card_status);
        }
    }
}
