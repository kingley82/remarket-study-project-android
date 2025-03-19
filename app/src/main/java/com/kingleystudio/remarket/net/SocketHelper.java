package com.kingleystudio.remarket.net;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.kingleystudio.remarket.Config;
import com.kingleystudio.remarket.activities.ABCActivity;
import com.kingleystudio.remarket.activities.BaseActivity;
import com.kingleystudio.remarket.models.Response;
import com.kingleystudio.remarket.models.Types;
import com.kingleystudio.remarket.utils.JsonUtils;
import com.kingleystudio.remarket.utils.Logs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dev.gustavoavila.websocketclient.WebSocketClient;

public class SocketHelper {
    private WebSocketClient webSocketClient;
    private static final SocketHelper sSocketHelper = new SocketHelper();
    private Set<SocketListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());


    public static SocketHelper getSocketHelper() {return sSocketHelper;}

    public SocketHelper() {
        URI uri;
        try {
            uri = new URI("ws://192.168.0.108:8088/");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        this.webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Logs.net("Connection opened");
                sendConnectedToSubscribers();
            }

            @Override
            public void onTextReceived(String message) {
                Logs.net("Text received: "+ message);
                Response response = JsonUtils.convertJsonStringToObject(message.trim(), Response.class);
                if (response.getTypeEvent().equals(Types.ACCOUNT_LOGOUT)) {
                    if (!response.getPayload().get(Types.STATUS).equals(Types.ERROR)) {
                        Logs.i("LOGOUT");
                        SharedPreferences sPref = Config.baseContext.getSharedPreferences("account_data", 0);
                        SharedPreferences.Editor edit = sPref.edit();
                        edit.clear();
                        edit.apply();
                        Intent intent = new Intent(Config.baseContext.getApplicationContext(), BaseActivity.class);
                        Config.baseContext.startActivity(intent);
                        Config.currentUser = null;
                    }

                }
                else {
                    sendToSubscribers(response);
                }

            }

            @Override
            public void onBinaryReceived(byte[] data) {
                Logs.net("Binary received");
                Logs.net(data);
            }

            @Override
            public void onPingReceived(byte[] data) {
                Logs.net("ping received");
            }

            @Override
            public void onPongReceived(byte[] data) {
                Logs.net("pong received");
            }

            @Override
            public void onException(Exception e) {
                Logs.enet(e.getMessage());
            }

            @Override
            public void onCloseReceived(int reason, String description) {
                Logs.e("Close received: " + description);
                sendDisconnectedToSubscribers();
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.addHeader("Origin", "http://developer.example.com");
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    public interface SocketListener {
        void onConnected();
        void onDisconnected();
        void onReceive(Response response);
        //void onNewMessage()
    }

    public void subscribe(SocketListener socketListener) {
        Logs.net("SUB" + socketListener.getClass().toString());
        this.listeners.add(socketListener);
    }

    public void unsubscribe(Object obj) {
        Logs.net("UNSUB" + obj.getClass().toString());
        if (obj instanceof SocketListener) {
            Iterator<SocketListener> it = this.listeners.iterator();
            while (it.hasNext()) {
                if (it.next().equals(obj)) {
                    it.remove();
                }
            }
            return;
        }

        throw new IllegalArgumentException("UnSubscriber must be an instance of SocketListener");
    }

    public void sendConnectedToSubscribers() {
        for (SocketListener onConnected : this.listeners) {
            onConnected.onConnected();
        }
    }

    public void sendToSubscribers(Response response) {
        for (SocketListener onReceive : this.listeners) {
            onReceive.onReceive(response);
        }
    }

    public void sendDisconnectedToSubscribers() {
        for (SocketListener onDisconnected : this.listeners) {
            onDisconnected.onDisconnected();
        }
    }

    public void send(String data) {
        if (data.contains("\"e\":\"adp\""))
            Logs.net("Sending data: ad post");
        else
            Logs.net("Sending data: " + data);
        this.webSocketClient.send(data);
    }

    public void send(PayloadWrapper data) {
        send(data.toString());
    }
}