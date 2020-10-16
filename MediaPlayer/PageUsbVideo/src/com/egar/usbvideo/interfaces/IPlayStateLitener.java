package com.egar.usbvideo.interfaces;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/25 16:19
 * @see {@link }
 */
public interface IPlayStateLitener {
    /**
     * @param var1 NONE = 0;
     *             PLAY = 1;
     *             PREPARED = 2;
     *             PAUSE = 3;
     *             COMPLETE = 4;
     *             RELEASE = 5;
     *             SEEK_COMPLETED = 6;
     *             ERROR = 100;
     *             ERROR_PLAYER_INIT = 101;
     *             ERROR_FILE_NOT_EXIST = 102;
     *             REFRESH_UI = 200;
     */
    void onPlayStateChanged(int var1);
}
