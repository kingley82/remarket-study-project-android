package com.kingleystudio.remarket.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingleystudio.remarket.Config;
import com.kingleystudio.remarket.R;
import com.kingleystudio.remarket.models.di.Message;
import com.kingleystudio.remarket.utils.TimeUtils;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;
    private final LayoutInflater inflater;
    private boolean isOurMessage;

    public MessageAdapter(Context ctx, List<Message> states) {
        this.inflater = LayoutInflater.from(ctx);
        this.messages = states;
    }

    public void setData(List<Message> data) {
        messages = data;
    }

    @Override
    public int getItemViewType(int pos) {
        Message message = messages.get(pos);
        if (message.getSender().getId() == Config.currentUser.getId())
            return 1;
        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = inflater.inflate(R.layout.item_sended_message, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_received_message, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.messageText.setText(messages.get(position).getMessage());
        holder.messageTime.setText(TimeUtils.TimestampToString(messages.get(position).getTime()));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText, messageTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            messageTime = itemView.findViewById(R.id.messageTime);
        }
    }
}
