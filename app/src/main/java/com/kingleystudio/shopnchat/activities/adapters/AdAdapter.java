package com.kingleystudio.shopnchat.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingleystudio.shopnchat.Config;
import com.kingleystudio.shopnchat.R;
import com.kingleystudio.shopnchat.activities.AdActivity;
import com.kingleystudio.shopnchat.models.di.Ad;
import com.kingleystudio.shopnchat.utils.Base64Utils;
import com.kingleystudio.shopnchat.utils.Logs;
import com.kingleystudio.shopnchat.utils.NumberUtils;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private List<Ad> ads;

    public AdAdapter(Context ctx, List<Ad> states) {
        this.inflater = LayoutInflater.from(ctx);
        this.context = ctx;
        this.ads = states;
    }

    @NonNull
    @Override
    public AdAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_ad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ad ad = ads.get(position);
        holder.statusView.setVisibility(View.GONE);
        holder.titleView.setText(ad.getTitle());
        holder.priceView.setText(String.format("%s₽", NumberUtils.roundFloatAndCastToString(ad.getPrice(), 2)));
        if (ad.getStatus().equals("closed")) {
            holder.statusView.setVisibility(View.VISIBLE);
            Logs.i("CLOSED " + ad.getTitle());
        }
        holder.photoElement.setImageBitmap(Base64Utils.base64ToBitmap(ad.getImages().get(0)));
        holder.ad_id = ad.getId();
        holder.context = context;
    }

    public void setData(List<Ad> data) {
        this.ads = data;
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView titleView, priceView, statusView;
        public final ImageView photoElement;
        public int ad_id = -1;
        public Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.titleLabel);
            priceView = itemView.findViewById(R.id.priceLabel);
            statusView = itemView.findViewById(R.id.statusLabel);
            photoElement = itemView.findViewById(R.id.photoElement);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ad_id != -1) {
                        Config.adIdToShow = ad_id;
                        Intent intent = new Intent(context, AdActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
