package com.kingleystudio.remarket.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kingleystudio.remarket.Config;
import com.kingleystudio.remarket.R;
import com.kingleystudio.remarket.activities.adapters.DialogsAdapter;
import com.kingleystudio.remarket.models.Types;
import com.kingleystudio.remarket.models.PayloadWrapper;
import com.kingleystudio.remarket.models.Response;
import com.kingleystudio.remarket.models.di.Dialog;
import com.kingleystudio.remarket.models.dto.GetDialogs;
import com.kingleystudio.remarket.net.SocketHelper;
import com.kingleystudio.remarket.utils.JsonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DialogsActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    private RecyclerView dialogsView;
    private List<Dialog> dialogs = new ArrayList<>();
    private DialogsAdapter dialogsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);

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

        dialogsView = findViewById(R.id.dialogsView);
        dialogsAdapter = new DialogsAdapter(this, dialogs);
        dialogsView.setAdapter(dialogsAdapter);
        dialogsView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
        try {
            socketHelper.send(new PayloadWrapper(new GetDialogs(Config.getDeviceID(DialogsActivity.this))));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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
                    case Types.GET_DIALOGS:
                        dialogs = JsonUtils.convertJsonNodeToList(response.getPayload().get(Types.DIALOGS), Dialog.class);
                        dialogsAdapter.setData(dialogs);
                        dialogsAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });

    }
}
