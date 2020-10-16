package com.egar.usbvideo.engine;

import android.os.Handler;
import android.util.Log;

/**
 * Video light mode controller
 *
 * @author Jun.Wang
 */
public class MediaLightModeController {
    //TAG
    private final String TAG = "LightModeController";

    /**
     * Thread handler
     */
    Handler mHandler = new Handler();

    // Means Light Mode Enable and Show Panel
    private final int ON = 1;
    // Means Light Mode Enable and Hide Panel
    private final int OFF = 2;

    /**
     * Light Mode Flag
     * <p>{@link #ON} or {@link #OFF}</p>
     */
    private int mLightMode = ON;

    /**
     * {@link MediaLightModeListener} object
     */
    private MediaLightModeListener mMediaLightModeListener;

    public interface MediaLightModeListener {
        void onLightOn();

        void onLightOff();
    }

    /**
     * Constructor
     */
    public MediaLightModeController() {
    }

    /**
     * Is Light Mode On
     */
    protected boolean isLightOn() {
        return mLightMode == ON;
    }

    /**
     * Set Light Mode
     *
     * @param lightMode {@link #ON} or {@link #OFF}
     */
    private void setLightMode(int lightMode) {
        this.mLightMode = lightMode;
        Log.i(TAG, "mMediaLightModeListener : " + mMediaLightModeListener);
        Log.i(TAG,"lightMode : "+lightMode);
        if (mMediaLightModeListener != null) {
            switch (mLightMode) {
                case ON:
                    mMediaLightModeListener.onLightOn();
                    break;
                case OFF:
                    mMediaLightModeListener.onLightOff();
                    break;
            }
        }
    }

    public void makeLightOff() {
        Log.i(TAG, "makeLightOff()");
        setLightMode(OFF);
    }

    public void makeLightOn() {
        Log.i(TAG, "makeLightOn()");
        setLightMode(ON);
    }

    /**
     * Keep current mode is ON.
     */
    public void keepLightOn() {
        Log.i(TAG, "keepLightOn()");
        makeLightOn();
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Switch Light Mode
     */
    public void switchLightMode() {
        Log.i(TAG, "switchLightMode()");
        switch (mLightMode) {
            case ON:
                makeLightOff();
                break;
            case OFF:
                resetLightMode();
                break;
        }
    }

    /**
     * Reset Light Mode
     */
    public void resetLightMode() {
        Log.i(TAG, "resetLightMode()");
        makeLightOn();
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "resetLightMode -run()-");
                setLightMode(OFF);
            }
        }, 3 * 1000);
    }

    /**
     * Settings on activity or other view destroy
     */
    public void destroy() {
        Log.i(TAG, "destroy()");

        mHandler.removeCallbacksAndMessages(null);
    }

    public void addModeListener(MediaLightModeListener l) {
        this.mMediaLightModeListener = l;
    }
}
