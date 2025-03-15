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
public class AccountChangeName implements IBaseMessage {
    private String newname;
    @Override
    public String getType() {
        return Types.CHANGE_NAME;
    }

    @Override
    public void initFromObject(JSONObject object) throws JSONException {

    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Types.USERNAME, newname);
        return jsonObject;
    }
}
