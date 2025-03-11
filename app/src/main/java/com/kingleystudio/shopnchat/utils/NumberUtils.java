package com.kingleystudio.shopnchat.utils;

import android.content.Context;
import android.util.TypedValue;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtils {
    public static String roundFloatAndCastToString(float number, int nums_after) {
        DecimalFormat df = new DecimalFormat("#." + "#".repeat(nums_after));
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(number);
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
