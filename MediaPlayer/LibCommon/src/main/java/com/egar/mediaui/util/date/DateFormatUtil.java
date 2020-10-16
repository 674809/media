package com.egar.mediaui.util.date;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期格式工具类
 *
 * @author Jun.Wang
 */
public class DateFormatUtil {
    // TAG
    // private static final String TAG = "DateFormatUtil";

    // MSEC
    private static final int HOUR_MSEC = 60 * 60 * 1000;
    private static final int MIN_MSEC = 60 * 1000;

    /**
     * Get Formatted Time
     */
    public static String format(long time, String format) {
        return format(new Date(time), format);
    }

    /**
     * Get Formatted Time
     */
    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Check if two time is at the same day.
     */
    public static boolean isSameDay(long time1, long time2) {
        String strFormat1 = format(time1, DateTemplates.FORMAT_42);
        String strFormat2 = format(time2, DateTemplates.FORMAT_42);
        return strFormat1.equals(strFormat2);
    }

    /**
     * Get formated Time Period
     */
    public static String getFormatHHmmss(long msec) {
        if (msec % 1000 > 0) {
            msec = (msec / 1000) * 1000 + 1000;
        }

        // Hour
        long hour = msec / HOUR_MSEC;
        // Minutes
        long min = (msec - hour * HOUR_MSEC) / MIN_MSEC;
        // Seconds
        long restMS = msec - hour * HOUR_MSEC - min * MIN_MSEC;
        long sec = restMS / 1000;

        String strHour = String.valueOf(hour);
        if (hour == 0) {
            strHour = "";
        } else if (hour < 10) {
            strHour = "0" + hour;
        }

        String strMin = String.valueOf(min);
        if (min < 10) {
            strMin = "0" + min;
        }

        String strSec = String.valueOf(sec);
        if (sec < 10) {
            strSec = "0" + sec;
        }

        if ("".equals(strHour)) {
            return strMin + ":" + strSec;
        }

        return strHour + ":" + strMin + ":" + strSec;
    }

    /**
     * 转换时间为整秒时间
     */
    public static int getIntSecondMsec(int msec) {
        if (msec <= 0) {
            return -1;
        }

        // <1S
        if (msec < 1000) {
            return 1000;
        }

        // >=1S
        int remainder = msec % 1000;
        if (remainder > 0) {
            return (msec / 1000) * 1000 + 1000;
        }
        return msec;
    }
}
