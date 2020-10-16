package com.egar.usbimage.presenter;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.util.LinkedHashSet;
import java.util.Set;

import juns.lib.android.utils.AudioManagerUtil;
import juns.lib.android.utils.Logs;

/**
 * Audio focus presenter
 *
 * @author Jun.Wang
 */
public class AudioFocusPresenter extends IAudioFocusPresenter {
    //TAG
    private static final String TAG = "AudioFocusPresenter";

    /**
     * {@link Context}
     */
    private Context mContext;

    /**
     * Audio focus flag
     * {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT}
     * or {@link AudioManager#AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK}
     * or {@link AudioManager#AUDIOFOCUS_LOSS}
     * or {@link AudioManager#AUDIOFOCUS_GAIN}
     */
    private int mAudioFocusFlag = 0;

    /**
     * Player State Listener out of service
     */
    private Set<IAudioFocusListener> mSetAudioFocusListeners = new LinkedHashSet<>();

    public AudioFocusPresenter(Context context) {
        this.mContext = context;
    }

    @Override
    public void setAudioFocusListener(IAudioFocusListener l) {
        if (l != null) {
            mSetAudioFocusListeners.add(l);
        }
    }

    @Override
    public void removeAudioFocusListener(IAudioFocusListener l) {
        if (l != null) {
            mSetAudioFocusListeners.remove(l);
        }
    }

    /**
     * Listener Audio Focus
     */
    private AudioManager.OnAudioFocusChangeListener mAfChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            Log.i(TAG, "f* -> {focusChange:[" + focusChange + "]");
            mAudioFocusFlag = focusChange;
            switch (focusChange) {
                // 暂时失去Audio Focus，并会很快再次获得。必须停止Audio的播放，
                // 但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源；
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    Log.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_LOSS_TRANSIENT----");
                    respAudioFocusTransient();
                    break;

                // 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    Logs.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK----");
                    respAudioFocusDuck();
                    break;

                // 失去了Audio Focus，并将会持续很长的时间。
                // 这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
                // 而因为停止播放Audio的时间会很长，如果程序因为这个原因而失去AudioFocus，
                // 最好不要让它再次自动获得AudioFocus而继续播放，不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。
                // 这里直接放弃AudioFocus，当然也不用再侦听远程播放控制【如下面代码的处理】。
                // 要再次播放，除非用户再在界面上点击开始播放，才重新初始化Media，进行播放
                case AudioManager.AUDIOFOCUS_LOSS:
                    Log.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_LOSS----");
                    respAudioFocusLoss();
                    break;

                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.i(TAG, "f*>> ----AudioManager.AUDIOFOCUS_GAIN----");
                    respAudioFocusGain();
                    break;
            }
        }
    };

    private void respAudioFocusDuck() {
        for (IAudioFocusListener l : mSetAudioFocusListeners) {
            if (l != null) {
                l.onAudioFocusDuck();
            }
        }
    }

    private void respAudioFocusTransient() {
        for (IAudioFocusListener l : mSetAudioFocusListeners) {
            if (l != null) {
                l.onAudioFocusTransient();
            }
        }
    }

    private void respAudioFocusLoss() {
        for (IAudioFocusListener l : mSetAudioFocusListeners) {
            if (l != null) {
                l.onAudioFocusLoss();
            }
        }
    }

    private void respAudioFocusGain() {
        for (IAudioFocusListener l : mSetAudioFocusListeners) {
            if (l != null) {
                l.onAudioFocusGain();
            }
        }
    }

    @Override
    public int reqAudioFocus() {
        Logs.i(TAG, "reqAudioFocus()");
        int result = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        if (!isAudioFocusGained()) {
            result = AudioManagerUtil.requestMusicGain(mContext, mAfChangeListener);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocusFlag = AudioManager.AUDIOFOCUS_GAIN;
            }
        }
        return result;
    }

    @Override
    public int abandonAudioFocus() {
        Logs.i(TAG, "abandonAudioFocus()");
        int result = AudioManagerUtil.abandon(mContext, mAfChangeListener);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocusFlag = AudioManager.AUDIOFOCUS_LOSS;
        }
        return result;
    }

    @Override
    public boolean isAudioFocusGained() {
        return (mAudioFocusFlag == AudioManager.AUDIOFOCUS_GAIN);
    }
}
