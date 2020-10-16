package com.egar.usbimage.engine;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.egar.usbimage.utils.UsbImageSpUtils;
import com.egar.usbimage.utils.UsbImageUtils;

import java.util.List;

import juns.lib.media.bean.ProImage;

public class UsbImagePlayService extends UsbImagePlayServiceBase {
    //TAG
    private static final String TAG = "UsbImagePlayService";

    /**
     * Play image handler.
     */
    private PlayHandler mPlayHandler;
    // Play one media per 4 minutes.
    private static final int PLAY_PERIOD = 4 * 1000;

    // Play information
    private int mCurrPos;
    private List<ProImage> mListData;

    /**
     * Loop direction.
     */
    private int mLoopDirection = LoopDirection.FROM_RIGHT_TO_LEFT;

    static final class LoopDirection {
        static final int FROM_LEFT_TO_RIGHT = 1;
        static final int FROM_RIGHT_TO_LEFT = 2;
        static final int RANDOM = 3;
    }

    /**
     * {@link UsbImagePlayCallback} object.
     */
    private UsbImagePlayCallback mPlayCallback;

    /**
     * Play callback interface.
     */
    public interface UsbImagePlayCallback {
        /**
         * @param state {@link PlayState}
         */
        void onPlayStateChanged(int state);

        /**
         * @param mode {@link PlayMode}
         */
        void onPlayModeChanged(int mode);
    }

    private UsbImagePlayService() {
    }

    private static class SingletonHolder {
        private static final UsbImagePlayService INSTANCE = new UsbImagePlayService();
    }

    public static UsbImagePlayService instance() {
        return SingletonHolder.INSTANCE;
    }

    private static class PlayHandler extends Handler {
        boolean mmIsPlaying = false;
    }

    public void addPlayCallback(UsbImagePlayCallback callback) {
        mPlayCallback = callback;
    }

    public void loadData(List<ProImage> listData) {
        mListData = listData;
        mCurrPos = getLastPos();
        isUserPause = false;
    }

    /**
     * Play prev media by user.
     */
    public void playPrevByUser() {
        Log.i(TAG, "playPrevByUser()");
        // register audio focus.
        //reqAudioFocus();
        // Play media.
        isUserPause = false;
        playPrev();
    }

    /**
     * Play prev media.
     */
    private void playPrev() {
        // Set pager index;
        setPlayPosOfPrev();
        // Automatically play.
        autoPlayPrev();
    }

    /**
     * Set previous media position.
     */
    private void setPlayPosOfPrev() {
        int playMode = UsbImageSpUtils.getPlayMode(false, 0);
        if (playMode == PlayMode.RANDOM) {
            setPlayPosOfRandom();
            return;
        }

        // Set loop direction.
        mLoopDirection = LoopDirection.FROM_LEFT_TO_RIGHT;
        // Set play position.
        if (mListData == null || mListData.size() == 0) {
            mCurrPos = -1;
        } else {
            mCurrPos--;
            if (mCurrPos < 0) {
                mCurrPos = mListData.size() - 1;
            }
        }
    }

    /**
     * Set random play position.
     */
    private void setPlayPosOfRandom() {
        // Set loop direction.
        mLoopDirection = LoopDirection.RANDOM;
        // Set play position.
        if (mListData == null || mListData.size() == 0) {
            mCurrPos = -1;
        } else {
            mCurrPos = UsbImageUtils.getRandomNum(mCurrPos, mListData.size());
        }
    }

    /**
     * Exec automatic task.
     */
    private void autoPlayPrev() {
        Log.d(TAG, "autoPlayPrev()");
        if (mPlayHandler == null) {
            mPlayHandler = new PlayHandler();
        }
        mPlayHandler.removeCallbacksAndMessages(null);
        mPlayHandler.mmIsPlaying = true;
        mPlayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playPrev();
            }
        }, PLAY_PERIOD);

        // Notify play.
        notifyPlay();
    }

    public void notifyPlay() {
        try {
            // Save media play information.
            ProImage media = getCurrMedia();
            UsbImageSpUtils.getLastMediaUrl(true, media.getMediaUrl());
            UsbImageSpUtils.getLastPos(true, mCurrPos);
        } catch (Exception ignored) {
        }
        //Callback.
        if (mPlayCallback != null) {
            mPlayCallback.onPlayStateChanged(PlayState.PLAY);
        }
    }

    /**
     * Play next media by user.
     */
    public void playNextByUser() {
        Log.i(TAG, "playNextByUser()");
        // register audio focus.
        //reqAudioFocus();
        // Set pager.
        isUserPause = false;
        playNext();
    }

    /**
     * Play next media.
     */
    private void playNext() {
        // Set pager index;
        setPlayPosOfNext();
        // Automatically play.
        autoPlayNext();
    }

    /**
     * Set next media position.
     */
    private void setPlayPosOfNext() {
        int playMode = UsbImageSpUtils.getPlayMode(false, 0);
        if (playMode == PlayMode.RANDOM) {
            setPlayPosOfRandom();
            return;
        }

        // Set loop direction.
        mLoopDirection = LoopDirection.FROM_RIGHT_TO_LEFT;
        // Set play position.
        if (mListData == null || mListData.size() == 0) {
            mCurrPos = -1;
        } else {
            mCurrPos++;
            if (mCurrPos > mListData.size() - 1) {
                mCurrPos = 0;
            }
        }
    }

    public void setPos(int pos) {
        mCurrPos = pos;
    }

    /**
     * Exec automatic task.
     */
    private void autoPlayNext() {
        Log.d(TAG, "autoPlayNext()");
        if (mPlayHandler == null) {
            mPlayHandler = new PlayHandler();
        }
        mPlayHandler.removeCallbacksAndMessages(null);
        mPlayHandler.mmIsPlaying = true;
        mPlayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playNext();
            }
        }, PLAY_PERIOD);

        // Notify play.
        notifyPlay();
    }

    private boolean isUserPause = false;

    /**
     * Play or pause by user.
     */
    public void playOrPauseByUser() {
        Log.d(TAG, "playOrPauseByUser : " + mPlayHandler.mmIsPlaying);

        if (mPlayHandler.mmIsPlaying) {
            pauseByUser(true);
            isUserPause = true;
        } else {
            isUserPause = false;
            resumeByUser();
        }
    }

    public void pauseByUser(boolean isCallback) {
        if (mPlayHandler != null) {
            mPlayHandler.mmIsPlaying = false;
//            isUserPause = false;
            mPlayHandler.removeCallbacksAndMessages(null);
            // Callback.
            if (isCallback && mPlayCallback != null) {
                mPlayCallback.onPlayStateChanged(PlayState.PAUSE);
            }
        }
    }

    public void resumeByUser() {
        // register audio focus.
        //reqAudioFocus();
        if (mPlayHandler != null && !isUserPause) {
            // Automatically play.
            autoPlay();
        }
    }

    /**
     * Play media automatically.
     */
    public void autoPlay() {
        switch (mLoopDirection) {
            case LoopDirection.FROM_LEFT_TO_RIGHT:
                autoPlayPrev();
                break;
            case LoopDirection.RANDOM:
            case LoopDirection.FROM_RIGHT_TO_LEFT:
            default:
                autoPlayNext();
                break;
        }
    }

    public int getCurrPos() {
        return mCurrPos;
    }

    public int getTotalCount() {
        return mListData == null ? 0 : mListData.size();
    }

    public ProImage getCurrMedia() {
        try {
            return mListData.get(mCurrPos);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Switch play mode.
     */
    public void switchPlayMode() {
        int playMode = getPlayMode();
        switch (playMode) {
            case PlayMode.LOOP:
                setPlayMode(PlayMode.RANDOM);
                break;
            case PlayMode.RANDOM:
                setPlayMode(PlayMode.LOOP);
                break;
        }
    }

    /**
     * Set play mode.
     */
    public void setPlayMode(int playMode) {
        if (playMode == PlayMode.NONE) {
            playMode = getPlayMode();
        } else {
            UsbImageSpUtils.getPlayMode(true, playMode);
        }
        if (mPlayCallback != null) {
            mPlayCallback.onPlayModeChanged(playMode);
        }
    }

    /**
     * Get play mode
     *
     * @return PlayMode values.
     */
    public int getPlayMode() {
        return UsbImageSpUtils.getPlayMode(false, 0);
    }

    /**
     * Get last play position.
     */
    private int getLastPos() {
        // 列表数据为空，将历史数据置空，因为无意义
        if (mListData == null || mListData.size() == 0) {
            UsbImageSpUtils.getLastPos(true, -1);
            UsbImageSpUtils.getLastMediaUrl(true, "");
            return -1;
        }

        // 获取历史播放位置
        int lastPos = UsbImageSpUtils.getLastPos(false, -1);
        if (lastPos < 0) {
            lastPos = 0;
        }

        // 根据历史播放信息来确认历史播放位置是否正确？
        try {
            ProImage historyMedia = mListData.get(lastPos);
            String lastMediaUrl = UsbImageSpUtils.getLastMediaUrl(false, "");
            // 根据历史位置获取到的MediaUrl != 缓存的MediaUrl -> 说明位置需要重新定位.
            if (!TextUtils.equals(lastMediaUrl, historyMedia.getMediaUrl())) {
                lastPos = 0;
                for (int LOOP = mListData.size(), idx = 0; idx < LOOP; idx++) {
                    ProImage tmpMedia = mListData.get(idx);
                    if (TextUtils.equals(tmpMedia.getMediaUrl(), lastMediaUrl)) {
                        lastPos = idx;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            lastPos = 0;
        }

        Log.i(TAG, "getLastPos: " + lastPos);
        return lastPos;
    }

    @Override
    public void onAudioFocusGain() {
        super.onAudioFocusGain();
        Log.i(TAG, "onAudioFocusGain()");
        //autoPlay();
    }

    @Override
    public void onAudioFocusTransient() {
        super.onAudioFocusTransient();
        Log.i(TAG, "onAudioFocusTransient()");
    }

    @Override
    public void onAudioFocusDuck() {
        super.onAudioFocusDuck();
        Log.i(TAG, "onAudioFocusDuck()");
    }

    @Override
    public void onAudioFocusLoss() {
        super.onAudioFocusLoss();
        Log.i(TAG, "onAudioFocusLoss()");
        pauseByUser(true);
    }

    public void destroy() {
        abandonAudioFocus();
        mPlayCallback = null;
        if (mPlayHandler != null) {
            mPlayHandler.removeCallbacksAndMessages(null);
        }
    }
}
