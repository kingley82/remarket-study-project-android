package com.kingleystudio.remarket.utils;

import android.app.AlertDialog;
import android.content.Context;

public class AlertUtils {
    public static void OkAlert(Context context, String title, String message, String okBtn) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(okBtn, null);
        alert.show();
    }

    public static void OkAlert(Context context, String message, String okBtn) {
        OkAlert(context, null, message, okBtn);
    }

    public static void OkAlert(Context context, String message) {
        OkAlert(context, null, message, "Ок");
    }
}
