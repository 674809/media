package com.egar.usbvideo.config;

/**
 * Created by:luli on 2019/5/22
 * Describe:保存一些常量
 */
public class Constants {

    public static boolean IS_OPEN_APP = false; //应用是否打开

    public static final String THEME_PATH = "path";//皮肤包路径

    public static final String PLAY_MODE = "play_mode";//播放模式

    //播放时每秒保存一次当前媒体URL和PROGRESS
    public static final String PREFER_KEY_MEDIA_URL = "video_last_played_media_URL";// 媒体URL
    public static final String PREFER_KEY_MEDIA_PROGRESS = "video_last_played_media_progress";//媒体PROGRESS

    public static final String PLAYER_VIDEO_WARNING_FLAG = "player_video_warning_flag";//警告标志
    public static final int PLAYER_VIDEO_WARNING_AGREE = 1;//本次同意
    public static final int PLAYER_VIDEO_WARNING_NO_TIP = 2;//本次不再提示

    public static final String VIDEO_LAST_TARGET_MEDIA_URL = "video_last_target_media_URL";

    public static final int MEDIA_PLAYER = 1;//android原生播放器

    public static final String HAS_UDISK_TIP = "has_udisk_tip";//无U盘提醒

    public static final String  PARAM = "METHOD";



}

