package com.egar.usbmusic.interfaces;

import java.util.List;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/14 17:55
 * @see {@link }
 */
public interface IPlayerState {
    /**
     *播放状态改变
     */
    void playStateChange(int state);

    /**
     * 播放进度改变
     * @param path
     * @param progress
     * @param duration
     */
    void playProgressChanged(String path ,int progress,int duration);

    /**
     * 播放模式改变
     */
    void playModeChange(int i);

    /**
     * 扫描状态改变
     *  <p>1 START</p>
     * <p>2 REFRESHING</p>
     * <p>3 END</p>
     */
    void scanStateChanged(int state);

    /**
     * 挂载状态改变
     */
    void MountStateChanged(List list);

    void  onAudioPlayServiceConnected();
}
