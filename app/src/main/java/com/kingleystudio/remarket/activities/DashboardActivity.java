package com.kingleystudio.remarket.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kingleystudio.remarket.Config;
import com.kingleystudio.remarket.R;
import com.kingleystudio.remarket.activities.adapters.AdAdapter;
import com.kingleystudio.remarket.models.PayloadWrapper;
import com.kingleystudio.remarket.models.Response;
import com.kingleystudio.remarket.models.di.Ad;
import com.kingleystudio.remarket.models.dto.GetAds;
import com.kingleystudio.remarket.models.dto.SearchAds;
import com.kingleystudio.remarket.net.SocketHelper;
import com.kingleystudio.remarket.utils.JsonUtils;
import com.kingleystudio.remarket.utils.Logs;
import com.kingleystudio.remarket.models.Types;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    private RecyclerView recyclerView;
    private AdAdapter adAdapter;
    private List<Ad> ads = new ArrayList<Ad>();

    private int offset = 0;

    private EditText searchEdit;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.adsList);
        adAdapter = new AdAdapter(this, ads);
        recyclerView.setAdapter(adAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchEdit = findViewById(R.id.searchEdit);

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
        findViewById(R.id.searchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socketHelper.send(new PayloadWrapper(new SearchAds(searchEdit.getText().toString(), 1000, offset)));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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
                    case Types.GET_ADS :
                        ads = JsonUtils.convertJsonNodeToList(response.getPayload().get(Types.ADS), Ad.class);
                        adAdapter.setData(ads);
                        adAdapter.notifyDataSetChanged();
                    case Types.SEARCH :
                        ads = JsonUtils.convertJsonNodeToList(response.getPayload().get(Types.ADS), Ad.class);
                        adAdapter.setData(ads);
                        adAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}


