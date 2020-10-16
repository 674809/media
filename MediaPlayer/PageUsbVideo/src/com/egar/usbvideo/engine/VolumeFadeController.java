package com.egar.usbvideo.engine;

import android.os.Handler;

import com.egar.mediaui.util.LogUtil;
import com.egar.usbvideo.view.VideoTextureView;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/31 15:19
 * @see {@link }
 */
public class VolumeFadeController {
    private String TAG ="VolumeFadeController";
    private static Handler mmFadeHandler = new Handler();
    private float mmVolume = 0.2f;
    private final float VOLUME_MAX = 1.0f, VOLUME_MIN = 0.2f;
    private final float STEP_VOLUME = 0.2f;
    private VideoTextureView iVideoPlayer;


    public VolumeFadeController(VideoTextureView iVideoPlayer){
      this.iVideoPlayer = iVideoPlayer;
    }

    private float stepCalc(boolean isAdd) {
        if (isAdd) {
            return (float) (Math.round((mmVolume + STEP_VOLUME) * 100)) / 100;
        }
        return (float) (Math.round((mmVolume - STEP_VOLUME) * 100)) / 100;
    }

    /**
     * 淡入
     */
    public  void resetAndFadeIn() {
        LogUtil.i(TAG,"resetAndFadeIn");
        mmFadeHandler.removeCallbacksAndMessages(null);
        mmVolume = VOLUME_MIN;
        fadeIn();
    }

    private   void fadeIn() {
        setPlayerVolume();
        LogUtil.d(TAG, "fadeIn() - mmVolume : " + mmVolume);
        if (mmVolume >= VOLUME_MAX) {
            mmVolume = VOLUME_MAX;
            return;
        }

        //
        mmVolume = stepCalc(true);
        if (mmVolume <= VOLUME_MAX) {
            mmFadeHandler.removeCallbacksAndMessages(null);
            mmFadeHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fadeIn();
                }
            }, 100);
        }
    }

    /**
     * 淡出
     */
    public  void resetAndFadeOut() {
        LogUtil.i(TAG,"resetAndFadeOut");
        mmFadeHandler.removeCallbacksAndMessages(null);
        mmVolume = VOLUME_MAX;
        fadeOut();
    }

    private void fadeOut() {
        setPlayerVolume();
        LogUtil.d(TAG, "fadeOut() - mmVolume : " + mmVolume);
        if (mmVolume < VOLUME_MIN) {
            mmVolume = VOLUME_MIN;
            return;
        }

        //Calculator
        mmVolume = stepCalc(false);
        if (mmVolume >= VOLUME_MIN) {
            mmFadeHandler.removeCallbacksAndMessages(null);
            mmFadeHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fadeOut();
                }
            }, 100);
        }
    }

    public  void setPlayerVolume() {
        if (mmVolume >= VOLUME_MIN && mmVolume <= VOLUME_MAX) {
            LogUtil.d(TAG, "setPlayerVolume() - mmVolume : " + mmVolume);
         iVideoPlayer.setVolume(mmVolume, mmVolume);
        }
    }

    public  void destroy() {
        iVideoPlayer = null;
        mmFadeHandler.removeCallbacksAndMessages(null);
    }
}
