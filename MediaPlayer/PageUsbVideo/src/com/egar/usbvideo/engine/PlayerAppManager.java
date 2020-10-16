package com.egar.usbvideo.engine;

import android.annotation.SuppressLint;
import android.content.Context;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbvideo.config.Constants;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Player Application Manager
 *
 */
public class PlayerAppManager {
    // TAG
    private static final String TAG = "PlayerAppManager";

    /**
     * Player Objects
     */
    @SuppressLint("UseSparseArrays")
    private static Set<Context> mSetContexts = new LinkedHashSet<>();

    /**
     * Cache Context[Activity or Service] Object
     */
    public static void addContext(Context actObj) {
        if (actObj != null) {
            mSetContexts.add(actObj);
        }
    }

    /**
     * Remove Context[Activity or Service] Object
     */
    public static void removeContext(Context actObj) {
        if (actObj != null) {
            mSetContexts.remove(actObj);
        }
    }



    /**
     * Exit Current Player
     */
    public static void exitCurrPlayer() {
        LogUtil.d(TAG, "exitCurrPlayer()");
        Constants.IS_OPEN_APP = false;
        clearPlayer();
    }

    /**
     * 清空播放器
     */
    private static void clearPlayer() {
        LogUtil.d(TAG,"clearPlayer()");
        try {
            Object[] objArr = mSetContexts.toArray();
            for (Object obj : objArr) {
                Context context = (Context) obj;
                if (context instanceof MainActivity) {
                    ((MainActivity) context).finish();
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "clearPlayer() :: ERROR - " + e.getMessage());
            e.printStackTrace();
        }
    }
}