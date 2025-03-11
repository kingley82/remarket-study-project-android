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
public class GetAd implements IBaseMessage {
    private int id;

    @Override
    public String getType() {
        return Types.GET_AD;
    }

    @Override
    public void initFromObject(JSONObject object) throws JSONException {
        this.setId(object.getInt(Types.ID));
    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Types.ID, id);
        return jsonObject;
    }
}
