package com.kingleystudio.remarket.models.dto;

import com.kingleystudio.remarket.models.IBaseMessage;
import com.kingleystudio.remarket.models.Types;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchAds implements IBaseMessage {
    private String word;
    private int count = 1000;
    private int offset = 0;
    @Override
    public String getType() {
        return Types.SEARCH;
    }

    @Override
    public void initFromObject(JSONObject object) throws JSONException {

    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Types.SEARCH, word);
        jsonObject.put(Types.COUNT, count);
        jsonObject.put(Types.OFFSET, offset);
        return jsonObject;
    }
}
