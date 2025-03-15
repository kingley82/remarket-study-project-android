package com.kingleystudio.remarket.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingleystudio.remarket.Config;
import com.kingleystudio.remarket.R;
import com.kingleystudio.remarket.activities.DialogActivity;
import com.kingleystudio.remarket.models.di.Dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DialogsAdapter extends RecyclerView.Adapter<DialogsAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private List<Dialog> dialogs = new ArrayList<>();
    private Context context;

    public DialogsAdapter(Context ctx, List<Dialog> states) {
        this.inflater = LayoutInflater.from(ctx);
        this.dialogs = states;
        this.context = ctx;
    }

    @NonNull
    @Override
    public DialogsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_dialog, parent, false);
        return new DialogsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogsAdapter.ViewHolder holder, int position) {
        Dialog dialog = dialogs.get(position);
        holder.memberUsername.setText(
                Objects.equals(Config.currentUser.getUsername(), dialog.getMember1().getUsername()) ? dialog.getMember2().getUsername() : dialog.getMember1().getUsername()
        );
        holder.lastMessage.setText(dialog.getLast_message());
        holder.iView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.dialogToShow = dialog;
                Intent intent = new Intent(context, DialogActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.dialogs.size();
    }

    public void setData(List<Dialog> data) {
        this.dialogs = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView memberUsername, lastMessage;
        public View iView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberUsername = itemView.findViewById(R.id.memberUsername);
            lastMessage = itemView.findViewById(R.id.recentMessage);
            iView = itemView;
        }
    }
}
