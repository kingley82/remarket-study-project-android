package com.kingleystudio.remarket.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class AlertUtils {
    public static void OkAlert(Context context, String title, String message, String okBtn, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(okBtn, listener);
        alert.show();
    }

    public static void OkAlert(Context context, String message, String okBtn) {
        OkAlert(context, null, message, okBtn, null);
    }

    public static void OkAlert(Context context, String message) {
        OkAlert(context, null, message, "Ок", null);
    }

    public static void OkAlert(Context context, String message, DialogInterface.OnClickListener listener) {

    }
}
