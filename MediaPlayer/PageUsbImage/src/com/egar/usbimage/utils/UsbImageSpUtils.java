package com.egar.usbimage.utils;

import android.content.Context;

import com.egar.usbimage.engine.PlayMode;

public class UsbImageSpUtils {
    public static void init(Context context) {
        SpUtils.instance().init(context);
    }

    public static int getPlayMode(boolean isSave, int playMode) {
        final String KEY = "USB_MEDIA_PLAY_MODE_OF_IMAGE";
        if (isSave) {
            SpUtils.instance().saveInt(KEY, playMode);
        }
        return SpUtils.instance().getInt(KEY, PlayMode.LOOP);
    }

    public static int getLastPos(boolean isSave, int position) {
        final String KEY = "USB_MEDIA_LAST_MEDIA_POS";
        if (isSave) {
            SpUtils.instance().saveInt(KEY, position);
        }
        return SpUtils.instance().getInt(KEY, -1);
    }

    public static String getLastMediaUrl(boolean isSave, String mediaUrl) {
        final String KEY = "USB_MEDIA_LAST_MEDIA_URL";
        if (isSave) {
            SpUtils.instance().saveString(KEY, mediaUrl);
        }
        return SpUtils.instance().getString(KEY, "");
    }
}