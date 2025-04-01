package com.kingleystudio.remarket.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kingleystudio.remarket.Config;
import com.kingleystudio.remarket.R;
import com.kingleystudio.remarket.activities.adapters.MessageAdapter;
import com.kingleystudio.remarket.models.di.User;
import com.kingleystudio.remarket.net.PayloadWrapper;
import com.kingleystudio.remarket.models.Response;
import com.kingleystudio.remarket.models.di.Dialog;
import com.kingleystudio.remarket.models.di.Message;
import com.kingleystudio.remarket.models.dto.GetDialogMessages;
import com.kingleystudio.remarket.models.dto.SendMessage;
import com.kingleystudio.remarket.net.SocketHelper;
import com.kingleystudio.remarket.models.Types;
import com.kingleystudio.remarket.utils.JsonUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DialogActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private Dialog dialog = Config.dialogToShow;

    private List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    private EditText edit;
    private TextView title;
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
        title = findViewById(R.id.titleDialog);
        User userInChat = dialog.getMember1().getId() == Config.currentUser.getId() ? dialog.getMember2() : dialog.getMember1();
        title.setText(userInChat.getUsername());
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.userToProfile = userInChat;
                newActivity(ProfileActivity.class);
            }
        });

        findViewById(R.id.sendMessageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socketHelper.send(new PayloadWrapper(new SendMessage(edit.getText().toString(), dialog.getId())));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                edit.getText().clear();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
        try {
            socketHelper.send(new PayloadWrapper(new GetDialogMessages(dialog.getId())));
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
                    case Types.NEW_MESSAGE:
                        Message msg = JsonUtils.convertJsonNodeToObject(response.getPayload().get(Types.MESSAGE), Message.class);
                        if (msg.getDialog() == dialog.getId()) {
                            messages.add(msg);
                            messageAdapter.setData(messages);
                            messageAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        });
    }
}
