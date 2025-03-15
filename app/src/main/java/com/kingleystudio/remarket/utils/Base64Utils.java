package com.kingleystudio.remarket.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Base64Utils {
    public static String uriToBase64(Context context, Uri uri) throws FileNotFoundException {
        InputStream imageStream = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String b64) {
        byte[] decodedString = Base64.decode(b64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
