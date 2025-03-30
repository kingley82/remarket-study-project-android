package com.kingleystudio.remarket.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;

import com.kingleystudio.remarket.Config;
import com.kingleystudio.remarket.R;
import com.kingleystudio.remarket.models.Errors;
import com.kingleystudio.remarket.net.PayloadWrapper;
import com.kingleystudio.remarket.models.Response;
import com.kingleystudio.remarket.models.di.User;
import com.kingleystudio.remarket.models.dto.AccountChangeName;
import com.kingleystudio.remarket.models.dto.AccountChangePassword;
import com.kingleystudio.remarket.models.dto.AccountLogOut;
import com.kingleystudio.remarket.net.SocketHelper;
import com.kingleystudio.remarket.models.Types;
import com.kingleystudio.remarket.utils.AlertUtils;
import com.kingleystudio.remarket.utils.JsonUtils;
import com.kingleystudio.remarket.utils.SHA256;

import org.json.JSONException;

import java.security.NoSuchAlgorithmException;

public class SettingsActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_settings);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        findViewById(R.id.SettingsBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socketHelper.send(new PayloadWrapper(new AccountLogOut()));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        findViewById(R.id.btnChangeName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
                final EditText editText = new EditText(SettingsActivity.this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setHint("Введите новое имя");
                alert.setView(editText);

                alert.setPositiveButton("Сменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newname = editText.getText().toString().trim();
                        if (!newname.isEmpty()) {
                            try {
                                socketHelper.send(new PayloadWrapper(new AccountChangeName(newname)));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
        findViewById(R.id.btnChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
                final EditText editOldPass = new EditText(SettingsActivity.this);
                editOldPass.setInputType(InputType.TYPE_CLASS_TEXT);
                editOldPass.setHint("Введите старый пароль");
                final EditText editNewPass = new EditText(SettingsActivity.this);
                editNewPass.setInputType(InputType.TYPE_CLASS_TEXT);
                editNewPass.setHint("Введите новый пароль");
                final LinearLayout layout = new LinearLayout(SettingsActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(editOldPass);
                layout.addView(editNewPass);
                alert.setView(layout);

                alert.setPositiveButton("Сменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String oldpass = SHA256.hash(String.format("%s:%s", editOldPass.getText().toString().trim(), editOldPass.getText().toString()));
                            String newpass = SHA256.hash(String.format("%s:%s", editNewPass.getText().toString().trim(), editNewPass.getText().toString()));
                            socketHelper.send(new PayloadWrapper(new AccountChangePassword(oldpass, newpass)));

                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
        findViewById(R.id.btnAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertUtils.OkAlert(SettingsActivity.this, "О проекте", "ReMarket v1.0\nАвтор: Лев Терентьев\n2025", "Закрыть", null);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        this.socketHelper.subscribe(this);
    }

    @Override
    public void onStop() {
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
                SharedPreferences sPrefs = getSharedPreferences("account_data", 0);
                SharedPreferences.Editor edit = sPrefs.edit();
                switch (event) {
                    case Types.ERROR:
                        if (response.getPayload().get(Types.ERROR).asInt() == Errors.ERROR_INVALID_REQUEST) {
                            AlertUtils.OkAlert(SettingsActivity.this, "Ошибка!");
                        } else if (response.getPayload().get(Types.ERROR).asInt() == Errors.ERROR_USERNAME_TAKEN) {
                            AlertUtils.OkAlert(SettingsActivity.this, "Вы не можете поменять имя на введенное.");
                        } else if (response.getPayload().get(Types.ERROR).asInt() == Errors.ERROR_INVALID_PASSWORD) {
                            AlertUtils.OkAlert(SettingsActivity.this, "Неправильный старый пароль.");
                        }
                        break;
                    case Types.CHANGE_NAME:
                        Config.currentUser = JsonUtils.convertJsonNodeToObject(response.getPayload().get(Types.USER), User.class);
                        if (Config.currentUser.getId() == Config.userToProfile.getId()) {
                            Config.userToProfile = Config.currentUser;
                        }
                        edit.putString("username", Config.currentUser.getUsername());
                        edit.apply();
                        AlertUtils.OkAlert(SettingsActivity.this, "Успешно.");
                        break;
                    case Types.CHANGE_PASSWORD:
                        edit.putString("password", response.getPayload().get(Types.PASSWORD).asText());
                        edit.apply();
                        AlertUtils.OkAlert(SettingsActivity.this, "Успешно.");
                        break;
                }
            }
        });
    }
}