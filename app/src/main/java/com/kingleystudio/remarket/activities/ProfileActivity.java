package com.kingleystudio.remarket.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kingleystudio.remarket.Config;
import com.kingleystudio.remarket.R;
import com.kingleystudio.remarket.activities.adapters.AdAdapter;
import com.kingleystudio.remarket.net.PayloadWrapper;
import com.kingleystudio.remarket.models.Response;
import com.kingleystudio.remarket.models.di.Ad;
import com.kingleystudio.remarket.models.di.User;
import com.kingleystudio.remarket.models.dto.GetAds;
import com.kingleystudio.remarket.net.SocketHelper;
import com.kingleystudio.remarket.utils.JsonUtils;
import com.kingleystudio.remarket.utils.Logs;
import com.kingleystudio.remarket.utils.NumberUtils;
import com.kingleystudio.remarket.models.Types;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private User user = Config.userToProfile;

    private TextView usernameLabel;
    private TextView profitLabel;
    private RecyclerView recyclerView;
    private AdAdapter adAdapter;
    private List<Ad> ads = new ArrayList<Ad>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_profile);

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
        findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivity(SettingsActivity.class);
            }
        });
        usernameLabel = findViewById(R.id.titleDialog);
        profitLabel = findViewById(R.id.profileItemsProfitLabel);

        recyclerView = findViewById(R.id.profileAdsView);
        adAdapter = new AdAdapter(this, ads);
        recyclerView.setAdapter(adAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        usernameLabel.setText(user.getUsername());
    }

    private float calculateProfit(boolean isMoney) {
        float profit = 0;
        for (Ad ad: ads)
            if (ad.getStatus().equals("closed")) {
                if (isMoney)
                    profit += ad.getPrice();
                else
                    profit += 1;
            }
        return profit;
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.socketHelper.subscribe(this);
        try {
            this.socketHelper.send(new PayloadWrapper(new GetAds(Config.userToProfile.getId(), 1000, 0, Config.userToProfile.getId() != Config.currentUser.getId())));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.socketHelper.unsubscribe(this);
    }

    @Override
    public void onConnected() {
        Logs.i("ProfileActivity onConnected");
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
                    case Types.GET_ADS:
                        ads = JsonUtils.convertJsonNodeToList(response.getPayload().get(Types.ADS), Ad.class);
                        adAdapter.setData(ads);
                        adAdapter.notifyDataSetChanged();
                        if (user.getId() == Config.currentUser.getId()) {
                            profitLabel.setText(String.format("Продано товаров на сумму %s₽\nПродано %s товаров", NumberUtils.roundFloatAndCastToString(calculateProfit(true), 2),
                                    NumberUtils.roundFloatAndCastToString(calculateProfit(false), 2)));

                        } else {
                            profitLabel.setText(String.format("Продано %s товаров", NumberUtils.roundFloatAndCastToString(calculateProfit(false), 2)));
                        }
                        break;
                }
            }
        });

    }
}
