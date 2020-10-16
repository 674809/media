package com.egar.usbimage.engine;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.egar.usbimage.presenter.AudioFocusPresenter;
import com.egar.usbimage.presenter.IAudioFocusPresenter;

public class UsbImagePlayServiceBase implements IAudioFocusPresenter.IAudioFocusListener {
    //TAG
    private static final String TAG = "UsbImagePlayServiceBase";

    /**
     * Audio focus presenter implement.
     */
    private IAudioFocusPresenter mAudioFocusPresenter;

    public void init(Context context) {
        // Initial AudioFocusPresenter
        mAudioFocusPresenter = new AudioFocusPresenter(context);
        mAudioFocusPresenter.setAudioFocusListener(this);
    }

    @Override
    public void onAudioFocusDuck() {
    }

    @Override
    public void onAudioFocusTransient() {
    }

    @Override
    public void onAudioFocusGain() {
    }

    @Override
    public void onAudioFocusLoss() {
    }

    /**
     * Execute request audio focus.
     *
     * @return {@link AudioManager#AUDIOFOCUS_REQUEST_GRANTED} or {@link AudioManager#AUDIOFOCUS_REQUEST_FAILED}
     */
    public int reqAudioFocus() {
        Log.i(TAG, "reqAudioFocus()");
        if (mAudioFocusPresenter != null) {
            return mAudioFocusPresenter.reqAudioFocus();
        }
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    /**
     * Execute abandon audio focus.
     *
     * @return {@link AudioManager#AUDIOFOCUS_REQUEST_GRANTED} or {@link AudioManager#AUDIOFOCUS_REQUEST_FAILED}
     */
    protected int abandonAudioFocus() {
        Log.i(TAG, "abandonAudioFocus()");
        if (mAudioFocusPresenter != null) {
            return mAudioFocusPresenter.abandonAudioFocus();
        }
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }
}
