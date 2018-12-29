package com.minami_m.project.android.wakemeapp.Screen.Main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minami_m.project.android.wakemeapp.Model.ChatRoomCard;
import com.minami_m.project.android.wakemeapp.Common.Listener.ChatRoomCardClickListener;
import com.minami_m.project.android.wakemeapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> {
    private List<ChatRoomCard> chatRoomCards;
    private static ChatRoomCardClickListener listener;

    public CardRecyclerAdapter(List<ChatRoomCard> chatRoomCards, ChatRoomCardClickListener listener) {
        this.chatRoomCards = chatRoomCards;
        this.listener = listener;
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
        if (roomCard.getReceiverIcon() == null) {
            viewHolder.iconView.setImageResource(R.drawable.test_default);
        } else {
            Picasso.get()
                    .load(roomCard.getReceiverIcon())
                    .error(R.drawable.ico_awake) // TODO: set default image
                    .into(viewHolder.iconView);
        }

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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView iconView, alertView;
        public TextView nameView, statusView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.card_icon);
            alertView = itemView.findViewById(R.id.card_alart);
            nameView = itemView.findViewById(R.id.card_name);
            statusView = itemView.findViewById(R.id.card_status);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            listener.onChatRoomCardClicked(v, this.getLayoutPosition());
        }
    }
}
