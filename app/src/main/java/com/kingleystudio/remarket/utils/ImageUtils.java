package com.kingleystudio.remarket.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageUtils {
    public static Bitmap uriToBitmap(Context context, Uri uri) throws FileNotFoundException {
        InputStream imageStream = context.getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(imageStream);
    }
    public static Bitmap resizeBitmapByWidth(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = 1366;
        float scale = (float) width / (float) newWidth;
        int newHeight = (int)((float) height / scale);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
    public static Bitmap resizeBitmapByHeight(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newHeight = 768;
        float scale = (float) height / (float) newHeight;
        int newWidth = (int)((float) width / scale);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
