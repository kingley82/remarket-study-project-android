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
public class GetOrStartDialogByMembers implements IBaseMessage {
    private int member1;
    private int member2;
    @Override
    public String getType() {
        return Types.GET_DIALOG;
    }

    @Override
    public void initFromObject(JSONObject object) throws JSONException {

    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray members = new JSONArray();
        members.put(member1);
        members.put(member2);
        jsonObject.put(Types.MEMBERS, members);
        return jsonObject;
    }
}
