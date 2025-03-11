package com.kingleystudio.shopnchat.models.dto;

import com.kingleystudio.shopnchat.models.IBaseMessage;
import com.kingleystudio.shopnchat.models.Types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountChangePassword implements IBaseMessage {
    private String uname;
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
        jsonObject.put(Types.USERNAME, uname);
        JSONArray passwords = new JSONArray();
        passwords.put(oldpass);
        passwords.put(newpass);
        jsonObject.put(Types.PASSWORD, passwords);
        return jsonObject;
    }
}
