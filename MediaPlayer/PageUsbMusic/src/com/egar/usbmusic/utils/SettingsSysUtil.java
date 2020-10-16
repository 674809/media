package com.egar.usbmusic.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

/**
 * {@link Settings} util
 */
public class SettingsSysUtil {
    //TAG
    private static final String TAG = "SettingsSysUtil";

    /**
     * Set music state
     * <p>0-未打开</p>
     * <p>1-打开</p>
     * <p>2-播放</p>
     */
    public static void setAudioState(Context context, int state) {
        try {
            Log.i(TAG, "setAudioState(Context," + state + ")");
            Settings.System.putInt(context.getApplicationContext().getContentResolver(), "app_music_state", state);

            //
            int storedFlag = Settings.System.getInt(context.getContentResolver(), "app_music_state");
            Log.i(TAG, "AudioState: " + storedFlag);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    /**
     * Set Radio state
     * <p>0-未打开</p>
     * <p>1-打开</p>
     * <p>2-播放</p>
     */
    public static void setFmState(Context context, int state) {
        try {
            Log.i(TAG, "setFmState(Context," + state + ")");
            Settings.System.putInt(context.getApplicationContext().getContentResolver(), "app_fm_state", state);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    /**
     * Get theme value
     *
     * @return int-0 默认主题 ; 1 苹果主题
     */
    public static int getThemeVal(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "theme_setting", 0);
    }

    /**
     * 设置记忆播放器标记
     * <p>1. ACC_ON 根据此标记打开播放器</p>
     * <p>2. B+ 根据此标记打开播放器</p>
     */
    public static void setRememberPlayFlag(Context context, boolean isRemember) {
        try {
            Log.i(TAG, "setRememberPlayFlag(Context," + isRemember + ")");
            String flag = isRemember ? "music" : "";
            Settings.System.putString(context.getContentResolver(), "remembered_application", flag);
        } catch (Exception e) {
            Log.i(TAG, "setRememberPlayFlag :: ERROR- " + e.getMessage());
        }
    }

//    /**
//     * 设置上一次播放的媒体类型
//     * <p>1. 实体Media按键，据此切换媒体源</p>
//     * <p>2. 方控Source按键，据此切换媒体源</p>
//     *
//     * @param mediaTypeEnum {@link MediaTypeEnum}
//     */
//    public static void setLastMediaType(Context context, MediaTypeEnum mediaTypeEnum) {
//        try {
//            Log.i(TAG, "setLastMediaType(context," + mediaTypeEnum);
//            final String PREFER_KEY = "LAST_PLAYED_MEDIA_TYPE";
//            Settings.System.putInt(context.getContentResolver(), PREFER_KEY, mediaTypeEnum.getType());
//        } catch (Exception e) {
//            Log.i(TAG, "setLastMediaType :: ERROR- " + e.getMessage());
//        }
//    }
//
//    /**
//     * @param context   {@link Context}
//     * @param pageState MediaPageState
//     */
//    public static void setMediaPageState(Context context, MediaPageState pageState) {
//        try {
//            Log.i(TAG, "setMediaPageState(context," + pageState);
//            final String PREFER_KEY = "CURRENT_MEDIA_PAGE_STATE";
//            Settings.System.putInt(context.getContentResolver(), PREFER_KEY, pageState.getType());
//        } catch (Exception e) {
//            Log.i(TAG, "setMediaPageState :: ERROR- " + e.getMessage());
//        }
//    }
}
