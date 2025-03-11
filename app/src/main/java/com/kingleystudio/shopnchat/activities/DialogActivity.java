package com.kingleystudio.shopnchat.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kingleystudio.shopnchat.Config;
import com.kingleystudio.shopnchat.R;
import com.kingleystudio.shopnchat.activities.adapters.MessageAdapter;
import com.kingleystudio.shopnchat.models.PayloadWrapper;
import com.kingleystudio.shopnchat.models.Response;
import com.kingleystudio.shopnchat.models.di.Message;
import com.kingleystudio.shopnchat.models.dto.GetDialogMessages;
import com.kingleystudio.shopnchat.models.dto.SendMessage;
import com.kingleystudio.shopnchat.net.SocketHelper;
import com.kingleystudio.shopnchat.models.Types;
import com.kingleystudio.shopnchat.utils.JsonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import kotlinx.coroutines.channels.Send;

public class DialogActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private int dialogid = Config.dialogToShow;

    private List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    private EditText edit;
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_dialog);

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

        recyclerView = findViewById(R.id.messagesView);
        messageAdapter = new MessageAdapter(DialogActivity.this, messages);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(DialogActivity.this));

        edit = findViewById(R.id.messageEdit);

        findViewById(R.id.sendMessageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socketHelper.send(new PayloadWrapper(new SendMessage(edit.getText().toString(), dialogid, Config.currentUser.getId())));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
        try {
            socketHelper.send(new PayloadWrapper(new GetDialogMessages(dialogid)));
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
                    case Types.GET_MESSAGES:
                        messages = JsonUtils.convertJsonNodeToList(response.getPayload().get(Types.MESSAGES), Message.class);
                        messageAdapter.setData(messages);
                        messageAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }
}
