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
public class SendMessage implements IBaseMessage {
    private String message;
    private int dialog;
    private int sender;
    @Override
    public String getType() {
        return Types.NEW_MESSAGE;
    }

    @Override
    public void initFromObject(JSONObject object) throws JSONException {

    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Types.TEXT, message);
        jsonObject.put(Types.DIALOG, dialog);
        jsonObject.put(Types.SENDER, sender);
        return jsonObject;
    }
}
