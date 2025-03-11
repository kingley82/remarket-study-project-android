package com.kingleystudio.shopnchat.activities;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kingleystudio.shopnchat.Config;
import com.kingleystudio.shopnchat.R;
import com.kingleystudio.shopnchat.activities.adapters.AdAdapter;
import com.kingleystudio.shopnchat.models.PayloadWrapper;
import com.kingleystudio.shopnchat.models.Response;
import com.kingleystudio.shopnchat.models.di.Ad;
import com.kingleystudio.shopnchat.models.dto.GetAds;
import com.kingleystudio.shopnchat.net.SocketHelper;
import com.kingleystudio.shopnchat.utils.JsonUtils;
import com.kingleystudio.shopnchat.utils.Logs;
import com.kingleystudio.shopnchat.models.Types;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    private RecyclerView recyclerView;
    private AdAdapter adAdapter;
    private List<Ad> ads = new ArrayList<Ad>();

    private int offset = 0;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.adsList);
        adAdapter = new AdAdapter(this, ads);
        recyclerView.setAdapter(adAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.profileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.userToProfile = Config.currentUser;
                newActivity(ProfileActivity.class);
            }
        });
        findViewById(R.id.messagesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivity(DialogsActivity.class);
            }
        });
        findViewById(R.id.newAdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivity(NewAdActivity.class);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.socketHelper.subscribe(this);
        try {
            this.socketHelper.send(new PayloadWrapper(new GetAds(-1, 100, offset, true)));
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
        Logs.i("DashboardActivity onConnected");
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
                }
            }
        });

    }
}


