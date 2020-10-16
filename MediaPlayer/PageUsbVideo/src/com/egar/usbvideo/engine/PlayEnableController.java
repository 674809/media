package com.egar.usbvideo.engine;

import android.util.Log;

/**
 * 播放使能标记
 *
 * @author Jun.Wang
 */
public class PlayEnableController {
    // TAG
    private static final String TAG = "PlayEnableController";

    /**
     * If paused by user?
     * <p>Usually caused by touch pause.</p>
     */
    private static boolean mIsPauseByUser = false;


    /**
     * 暂停 BY 用户点击了暂停
     *
     * @param isPauseByUser :用户是否点击了暂停
     */
    public static void pauseByUser(boolean isPauseByUser) {
        Log.i(TAG, "pauseByUser(" + isPauseByUser + ")");
        mIsPauseByUser = isPauseByUser;
    }

    public static boolean isPauseByUser() {
        return mIsPauseByUser;
    }

    /**
     * 是否可以播放
     * <p>4. mIsPauseByUser == false</p>
     */
    public static boolean isPlayEnable() {
        boolean isPlayEnable = !isPauseByUser();
      //  Log.i(TAG, "isPlayEnable() - isPlayEnable :: " + isPlayEnable);
        return isPlayEnable;
    }


}