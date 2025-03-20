package com.kingleystudio.remarket.activities;

import static android.widget.Toast.LENGTH_LONG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.kingleystudio.remarket.Config;
import com.kingleystudio.remarket.R;
import com.kingleystudio.remarket.models.Errors;
import com.kingleystudio.remarket.net.PayloadWrapper;
import com.kingleystudio.remarket.models.Response;
import com.kingleystudio.remarket.models.Types;
import com.kingleystudio.remarket.models.dto.AdPost;
import com.kingleystudio.remarket.net.SocketHelper;
import com.kingleystudio.remarket.utils.AlertUtils;
import com.kingleystudio.remarket.utils.Base64Utils;
import com.kingleystudio.remarket.utils.ImageUtils;
import com.kingleystudio.remarket.utils.JsonUtils;
import com.kingleystudio.remarket.utils.Logs;
import com.kingleystudio.remarket.utils.NumberUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewAdActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    private LinearLayout adPhotosScroll;
    private EditText adName;
    private EditText adPrice;
    private EditText adPhone;
    private EditText adDesc;
    private Button adPostBtn;
    private ImageButton newImageBtn;

    private List<Uri> attachedPhotos = new ArrayList<>();
    private PickVisualMediaRequest pickVisualMediaRequest;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_new_ad);

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

        adPhotosScroll = findViewById(R.id.adPhotoScroll);
        adName = findViewById(R.id.adNameEdit);
        adPrice = findViewById(R.id.adPriceEdit);
        adPhone = findViewById(R.id.adPhoneEdit);
        adDesc = findViewById(R.id.adDescEdit);
        adPostBtn = findViewById(R.id.postAdBtn);
        newImageBtn = findViewById(R.id.adAddPhotoBtn);

        pickVisualMediaRequest = new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build();

        newImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attachedPhotos.size() >= 3) {
                    Toast toast = Toast.makeText(NewAdActivity.this, "Выбрано макс. кол-во фото. Вы можете удалить фото нажатием по нему", LENGTH_LONG);
                    toast.show();
                    return;
                }

                pickMultipleMedia.launch(pickVisualMediaRequest);
            }
        });

        adPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Logs.i("sending");
                        String title = adName.getText().toString().trim();
                        String textPrice = adPrice.getText().toString().trim();
                        String phone = adPhone.getText().toString().trim();
                        String desc = adDesc.getText().toString().trim();
                        if (title.isEmpty() || title.length() < 10) {
                            alert("Название объявления слишком короткое");
                            return;
                        }
                        if (textPrice.isEmpty()) {
                            alert("Необходимо указать цену");
                            return;
                        }
                        float price = Float.parseFloat(textPrice);
                        if (price < 0f) {
                            alert("Цена не может быть отрицательной");
                            return;
                        }
                        if (desc.isEmpty()) {
                            alert("Заполните описание товара");
                            return;
                        }
                        if (
                                !phone.matches("(^8|7|\\+7)[23459](\\d{9})") &
                                        !phone.matches("(^375|\\+375)(\\s+)?\\(?(17|29|33|44)\\)?(\\s+)?[0-9]{3}[0-9]{2}[0-9]{2}$") &
                                        !phone.matches("(^8|7|\\+7)[067](\\d{9})")
                        ) {
                            alert("Указан неверный номер телефона. Возможен ввод только российских, белорусских и казахских номеров.");
                            return;
                        }
                        if (attachedPhotos.isEmpty()) {
                            alert("Вы должны прикрепить хотя бы одно фото");
                            return;
                        }
                        JSONArray images = new JSONArray();
                        for (Uri uri : attachedPhotos) {
                            try {
                                Bitmap image = ImageUtils.uriToBitmap(NewAdActivity.this, uri);
                                if (image.getWidth() > 1366) {
                                    image = ImageUtils.resizeBitmapByWidth(image);
                                } else if (image.getHeight() > 768) {
                                    image = ImageUtils.resizeBitmapByHeight(image);
                                }
                                images.put(Base64Utils.bitmapToBase64(image));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        try {
                            socketHelper.send(new PayloadWrapper(new AdPost(title, price, images, phone, desc, Config.currentUser.getId(), "active")));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();

            }
        });
    }

    private void alert(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertUtils.OkAlert(NewAdActivity.this, message);
            }
        });

    }

    public String getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toString();
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(3), uris -> {
                if (!uris.isEmpty()) {
                    for (Uri uri : uris) {
                        if (attachedPhotos.size() >= 3) {
                            break;
                        } else {
                            attachedPhotos.add(uri);
                            ImageButton button = new ImageButton(NewAdActivity.this);
                            button.setLayoutParams(new LinearLayout.LayoutParams(NumberUtils.spToPx(150f, NewAdActivity.this), NumberUtils.spToPx(150f, NewAdActivity.this)));
                            button.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            button.setImageURI(uri);
                            button.setBackgroundTintList(getResources().getColorStateList(R.color.transparent));
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    attachedPhotos.remove(uri);
                                                    button.setVisibility(View.GONE);
                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    //No button clicked
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(NewAdActivity.this);
                                    builder.setMessage("Удалить это фото?").setPositiveButton("Да", dialogClickListener)
                                            .setNegativeButton("Нет", dialogClickListener).show();
                                }
                            });

                            adPhotosScroll.addView(button);
                        }
                    }
                }
            });

    @Override
    public void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
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
        switch (event) {
            case Types.ERROR:
                int error = response.getPayload().get(Types.ERROR).asInt();
                if (error == Errors.ERROR_INVALID_REQUEST) {
                    AlertUtils.OkAlert(NewAdActivity.this, "Ошибка: некорректный запрос");
                }
                break;
            case Types.AD_POST:
                int id = response.getPayload().get(Types.ID).asInt();
                Config.adIdToShow = id;
                newActivity(AdActivity.class);
        }
    }
}
