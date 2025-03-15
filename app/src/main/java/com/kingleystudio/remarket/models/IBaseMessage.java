package com.kingleystudio.remarket.models;

import org.json.JSONException;
import org.json.JSONObject;

public interface IBaseMessage {
    String getType();
    void initFromObject(JSONObject object) throws JSONException;
    JSONObject toJsonObject() throws JSONException;
}
