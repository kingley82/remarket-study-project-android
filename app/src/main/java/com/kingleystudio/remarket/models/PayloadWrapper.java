package com.kingleystudio.remarket.models;

import androidx.annotation.NonNull;

import com.kingleystudio.remarket.Config;

import org.json.JSONException;
import org.json.JSONObject;


public class PayloadWrapper {
    private JSONObject data;

    public PayloadWrapper(IBaseMessage message) throws JSONException {
        this.data = new JSONObject();
        this.data.put(Types.EVENT, message.getType());
        this.data.put(Types.USERNAME, Config.currentUser == null ? "" : Config.currentUser.getUsername());
        this.data.put(Types.DEVICE_ID, Config.getDeviceID(Config.baseContext));
        this.data.put(Types.PAYLOAD, message.toJsonObject());
    }

    public String getType() throws JSONException {
        return (String) this.data.get(Types.EVENT);
    }

    @NonNull
    @Override
    public String toString() {
        return this.data.toString() + "\n";
    }
}
