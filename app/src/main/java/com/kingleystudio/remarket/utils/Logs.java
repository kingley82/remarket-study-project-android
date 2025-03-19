package com.kingleystudio.remarket.utils;

import android.content.Context;
import android.util.Log;

import com.kingleystudio.remarket.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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
