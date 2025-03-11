package com.kingleystudio.shopnchat.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kingleystudio.shopnchat.models.Types;

import androidx.activity.OnBackPressedCallback;

import com.kingleystudio.shopnchat.Config;
import com.kingleystudio.shopnchat.R;
import com.kingleystudio.shopnchat.models.PayloadWrapper;
import com.kingleystudio.shopnchat.models.Response;
import com.kingleystudio.shopnchat.models.di.Ad;
import com.kingleystudio.shopnchat.models.dto.AdStatusChange;
import com.kingleystudio.shopnchat.models.dto.GetOrStartDialogByMembers;
import com.kingleystudio.shopnchat.models.dto.GetAd;
import com.kingleystudio.shopnchat.net.SocketHelper;
import com.kingleystudio.shopnchat.utils.Base64Utils;
import com.kingleystudio.shopnchat.utils.JsonUtils;
import com.kingleystudio.shopnchat.utils.Logs;
import com.kingleystudio.shopnchat.utils.NumberUtils;
import com.kingleystudio.shopnchat.utils.StringUtils;

import org.json.JSONException;

import java.util.Objects;

public class AdActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private Ad currentAd = null;

    private LinearLayout adPhotosLayout;
    private TextView adTitle, adTitle2;
    private TextView adPrice, adPhone, adDescription, adSeller;
    private Button chatWithSellerBtn;
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_ad);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        findViewById(R.id.ProfileBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adPhotosLayout = findViewById(R.id.adPhotosLayout);
        adTitle = findViewById(R.id.titleDialog);
        adTitle2 = findViewById(R.id.adTitle2);
        adPrice = findViewById(R.id.adPrice);
        adPhone = findViewById(R.id.adPhone);
        adDescription = findViewById(R.id.adDesc);
        adSeller = findViewById(R.id.adSeller);
        chatWithSellerBtn = findViewById(R.id.chatWithSellerBtn);
    }

    @Override
    public void onStart() {
        super.onStart();

        socketHelper.subscribe(this);
        if (currentAd == null || currentAd.getId() != Config.adIdToShow) {
            try {
                socketHelper.send(new PayloadWrapper(new GetAd(Config.adIdToShow)));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        socketHelper.unsubscribe(this);
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(Response response) {
        String event = response.getTypeEvent();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (event) {
                    case Types.GET_AD:
                        currentAd = JsonUtils.convertJsonNodeToObject(response.getPayload().get(Types.AD), Ad.class);
                        adTitle.setText(currentAd.getTitle());
                        adTitle2.setText(currentAd.getTitle());
                        adPrice.setText(String.valueOf(currentAd.getPrice()));
                        adPhone.setText(StringUtils.beautifyPhone(currentAd.getPhone()));
                        adDescription.setText(currentAd.getDescription());
                        Logs.i("DESC "+currentAd.getDescription());
                        adSeller.setText("Продавец: " + currentAd.getSeller().getUsername());
                        for (String image_str : currentAd.getImages()) {
                            ImageView image = new ImageView(AdActivity.this);
                            image.setLayoutParams(new LinearLayout.LayoutParams(NumberUtils.spToPx(300, AdActivity.this), NumberUtils.spToPx(300, AdActivity.this)));
                            Bitmap bitmap = Base64Utils.base64ToBitmap(image_str);
                            image.setImageBitmap(bitmap);
                            adPhotosLayout.addView(image);
                        }
                        if (currentAd.getSeller().getId() == Config.currentUser.getId()) {
                            chatWithSellerBtn.setEnabled(true);
                            if (Objects.equals(currentAd.getStatus(), "active")) {
                                chatWithSellerBtn.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                                chatWithSellerBtn.setText("Снять с продажи");
                            } else {
                                chatWithSellerBtn.setBackgroundTintList(getResources().getColorStateList(R.color.green));
                                chatWithSellerBtn.setText("Вернуть на продажу");
                            }
                            chatWithSellerBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        socketHelper.send(new PayloadWrapper(new AdStatusChange(currentAd.getId())));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        } else {
                            chatWithSellerBtn.setEnabled(true);
                            chatWithSellerBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        socketHelper.send(new PayloadWrapper(new GetOrStartDialogByMembers(Config.currentUser.getId(), currentAd.getSeller().getId())));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }
                        break;
                    case Types.GET_DIALOG:
                        Config.dialogToShow = response.getPayload().get(Types.ID).asInt();
                        newActivity(DialogActivity.class);
                        break;
                    case Types.AD_STATUS_CHANGE:
                        currentAd.setStatus(response.getPayload().get(Types.STATUS).asText());
                        if (Objects.equals(currentAd.getStatus(), "active")) {
                            chatWithSellerBtn.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                            chatWithSellerBtn.setText("Снять с продажи");
                        } else {
                            chatWithSellerBtn.setBackgroundTintList(getResources().getColorStateList(R.color.green));
                            chatWithSellerBtn.setText("Вернуть на продажу");
                        }
                }
            }
        });

    }
}
