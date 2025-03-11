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
@AllArgsConstructor
@NoArgsConstructor
public class AccountSignIn implements IBaseMessage {
    private String deviceId = "";
    private String username = "";
    private String password = "";

    @Override
    public String getType() {
        return Types.ACCOUNT_SIGNIN;
    }

    @Override
    public void initFromObject(JSONObject object) throws JSONException {
        this.setDeviceId((String) object.get(Types.DEVICE_ID));
        this.setUsername((String) object.get(Types.USERNAME));
        this.setPassword((String) object.get(Types.PASSWORD));
    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Types.DEVICE_ID, this.getDeviceId());
        jsonObject.put(Types.USERNAME, this.getUsername());
        jsonObject.put(Types.PASSWORD, this.getPassword());
        return jsonObject;
    }
}
