package com.kingleystudio.shopnchat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.kingleystudio.shopnchat.Config;
import com.kingleystudio.shopnchat.R;
import com.kingleystudio.shopnchat.models.Errors;
import com.kingleystudio.shopnchat.models.PayloadWrapper;
import com.kingleystudio.shopnchat.models.Response;
import com.kingleystudio.shopnchat.models.Types;
import com.kingleystudio.shopnchat.models.di.User;
import com.kingleystudio.shopnchat.models.dto.AccountSignIn;
import com.kingleystudio.shopnchat.models.dto.AccountSignUp;
import com.kingleystudio.shopnchat.net.SocketHelper;
import com.kingleystudio.shopnchat.utils.JsonUtils;
import com.kingleystudio.shopnchat.utils.Logs;
import com.kingleystudio.shopnchat.utils.SHA256;

import org.json.JSONException;

import java.security.NoSuchAlgorithmException;

public class LoginActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    private Button regBtn;
    private Button logBtn;
    private TextView statusText;
    private EditText nameEdit;
    private EditText passEdit;

    private String uname;
    private String pass;
    private String allowedPassChars = "1234567890-_qwertyuiopasdfghjklzxcvbnm";
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        regBtn = (Button)findViewById(R.id.regBtn);
        logBtn = (Button)findViewById(R.id.loginBtn);
        statusText = (TextView)findViewById(R.id.statusText);
        nameEdit = (EditText)findViewById(R.id.nameEdit);
        passEdit = (EditText)findViewById(R.id.passwordEdit);

        regBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uname = nameEdit.getText().toString().strip();
                pass = passEdit.getText().toString().strip();
                statusText.setVisibility(View.GONE);
                if (uname.isEmpty() || pass.isEmpty()) {
                    statusText.setVisibility(View.VISIBLE);
                    statusText.setText("Заполните все поля");
                    return;
                }
                if (uname.length() < 3) {
                    statusText.setVisibility(View.VISIBLE);
                    statusText.setText("Слишком короткое имя");
                    return;
                }
                for (char i : pass.toLowerCase().toCharArray()) {
                    if (!(allowedPassChars.contains((""+i)))) {
                        statusText.setVisibility(View.VISIBLE);
                        statusText.setText("Пароль содержит недопустимые символы: " + i);
                        return;
                    }
                }
                try {
                    pass = SHA256.hash(String.format("%s:%s", pass, pass));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                try {
                    socketHelper.send(new PayloadWrapper(new AccountSignUp(Config.getDeviceID(LoginActivity.this), uname, pass)));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uname = nameEdit.getText().toString().strip();
                pass = passEdit.getText().toString().strip();
                statusText.setVisibility(View.GONE);
                if (uname.isEmpty() || pass.isEmpty()) {
                    statusText.setVisibility(View.VISIBLE);
                    statusText.setText("Заполните все поля");
                    return;
                }
                try {
                    pass = SHA256.hash(String.format("%s:%s", pass, pass));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                try {
                    socketHelper.send(new PayloadWrapper(new AccountSignIn(Config.getDeviceID(LoginActivity.this), uname, pass)));
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
    }

    @Override
    public void onConnected() {
        Logs.i("LoginActivity onConnected");
    }

    @Override
    public void onStop() {
        super.onStop();
        this.socketHelper.unsubscribe(this);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(Response data) {
        //{"e": "err", "p": {"err": 1}}
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String event = data.getTypeEvent();
                switch (event) {
                    case Types.ERROR:
                        int code = data.getPayload().get(Types.ERROR).asInt();
                        Logs.i(code);
                        if (code == Errors.ERROR_USERNAME_TAKEN) {
                            statusText.setVisibility(View.VISIBLE);
                            statusText.setText("Данное имя уже занято");
                        } else if (code == Errors.ERROR_INVALID_PASSWORD) {
                            statusText.setVisibility(View.VISIBLE);
                            statusText.setText("Неверный пароль");
                        } else if (code == Errors.ERROR_USER_NOT_EXISTS) {
                            statusText.setVisibility(View.VISIBLE);
                            statusText.setText("Пользователь с данным именем не найден");
                        }
                        break;
                    case Types.ACCOUNT_SIGNUP:
                        try {
                            socketHelper.send(new PayloadWrapper(new AccountSignIn(Config.getDeviceID(LoginActivity.this), uname, pass)));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case Types.ACCOUNT_SIGNIN:
                        Config.currentUser = JsonUtils.convertJsonNodeToObject(data.getPayload().get(Types.USER), User.class);
                        SharedPreferences sPref = getSharedPreferences("account_data", 0);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("username", uname);
                        ed.putString("password", pass);
                        ed.apply();
                        newActivity(DashboardActivity.class);
                        break;
                }
            }
        });

    }
}