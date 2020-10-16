package com.egar.usbimage.presenter;

import android.media.AudioManager;

/**
 * Audio focus presenter.
 * <p>Define audio focus actions to be implement.</p>
 *
 * @author Jun.Wang
 */
public abstract class IAudioFocusPresenter {
    /**
     * Playing audio focus Listener
     */
    public interface IAudioFocusListener {
        /**
         * 暂时失去AudioFocus，但是可以继续播放，不过要在降低音量。
         * <p>Register below event will call this method.</p>
         * {@link AudioManager#AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK}
         */
        void onAudioFocusDuck();

        /**
         * 暂时失去Audio Focus，并会很快再次获得。
         * 必须停止Audio的播放， 但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源.
         * <p>Register below event will call this method.</p>
         * {@link AudioManager#AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK}
         */
        void onAudioFocusTransient();

        /**
         * 获得了Audio Focus
         * <p>Register below event will call this method.</p>
         * {@link AudioManager#AUDIOFOCUS_GAIN}
         */
        void onAudioFocusGain();

        /**
         * 失去了Audio Focus，并将会持续很长的时间。
         * 这里因为可能会停掉很长时间，所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。
         * 而因为停止播放Audio的时间会很长，如果程序因为这个原因而失去AudioFocus，
         * 最好不要让它再次自动获得AudioFocus而继续播放，不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。
         * <p>别的音源注册了如下事件：</p>
         * {@link AudioManager#AUDIOFOCUS_GAIN}
         */
        void onAudioFocusLoss();
    }

    /**
     * Set {@link IAudioFocusListener}
     *
     * @param l {@link IAudioFocusListener}
     */
    public abstract void setAudioFocusListener(IAudioFocusListener l);

    /**
     * Remove {@link IAudioFocusListener}
     *
     * @param l {@link IAudioFocusListener} to remove.
     */
    public abstract void removeAudioFocusListener(IAudioFocusListener l);

    /**
     * Execute request audio focus.
     *
     * @return {@link AudioManager#AUDIOFOCUS_REQUEST_GRANTED} or {@link AudioManager#AUDIOFOCUS_REQUEST_FAILED}
     */
    public abstract int reqAudioFocus();

    /**
     * Execute abandon audio focus.
     *
     * @return {@link AudioManager#AUDIOFOCUS_REQUEST_GRANTED} or {@link AudioManager#AUDIOFOCUS_REQUEST_FAILED}
     */
    public abstract int abandonAudioFocus();

    /**
     * Is audio focus registered
     *
     * @return true-registered; false-unregistered or loss.
     */
    public abstract boolean isAudioFocusGained();
}
