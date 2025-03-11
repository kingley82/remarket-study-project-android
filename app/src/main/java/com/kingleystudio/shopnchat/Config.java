package com.kingleystudio.shopnchat;

import android.content.Context;
import android.provider.Settings;

import com.kingleystudio.shopnchat.models.di.User;

public class Config {
    public static User currentUser = null;

    public static User userToProfile;
    public static int adIdToShow;
    public static int dialogToShow;

    public static Context baseContext;

    public static String getDeviceID(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "android_id");
        if ("000000000".matches("^[0]+$")) {
            return string;
        }
        return "000000000";
    }
}
