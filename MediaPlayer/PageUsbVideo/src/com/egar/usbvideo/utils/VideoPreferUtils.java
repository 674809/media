package com.egar.usbvideo.utils;


import com.egar.usbvideo.config.Constants;

import juns.lib.media.flags.PlayMode;


public class VideoPreferUtils extends PreferenceHelper {
    /**
     * Get play mode
     *
     * @return js.lib.android.media.PlayMode
     */
    public static int getPlayMode() {
        return getInt(Constants.PLAY_MODE, PlayMode.LOOP);
    }

    /**
     * @param playMode {@link PlayMode}
     */
    public static void savePlayMode(int playMode) {
        saveInt(Constants.PLAY_MODE, playMode);
    }

    /**
     * Get last target mediaUrl to play
     */
    public static String getLastTargetMediaUrl() {
        return getString(Constants.VIDEO_LAST_TARGET_MEDIA_URL, "");
    }

    public static void saveLastTargetMediaUrl(String mediaUrl) {
        saveString(Constants.VIDEO_LAST_TARGET_MEDIA_URL, mediaUrl);
    }

    /**
     * Get last played media information
     *
     * @return String[] [0]mediaUrl,[1]progress
     */
    public static String[] getLastPlayedMediaInfo() {
        //Get
        String[] mediaInfos = new String[2];
        mediaInfos[0] = getString(Constants.PREFER_KEY_MEDIA_URL, "");
        mediaInfos[1] = getInt(Constants.PREFER_KEY_MEDIA_PROGRESS, 0).toString();
        return mediaInfos;
    }

    /**
     * Save last played media information
     *
     * @param mediaUrl 媒体URL
     * @param progress 当前进度Progress
     */
    public static void saveLastPlayedMediaInfo(String mediaUrl, int progress) {
        saveString(Constants.PREFER_KEY_MEDIA_URL, mediaUrl);
        saveInt(Constants.PREFER_KEY_MEDIA_PROGRESS, progress);
    }

    /**
     * Used to flag warning information
     * <p>
     * <p>1 “本次同意”---应用退出后，下次进入时继续提示，选择后进入应用</p>
     * <p>2 “本次不再提示”---没有熄火，则不再提示，选择后进入应用</p>
     */
//    public static int getVideoWarningFlag() {
//        // final String preferKey = "PLAYER_VIDEO_WARNING_FLAG";
//        if (isSet) {
//            saveInt(Constants.PLAYER_VIDEO_WARNING_FLAG, flag);
//        }
//        return getInt(Constants.PLAYER_VIDEO_WARNING_FLAG, 1);
//    }

    /**
     * @param flag 1 “本次同意”;2 “本次不再提示”
     */
    /*public static void saveVideoWarningFlag(int flag) {
        saveInt(Constants.PLAYER_VIDEO_WARNING_FLAG, flag);
    }*/


    /**
     * Used to flag warning information
     * <p>
     * <p>0 "测试版本" - 不提示无U盘</p>
     * <p>1 "正式版本" - 提示无U盘</p>
     */
    public static int getNoUDiskToastFlag(boolean isSet) {
        final String preferKey = "VIDEO_PLAYER_NO_UDISK_TOAST_FLAG";
        int flag = getInt(preferKey, 1);
        if (isSet) {
            switch (flag) {
                case 1:
                    flag = 0;
                    break;
                case 0:
                    flag = 1;
                    break;
            }
            saveInt(preferKey, flag);
        }
        return flag;
    }
}
