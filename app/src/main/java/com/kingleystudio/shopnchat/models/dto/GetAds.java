package com.kingleystudio.shopnchat.models.dto;

import com.kingleystudio.shopnchat.models.IBaseMessage;
import com.kingleystudio.shopnchat.models.Types;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAds implements IBaseMessage {
    private int user_id = -1;
    private int count = 100;
    private int offset = 0;
    private boolean onlyActive = true;
    @Override
    public String getType() {
        return Types.GET_ADS;
    }

    @Override
    public void initFromObject(JSONObject object) throws JSONException {

    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Types.USER, user_id);
        jsonObject.put(Types.COUNT, count);
        jsonObject.put(Types.OFFSET, offset);
        jsonObject.put(Types.ACTIVE, onlyActive);

        return jsonObject;
    }
}
