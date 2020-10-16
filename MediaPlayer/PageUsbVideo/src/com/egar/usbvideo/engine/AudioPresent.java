package com.egar.usbvideo.engine;

import android.media.AudioManager;

import com.egar.mediaui.App;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbvideo.interfaces.IAudioFocusListener;

import juns.lib.android.utils.AudioManagerUtil;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/29 19:32
 * @see {@link }
 * 声音焦点处理
 */
public class AudioPresent {
    private String TAG = "AudioPresent";
    private int mAudioFocusFlag = 0;
    private IAudioFocusListener iAudioFocusListener;

    public AudioPresent() {
    }

    public void setAudioFocusListener(IAudioFocusListener iAudioFocusListener) {
        this.iAudioFocusListener = iAudioFocusListener;
    }

    public void removeAudioFocusListener() {
        this.iAudioFocusListener = null;
    }

    protected AudioManager.OnAudioFocusChangeListener mAfChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            mAudioFocusFlag = focusChange;
            LogUtil.d(TAG, "focusChange : " + focusChange);
            switch (focusChange) {
                // 暂时失去Audio Focus，并会很快再次获得。必须停止Audio的播放，
                // 但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    LogUtil.d(TAG, "AudioManager.AUDIOFOCUS_LOSS_TRANSIENT");
                    if (iAudioFocusListener != null) {
                        iAudioFocusListener.onAudioFocusTransient();
                    }

                    break;
                // 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    LogUtil.d(TAG, "AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    if (iAudioFocusListener != null) {
                        iAudioFocusListener.onAudioFocusDuck();
                    }

                    break;
                // 失去了Audio Focus，并将会持续很长的时间。
                // 这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
                // 而因为停止播放Audio的时间会很长，如果程序因为这个原因而失去AudioFocus，
                // 最好不要让它再次自动获得AudioFocus而继续播放，不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。
                // 这里直接放弃AudioFocus，当然也不用再侦听远程播放控制【如下面代码的处理】。
                // 要再次播放，除非用户再在界面上点击开始播放，才重新初始化Media，进行播放
                case AudioManager.AUDIOFOCUS_LOSS:
                    LogUtil.d(TAG, "AudioManager.AUDIOFOCUS_LOSS");
                    if (iAudioFocusListener != null) {
                        iAudioFocusListener.onAudioFocusLoss();
                    }

                    break;
                //获得了Audio Focus；
                case AudioManager.AUDIOFOCUS_GAIN:
                    LogUtil.d(TAG, "AudioManager.AUDIOFOCUS_GAIN");
                    if (iAudioFocusListener != null) {
                        iAudioFocusListener.onAudioFocusGain();
                    }
                    break;
            }
        }
    };


    /**
     * 注册音频焦点
     *
     * @param flag 1 注册音频焦点 2释放音频焦点
     *
     * @return
     */
    public boolean registerAudioFocus(int flag) {
        boolean isExecuted = false;
        LogUtil.d(TAG, "registerAudioFocus ( " + flag + " )");
        switch (flag) {
            case 1:
                if (!isAudioFocuseGained()) {
                    int result = AudioManagerUtil.requestMusicGain(App.getContext(), mAfChangeListener);
                    LogUtil.d(TAG, "registerAudioFocus : result -> " + result);
                    if (result == 1) {
                        isExecuted = true;
                        mAudioFocusFlag = AudioManager.AUDIOFOCUS_GAIN;
                        if (iAudioFocusListener != null) {
                            iAudioFocusListener.onAudioFocus(1);
                        }

                    }
                }
                break;
            case 2:
                int result = AudioManagerUtil.abandon(App.getContext(), mAfChangeListener);
                LogUtil.d(TAG, "registerAudioFocus : result -> " + result);
                if (result == 1) {
                    isExecuted = true;
                    mAudioFocusFlag = AudioManager.AUDIOFOCUS_LOSS;
                    if (iAudioFocusListener != null) {
                        iAudioFocusListener.onAudioFocus(2);
                    }
                }
                break;
        }
        return isExecuted;
    }

    public boolean isAudioFocuseGained() {
        return (mAudioFocusFlag == AudioManager.AUDIOFOCUS_GAIN);
    }
}
