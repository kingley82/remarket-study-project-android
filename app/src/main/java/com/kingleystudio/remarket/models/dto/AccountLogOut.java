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
@NoArgsConstructor
public class AccountLogOut implements IBaseMessage {
    @Override
    public String getType() {
        return Types.ACCOUNT_LOGOUT;
    }

    @Override
    public void initFromObject(JSONObject object) throws JSONException {

    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        return new JSONObject();
    }
}
