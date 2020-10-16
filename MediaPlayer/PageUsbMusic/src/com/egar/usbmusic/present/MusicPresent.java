package com.egar.usbmusic.present;


import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.egar.mediaui.App;
import com.egar.mediaui.util.LogUtil;
import com.egar.music.api.AudioPlayRespFactory;
import com.egar.music.api.EgarApiMusic;
import com.egar.usbmusic.interfaces.IPlayerState;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import juns.lib.android.utils.Logs;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.FilterType;
import juns.lib.media.play.IAudioPlayListener;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/12/11 14:33
 * @see {@link }
 */
public class MusicPresent {

    private String TAG = "MusicPresent";
    private static MusicPresent mMusicPresent;
    private final String MUSIC_TAG = "MUSIC_PLAYER";
    //音乐操作 api
    private EgarApiMusic mEgarApiMusic;
    private EgarApiMusicListenerImp egarApiMusicListenerImp;
    private IAudioPlayListener mAudioPlayListener;
    private AllSongsLoadingTask mFilterLoadingTask;
    //播放模式
    private int playMode = 0;

    public IPlayerState iPlayerState;
    private static MyHandler myHandler;
    //播放状态改变
    private static final int PLAY_STATE_CHANGE = 0;
    //播放进度改变
    private static final int PLAY_PROGRESS_CHANGE = 1;
    //播放模式改变
    private static final int PLAY_MODEL_CHANGE = 2;
    //服务连接成功
    private static final int AUDIO_SERVICE_CONNECT = 3;
    //扫描状态
    private static final int SCANNER_CHANGE = 4;
    //是否在音乐界面
    private boolean isMusicPage = false;


    private static class MyHandler extends Handler {
        private  WeakReference<MusicPresent> weakReference;

        private MyHandler(MusicPresent musicPresent) {
            weakReference = new WeakReference<MusicPresent>(musicPresent);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MusicPresent mMusicPresent = weakReference.get();
            if (mMusicPresent != null) {
                switch (msg.what) {
                    case PLAY_STATE_CHANGE:
                        if (mMusicPresent.iPlayerState != null) {
                            mMusicPresent.iPlayerState.playStateChange(msg.arg1);
                        }
                        break;
                    case PLAY_PROGRESS_CHANGE:
                        if (mMusicPresent.iPlayerState  != null) {
                            mMusicPresent.iPlayerState .playProgressChanged((String) msg.obj, msg.arg1, msg.arg2);
                        }
                        break;
                    case PLAY_MODEL_CHANGE:
                        if (mMusicPresent.iPlayerState  != null) {
                            mMusicPresent.iPlayerState .playModeChange(msg.arg1);
                        }
                        break;
                    case AUDIO_SERVICE_CONNECT:
                        if (mMusicPresent.iPlayerState  != null) {
                            mMusicPresent.iPlayerState .onAudioPlayServiceConnected();
                        }
                        break;
                    case SCANNER_CHANGE:
                        if (mMusicPresent.iPlayerState  != null) {
                            mMusicPresent.iPlayerState .scanStateChanged(msg.arg1);
                        }
                        break;

                }
            }
        }
    }

    private MusicPresent() {

    }

    public static MusicPresent getInstance() {
        if (mMusicPresent == null) {
            mMusicPresent = new MusicPresent();
        }
        return mMusicPresent;
    }

    /**
     * 初始化EgarApi 操作句柄
     */
    public void initEgarMusicApi() {
        if (mEgarApiMusic == null) {
            LogUtil.i(TAG, "initEgarMusicApi");
            egarApiMusicListenerImp = new EgarApiMusicListenerImp();
            mEgarApiMusic = new EgarApiMusic(App.getContext(), egarApiMusicListenerImp);
            myHandler = new MyHandler(this);
        }
        if (!isPlayServiceConnected()) {
            bindPlayService(true);
        }
    }

    /**
     * 绑定播放服务
     *
     * @param isConnected
     */
    public void bindPlayService(boolean isConnected) {
        if (mEgarApiMusic != null) {
            if (isConnected) {
                mEgarApiMusic.bindPlayService();
                LogUtil.i(TAG,"bindPlayService");
            } else {
                mEgarApiMusic.unbindPlayService();
                LogUtil.i(TAG,"unbindPlayService");
            }
        }
    }

    /**
     * 是否在音乐界面
     *
     * @param isVisable
     */
    public void setMusicPage(boolean isVisable) {
        this.isMusicPage = isVisable;
    }

    public boolean isMusicPage() {
        return isMusicPage;
    }

    //========================================= 绑定服务 start ====================================
    /*
     * 首先去绑定服务，绑定成功后会有此回调
     * EgarApiMusic.bindService()
     */
    private class EgarApiMusicListenerImp implements EgarApiMusic.IEgarApiMusicListener {
        /**
         * 当MusicPlayService绑定成功
         */
        @Override
        public void onAudioPlayServiceConnected() {
            LogUtil.i(TAG, "PlayServiceConnected");
            if (mEgarApiMusic != null) {
                //   LogUtil.i(TAG, "addPlayListener(" + MUSIC_TAG + ")");
                mAudioPlayListener = new AudioPlayRespFactory(new AudioPlayListenerImp()).getRespCallback();
                addPlayListener();
            }
            myHandler.sendMessage(myHandler.obtainMessage(AUDIO_SERVICE_CONNECT, 0, 0));
            if (!isPlaying() && !isScanning()) {
                firstAutoPlay();
            }
        }

        /*
         * 当MusicPlayServiced 绑定断开
         */
        @Override
        public void onAudioPlayServiceDisconnected() {
            LogUtil.i(TAG, "PlayServiceDisconnected");
            removePlayListener();
        }
    }


//========================================= 回调状态改变start ==================================================

    /**
     * 歌曲播放回调
     */
    private class AudioPlayListenerImp implements IAudioPlayListener {

        @Override
        public void onMountStateChanged(List list) throws RemoteException {
            LogUtil.i(TAG, "onMountStateChanged U盘事件 list ="+list.size());
        }

        @Override
        public void onScanStateChanged(int i) throws RemoteException {
            LogUtil.i(TAG, "onScanStateChanged");
            if (isMusicPage()) {
                LogUtil.i(TAG, "onScanStateChanged 上报扫描状态");
                myHandler.sendMessage(myHandler.obtainMessage(SCANNER_CHANGE, i, 0));
            }

        }

        @Override
        public void onGotDeltaMedias(List list) throws RemoteException {
            LogUtil.i(TAG, "onGotDeltaMedias 上报增量数据");
        }

        @Override
        public void onPlayStateChanged(int i) throws RemoteException {
            if (isMusicPage() && isPlayerFocused()) {
                LogUtil.i(TAG, "onPlayStateChanged 上报播放状态 =" + i);
                myHandler.sendMessage(myHandler.obtainMessage(PLAY_STATE_CHANGE, i, 0));
            }


        }

        @Override
        public void onPlayProgressChanged(String mediaPath, int progress, int duration) throws RemoteException {
            if (MusicPresent.getInstance().isPlaying()) {
               // LogUtil.i(TAG, "onPlayProgressChanged 上报播放进度 =" + progress);
                myHandler.sendMessage(myHandler.obtainMessage(PLAY_PROGRESS_CHANGE, progress, duration, mediaPath));
            }
        }

        @Override
        public void onPlayModeChanged(int mode) throws RemoteException {
            LogUtil.i(TAG, "onPlayModeChanged 播放模式改变 =" + mode);
            myHandler.sendMessage(myHandler.obtainMessage(PLAY_MODEL_CHANGE, mode, 0, 0));
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }
//=========================================end==================================================


//============================= Api start  封装EgarApi 原始接口=====================================

    /**
     * 判断服务是否连接
     * {@link EgarApiMusic.IEgarApiMusicListener#isPlayServiceConnected()}
     */
    public boolean isPlayServiceConnected() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.isPlayServiceConnected();
        }
        return false;
    }

    /**
     * 自动播放
     */
    public void autoPlay() {
        LogUtil.i(TAG, "autoPlay");
        if (mEgarApiMusic != null) {
            mEgarApiMusic.autoPlay();
        }
    }

    /**
     * 是否正在扫描
     */
    public boolean isScanning() {
        return mEgarApiMusic != null && mEgarApiMusic.isScanning();
    }

    /**
     * 获取当前媒体在播放列表中的位置
     */
    public int getCurrPos() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getCurrPos();
        }
        return 0;
    }

    /**
     * 获取播放列表总数
     */
    public int getTotalCount() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getTotalCount();
        }
        return 0;
    }

    /**
     * // 获取当前媒体媒体对象
     */
    public ProAudio getCurrMedia() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getCurrMedia();
        }
        return null;
    }

    /**
     * 当前音频的文件路径
     */
    public String getCurrMediaPath() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getCurrMediaPath();
        }
        return null;
    }

    /**
     * 获取当前音频的播放进度
     */
    public long getProgress() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getProgress();
        }
        return 0;
    }

    /**
     * 获取当前音频总时长
     */
    public long getDuration() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getDuration();
        }
        return 0;
    }

    /**
     * 是否在播放
     */
    public boolean isPlaying() {
        return mEgarApiMusic != null && mEgarApiMusic.isPlaying();
    }

    /**
     * 播放
     */
    public void play() {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.play();
        }
    }

    /**
     * 通知音频服务更新播放列表
     *
     * @param params
     */
    public void applyPlayList(String[] params) {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.applyPlayList(params);
        }
    }

    /**
     * 通知音频服务更新播放信息
     */
    public void applyPlayInfo(String mediaUrl, int pos) {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.applyPlayInfo(mediaUrl, pos);
        }
    }

    /**
     * 播放指定某一条歌曲
     */
    public void playByUrlByUser(String mediaPath) {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.playByUrlByUser(mediaPath);
        }
    }

    /**
     * 播放上一曲
     */
    public void playPrevByUser() {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.playPrevByUser();
        }
    }

    /**
     * 播放下一首
     */
    public void playNextByUser() {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.playNextByUser();
        }
    }

    /**
     * 暂停播放
     */
    public void playOrPauseByUser() {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.playOrPauseByUser();
        }
    }

    /**
     * 释放播放器
     */
    public void release() {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.release();
        }
    }

    public void seekTo(int time) {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.seekTo(time);
        }
    }

    /**
     * 音频进度跳转是异步的，用来判断跳转是否完成
     *
     * @return
     */
    public boolean isSeeking() {
        return mEgarApiMusic != null && mEgarApiMusic.isSeeking();
    }

    public String getLastMediaUrl() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getLastMediaUrl();
        }
        return "";
    }

    public long getLastProgress() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getLastProgress();
        }
        return 0;
    }

    public void switchPlayMode(int supportFlag) {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.switchPlayMode(supportFlag);
        }
    }

    public void setPlayMode(int mode) {
        if (mEgarApiMusic != null) {
            mEgarApiMusic.setPlayMode(mode);
        }
    }

    public int getPlayMode() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getPlayMode();
        }
        return 0;
    }

    public void focusPlayer() {
        if (mEgarApiMusic != null) {
            try {
                mEgarApiMusic.focusPlayer();
            } catch (Exception e) {
                Logs.i(TAG, "focusPlayer() >> e: " + e.getMessage());
            }
        }
    }

    /**
     * 判断是否获取声音焦点
     *
     * @return
     */
    public boolean isPlayerFocused() {
        Logs.i(TAG, "isPlayerFocused ");
        try {
            return mEgarApiMusic != null && mEgarApiMusic.isPlayerFocused();
        } catch (Exception e) {
            Logs.i(TAG, "isPlayerFocused() >> e: " + e.getMessage());
            return false;
        }
    }


    /**
     * 获取歌曲信息
     *
     * @param sortBy
     * @param params
     *
     * @return
     */
    public List getAllMedias(int sortBy, String[] params) {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getAllMedias(sortBy, params);
        }
        return null;
    }

    /**
     * Update collect state.
     *
     * @param position
     * @param media
     *
     * @return
     */
    public int updateMediaCollect(int position, ProAudio media) {
        if (mEgarApiMusic != null) {
            Log.i(TAG,"updateMediaCollect  position =" +position);
            return mEgarApiMusic.updateMediaCollect(position, media);
        }
        return 0;
    }

    /**
     * 获取文件
     *
     * @return
     */
    public List getFilterFolders() {
        if (mEgarApiMusic != null) {
            LogUtil.i(TAG, "getFilterFolders");
            return mEgarApiMusic.getFilterFolders();
        }
        LogUtil.i(TAG, "getFilterFolders  is null");
        return null;
    }

    /**
     * 获取所有媒体信息
     *
     * @param mapColumns
     * @param sortOrder
     *
     * @return
     */
    public List getMediasByColumns(Map<String, String> mapColumns, String sortOrder) {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getMediasByColumns(mapColumns, sortOrder);
        }
        return null;
    }

    /**
     * 清空收藏列表
     * @return
     */
    public int clearHistoryCollect() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.clearHistoryCollect();
        }
        return 0;
    }

    //获取歌手文件夹
    public List getFilterArtists() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getFilterArtists();
        }
        return null;
    }


    public List getFilterAlbums() {
        if (mEgarApiMusic != null) {
            return mEgarApiMusic.getFilterAlbums();
        }
        return null;
    }
//==================================第一次封装 end========================================

    /**
     * Is Playing Media
     */
    public boolean isPlayingSameMedia(String mediaUrl) {
        boolean isPlayingSameMedia = isPlaying() && TextUtils.equals(getCurrMediaPath(), mediaUrl);
        Log.i(TAG, "isPlayingSameMedia : " + isPlayingSameMedia);
        return isPlayingSameMedia;
    }


//-----------------------------------------再次自己封装 start----------------------------------------

    /**
     * 设置播放模式
     */
    public void switchPlayMode() {
        if (playMode == 0) {
            setPlayMode(1);
            playMode += 1;
        } else if (playMode == 1) {
            setPlayMode(2);
            playMode += 1;
        } else if (playMode == 2) {
            setPlayMode(3);
            playMode = 0;
        }
    }

    /**
     * 首次自动播放
     */
    public void firstAutoPlay() {
        ProAudio media = getCurrMedia();
        if (media != null) {
            LogUtil.i(TAG, "firstAutoPlay  media = "+media.getCoverUrl());
            if (!isPlaying()) {
                playOrPauseByUser();
                LogUtil.i(TAG, "playOrPauseByUser");
            }
        } else {
            loadAllSongs();
        }
    }


    /**
     * 播放指定路径歌曲
     *
     * @param mediaUrl
     * @param position
     */
    public void playMusic(String mediaUrl, int position) {
        Log.i(TAG, "playAndOpenPlayerActivity(" + mediaUrl + "," + position + ")");
        // Apply play information.
        applyPlayInfo(mediaUrl, position);
        // Check if already playing.
        if (isPlayingSameMedia(mediaUrl)) {
            Log.i(TAG, "### The media to play is already playing now. ###");
        } else {
            Logs.i("TIME_COL", "-3-" + System.currentTimeMillis());
            playByUrlByUser(mediaUrl);
        }
    }


//-----------------------------------------再次封装 end----------------------------------------


    //==================================finish activity api=========================================

    /**
     * 服务断掉时，使用此方法
     * finish  时调用此方法
     */
    public void removePlayListener() {
        if (mEgarApiMusic != null) {
            LogUtil.i(TAG, "removePlayListener(" + MUSIC_TAG + ")");
            mEgarApiMusic.removePlayListener(MUSIC_TAG, mAudioPlayListener);
        }
    }


    public void addPlayListener() {
        if (mEgarApiMusic != null && mAudioPlayListener != null) {
            mEgarApiMusic.addPlayListener(true, MUSIC_TAG, mAudioPlayListener);
            LogUtil.i(TAG, "addPlayListener(" + MUSIC_TAG + ")");
        }
    }

    public void destoryEgarMusicApi() {
        LogUtil.i(TAG, "destoryEgarMusicApi");
        if (isPlayServiceConnected()) {
            MusicPresent.getInstance().bindPlayService(false);
        }
        removePlayListener();
    }
    //==================================finish activity end=========================================

    //获取所有歌曲数据
    public void loadAllSongs() {
        if (mFilterLoadingTask != null) {
            mFilterLoadingTask.cancel(true);
            mFilterLoadingTask = null;
        }
        mFilterLoadingTask = new AllSongsLoadingTask();
        mFilterLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 第一次获取歌曲数据
     */

    private class AllSongsLoadingTask extends AsyncTask<Void, Void, List<ProAudio>> {


        AllSongsLoadingTask() {
            LogUtil.i(TAG, "AllSongsLoadingTask");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<ProAudio> doInBackground(Void... voids) {
            try {
                // Get play list from MusicPlayService.
                List<ProAudio> list = getAllMedias(FilterType.MEDIA_NAME, null);
                // Update play list of MusicPlayService.
                applyPlayList(null);
                return list;
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ProAudio> audios) {
            super.onPostExecute(audios);
            try {
                if (audios.size() > 0) {
                    LogUtil.i(TAG, "postExecute= " + audios.get(0).getTitle());
                    LogUtil.i(TAG, "isAudioFocus= " + isPlayerFocused());
                    focusPlayer();
                    autoPlay();
                } else {
                    Log.i(TAG, "audios is null");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, e.toString());
            }
        }
    }
    //interface

    /**
     * 设置播放状态监听给 musicFragment
     *
     * @param linsteren
     */
    public void setPlayerStateLinsteren(IPlayerState linsteren) {
        this.iPlayerState = linsteren;
    }

    /**
     * 取消播放状态监听
     */
    public void removePlayerStateLinsteren() {
        LogUtil.i(TAG,"removePlayerStateLinsteren");
        this.iPlayerState = null;
    }

    public IPlayerState getPlayerState() {
        return iPlayerState;
    }
}
