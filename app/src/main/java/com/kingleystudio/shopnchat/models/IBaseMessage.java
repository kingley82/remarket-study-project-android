package com.kingleystudio.shopnchat.models;

import org.json.JSONException;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;

public interface IBaseMessage {
    String getType();
    void initFromObject(JSONObject object) throws JSONException;
    JSONObject toJsonObject() throws JSONException;
}
