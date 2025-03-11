package com.kingleystudio.shopnchat.utils;

import android.util.Log;

public class Logs {
    public static void i(Object message) {
        Log.i("MAIN", message.toString());
    }
    public static void e(Object message) {
        Log.e("MAIN", message.toString());
    }
    public static void net(Object message) {
        Log.i("NET", message.toString());
    }
    public static void enet(Object message) {
        Log.e("NET", message.toString());
    }
}
