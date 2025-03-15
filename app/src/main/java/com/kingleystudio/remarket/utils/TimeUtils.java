package com.kingleystudio.remarket.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TimeUtils {
    public static String TimestampToString(long ts) {
        ts*=1000L;
        Timestamp timestamp = new Timestamp(ts);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(timestamp);
    }
}
