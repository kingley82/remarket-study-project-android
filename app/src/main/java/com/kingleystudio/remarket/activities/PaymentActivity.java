package com.kingleystudio.remarket.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kingleystudio.remarket.Config;
import com.kingleystudio.remarket.R;
import com.kingleystudio.remarket.models.Errors;
import com.kingleystudio.remarket.models.Response;
import com.kingleystudio.remarket.models.di.Ad;
import com.kingleystudio.remarket.models.dto.AdPay;
import com.kingleystudio.remarket.models.dto.GetAd;
import com.kingleystudio.remarket.net.PayloadWrapper;
import com.kingleystudio.remarket.net.SocketHelper;
import com.kingleystudio.remarket.models.Types;
import com.kingleystudio.remarket.utils.AlertUtils;
import com.kingleystudio.remarket.utils.JsonUtils;

import org.json.JSONException;

public class PaymentActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private TextView adPrice, adTitle, paymentStatus;
    private Button payBtn, cancelBtn;
    private Ad currentAd = null;
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_payment);

        adTitle = findViewById(R.id.paymentAdTitle);
        adPrice = findViewById(R.id.paymentAdPrice);
        paymentStatus = findViewById(R.id.paymentStatusText);
        payBtn = findViewById(R.id.paymentPayBtn);
        cancelBtn = findViewById(R.id.paymentCancelBtn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentStatus.setText("Оплачиваем");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    socketHelper.send(new PayloadWrapper(new AdPay(Config.adIdToShow)));

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }, 1000);
                    }
                });


            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
        try {
            socketHelper.send(new PayloadWrapper(new GetAd(Config.adIdToShow)));
        } catch (JSONException e) {
            throw new RuntimeException(e);
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
                    case Types.PAY:
                        paymentStatus.setText("Успешно");
                        payBtn.setEnabled(false);
                        cancelBtn.setEnabled(false);
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                newActivityNewTask(DashboardActivity.class);
                            }
                        }, 2000);

                        break;
                    case Types.GET_AD:
                        currentAd = JsonUtils.convertJsonNodeToObject(response.getPayload().get(Types.AD), Ad.class);
                        adTitle.setText(currentAd.getTitle());
                        adPrice.setText(String.valueOf(currentAd.getPrice())+"₽");
                        payBtn.setEnabled(true);
                        cancelBtn.setEnabled(true);
                        break;
                    case Types.ERROR:
                        if (response.getPayload().get(Types.ERROR).asInt() == Errors.ERROR_AD_UNACCESSIBLE) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(PaymentActivity.this);
                            alert.setMessage("Ошибка покупки - объявление недоступно");
                            alert.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    newActivityNewTask(DashboardActivity.class);
                                }
                            });
                            alert.show();
                        }
                        break;
                }
            }
        });

    }
}
