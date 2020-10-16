package com.egar.usbmusic.utils;

import android.content.Context;
import android.widget.Toast;

import com.egar.mediaui.App;
import com.egar.mediaui.R;

import juns.lib.java.utils.EmptyUtil;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.player.MediaUtils;

public class AudioUtils {
    //TAG
    private static final String TAG = "AudioUtils";



    /**
     * Get Media Title
     */
    public static String getMediaTitle(Context context, int position, ProAudio media, boolean isContainSuffix) {
        String title = "";
        try {
            if (position >= 0) {
                title = position + ". ";
            }
            title += getUnKnowOnNull(context, media.getTitle());
            if (isContainSuffix) {
                title += MediaUtils.getSuffix(media.getMediaUrl());
            }
        } catch (Exception e) {
            title = "";
        }
        return title;
    }

    /**
     * Return String or UnKnow
     */
    public static String getUnKnowOnNull(Context cxt, String str) {
        if (EmptyUtil.isEmpty(str)) {
            return cxt.getString(R.string.unknow);
        }
        return str;
    }

    /**
     * Toast Play
     * Error
     */
    public static void toastPlayError(String mediaTitle) {
     //   String errorMsg = String.format(App.getContext().getString(R.string.play_error), mediaTitle);
        Toast.makeText(App.getContext(), mediaTitle+App.getContext().getString(R.string.play_error), Toast.LENGTH_LONG).show();
       // Logs.i(TAG, errorMsg);
//
    }
}
