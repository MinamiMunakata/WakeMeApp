package com.minami_m.project.android.wakemeapp.screen.main;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.DateAndTimeFormatHandler;
import com.minami_m.project.android.wakemeapp.common.listener.ChatRoomCardClickListener;
import com.minami_m.project.android.wakemeapp.model.ChatRoomCard;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder> {
    private static ChatRoomCardClickListener listener;
    private List<ChatRoomCard> chatRoomCards;

    CardRecyclerAdapter(List<ChatRoomCard> chatRoomCards, ChatRoomCardClickListener listener) {
        this.chatRoomCards = chatRoomCards;
        CardRecyclerAdapter.listener = listener;
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
        // Capitalize the first letter of an user name.
        String rName = roomCard.getReceiver().getName();
        if (rName != null && rName.length() > 0) {
            String[] fullName = rName.split(" ");
            StringBuilder displayName = new StringBuilder();
            if (fullName[0].length() > 0) {
                displayName
                        .append(fullName[0].substring(0, 1).toUpperCase())
                        .append(fullName[0].substring(1));
            } else {
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
            }
            viewHolder.nameView.setText(displayName);
        } else {
            viewHolder.nameView.setText(rName);
        }
        viewHolder.statusView.setText(roomCard.getOversleepTimeStatus());
        if (roomCard.getReceiver().getIcon() == null) {
            viewHolder.iconView.setImageResource(R.drawable.ico_default_avator);
        } else {
            Picasso.get()
                    .load(roomCard.getReceiver().getIcon())
                    .error(R.drawable.ico_default_avator)
                    .into(viewHolder.iconView);
        }

        if (roomCard.getOversleepTime() > 0 &&
                DateAndTimeFormatHandler.isLessThan24h(roomCard.getOversleepTime())) {
            if (roomCard.getUnread()) {
                viewHolder.alertView.setColorFilter(Color.parseColor("#db8623"));
                viewHolder.alertView.setImageResource(R.drawable.ic_baseline_chat_24px);
            } else {
                viewHolder.alertView.clearColorFilter();
                viewHolder.alertView.setImageResource(R.drawable.ico_alert);
            }

            viewHolder.statusView.setTextColor(Color.RED);
        } else {
            if (roomCard.getUnread()) {
                viewHolder.alertView.setColorFilter(Color.parseColor("#db8623"));
                viewHolder.alertView.setImageResource(R.drawable.ic_baseline_chat_24px);
            } else {
                viewHolder.alertView.setImageResource(R.drawable.ico_awake);
                viewHolder.statusView.setTextColor(Color.parseColor("#db8623"));
            }

        }

    }

    @Override
    public int getItemCount() {
        return chatRoomCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iconView, alertView;
        TextView nameView, statusView;

        ViewHolder(@NonNull View itemView) {
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
