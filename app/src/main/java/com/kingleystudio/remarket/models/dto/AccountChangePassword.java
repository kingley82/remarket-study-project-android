package com.kingleystudio.remarket.models.dto;

import com.kingleystudio.remarket.models.IBaseMessage;
import com.kingleystudio.remarket.models.Types;

import org.json.JSONArray;
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
public class AccountChangePassword implements IBaseMessage {
    private String oldpass;
    private String newpass;
    @Override
    public String getType() {
        return Types.CHANGE_PASSWORD;
    }

    @Override
    public void initFromObject(JSONObject object) throws JSONException {

    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray passwords = new JSONArray();
        passwords.put(oldpass);
        passwords.put(newpass);
        jsonObject.put(Types.PASSWORD, passwords);
        return jsonObject;
    }
}
