package com.kingleystudio.remarket.net;

import com.kingleystudio.remarket.models.Response;
import com.kingleystudio.remarket.utils.Logs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.enums.Opcode;
import org.java_websocket.handshake.ServerHandshake;

public class NewSocketHelper{
    WebSocketClient client;
    private static final NewSocketHelper sSocketHelper = new NewSocketHelper();
    private Set<NewSocketHelper.SocketListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public NewSocketHelper() {
        try {
            URI uri = new URI("ws://192.168.0.108:8088/");
            client = new WSClient(uri);
            client.addHeader("Origin", "http://developer.example.com");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String data) {
        Logs.net("Sending: " + data);
        client.send(data);
    }

    public void send(PayloadWrapper payloadWrapper) {
        send(payloadWrapper.toString());
    }

    public void sendFramed(PayloadWrapper payloadWrapper) {
        sendFramed(payloadWrapper.toString());
    }

    public void sendFramed(String data) {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        ByteBuffer longelinebuffer = ByteBuffer.wrap(data.getBytes());
        longelinebuffer.rewind();
        for (int pos = 2;;pos +=2) {
            if (pos < longelinebuffer.capacity()) {
                longelinebuffer.limit(pos);
                client.sendFragmentedFrame(Opcode.TEXT, longelinebuffer, false);
                assert (longelinebuffer.remaining() == 0);
            } else {
                longelinebuffer.limit(longelinebuffer.capacity());
                client.sendFragmentedFrame(Opcode.TEXT, longelinebuffer, true);
                break;
            }
        }
    }

    public interface SocketListener {
        void onConnected();
        void onDisconnected();
        void onReceive(Response response);
        //void onNewMessage()
    }

    public void subscribe(NewSocketHelper.SocketListener socketListener) {
        Logs.net("SUB" + socketListener.getClass().toString());
        this.listeners.add(socketListener);
    }

    public void unsubscribe(Object obj) {
        Logs.net("UNSUB" + obj.getClass().toString());
        if (obj instanceof SocketHelper.SocketListener) {
            Iterator<NewSocketHelper.SocketListener> it = this.listeners.iterator();
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
        for (NewSocketHelper.SocketListener onConnected : this.listeners) {
            onConnected.onConnected();
        }
    }

    public void sendToSubscribers(Response response) {
        for (NewSocketHelper.SocketListener onReceive : this.listeners) {
            onReceive.onReceive(response);
        }
    }

    public void sendDisconnectedToSubscribers() {
        for (NewSocketHelper.SocketListener onDisconnected : this.listeners) {
            onDisconnected.onDisconnected();
        }
    }

    protected static class WSClient extends WebSocketClient {
        public WSClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Logs.net("new connection opened");
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Logs.net("closed with exit code " + code + " additional info: " + reason);
        }

        @Override
        public void onMessage(String message) {
            Logs.net("received message: " + message);
        }

        @Override
        public void onMessage(ByteBuffer message) {
            Logs.net("received ByteBuffer");
        }

        @Override
        public void onError(Exception ex) {
            Logs.enet("an error occurred:" + ex);
        }
    }
}