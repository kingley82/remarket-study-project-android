package com.kingleystudio.shopnchat.models.dto;

import com.kingleystudio.shopnchat.models.IBaseMessage;
import com.kingleystudio.shopnchat.models.Types;
import com.kingleystudio.shopnchat.models.di.User;
import com.kingleystudio.shopnchat.utils.JsonUtils;

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
public class AdPost implements IBaseMessage {
    private String title = "";
    private float price = 0f;
    private JSONArray images = new JSONArray();
    private String phone = "";
    private String description = "";
    private int seller;
    private String status = "active";

    @Override
    public String getType() {
        return Types.AD_POST;
    }

    @Override
    public void initFromObject(JSONObject object) throws JSONException {
    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Types.TITLE, title);
        jsonObject.put(Types.PRICE, price);
        jsonObject.put(Types.IMAGES, images);
        jsonObject.put(Types.PHONE, phone);
        jsonObject.put(Types.DESCRIPTION, description);
        jsonObject.put(Types.SELLER, seller);
        jsonObject.put(Types.STATUS, status);
        return jsonObject;
    }
}
