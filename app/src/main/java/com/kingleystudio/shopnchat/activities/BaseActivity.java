package com.kingleystudio.shopnchat.activities;

import android.os.Bundle;

import com.kingleystudio.shopnchat.Config;
import com.kingleystudio.shopnchat.R;
import com.kingleystudio.shopnchat.models.PayloadWrapper;
import com.kingleystudio.shopnchat.models.Response;
import com.kingleystudio.shopnchat.models.di.User;
import com.kingleystudio.shopnchat.models.dto.AccountSignIn;
import com.kingleystudio.shopnchat.net.SocketHelper;
import com.kingleystudio.shopnchat.models.Types;
import com.kingleystudio.shopnchat.utils.JsonUtils;

import org.json.JSONException;

public class BaseActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Config.baseContext = BaseActivity.this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.socketHelper.subscribe(this);
        String username = getSharedPreferences("account_data", 0).getString("username", "");
        String password = getSharedPreferences("account_data", 0).getString("password", "");
        if (username.isEmpty() || password.isEmpty()) {
            newActivity(LoginActivity.class);
        } else {
            try {
                this.socketHelper.send(new PayloadWrapper(new AccountSignIn(Config.getDeviceID(this.getApplicationContext()), username, password)));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        this.socketHelper.unsubscribe(this);
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(Response response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String event = response.getTypeEvent();
                switch (event) {
                    case Types.ERROR:
                        newActivity(LoginActivity.class);
                        break;
                    case Types.ACCOUNT_SIGNIN:
                        Config.currentUser = JsonUtils.convertJsonNodeToObject(response.getPayload().get(Types.USER), User.class);
                        newActivity(DashboardActivity.class);
                        break;
                }
            }
        });
    }
}
