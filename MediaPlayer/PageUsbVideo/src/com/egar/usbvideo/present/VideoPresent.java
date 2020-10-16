package com.egar.usbvideo.present;

import android.egar.BlockPolicyClient;
import android.egar.CarManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.egar.mediaui.App;
import com.egar.mediaui.util.LogUtil;
import com.egar.scanner.api.EgarApiScanner;
import com.egar.scanner.api.MediaScanRespFactory;
import com.egar.usbmusic.utils.UdiskUtil;
import com.egar.usbvideo.engine.PlayEnableController;
import com.egar.usbvideo.engine.VolumeFadeController;
import com.egar.usbvideo.interfaces.IRefreshUI;
import com.egar.usbvideo.interfaces.PlayDelegate;
import com.egar.usbvideo.utils.PlayDataCache;
import com.egar.usbvideo.utils.VideoPreferUtils;
import com.egar.usbvideo.view.VideoTextureView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import juns.lib.java.utils.EmptyUtil;
import juns.lib.media.bean.MediaBase;
import juns.lib.media.bean.ProVideo;
import juns.lib.media.flags.FilterType;
import juns.lib.media.flags.MediaScanState;
import juns.lib.media.flags.MediaType;
import juns.lib.media.flags.PlayMode;
import juns.lib.media.flags.PlayState;
import juns.lib.media.scanner.IMediaScanListener;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/11/14 18:26
 */
public class VideoPresent implements EgarApiScanner.IEgarApiScanListener, PlayDelegate, IMediaScanListener {
    private String TAG = "VideoPresent";
    private static VideoPresent mVideoPresent;
    private EgarApiScanner mEgarApiScanner;
    private VideoTextureView mVideoTextureView;
    private LoadLocalMediasTask mLoadLocalMediasTask;
    private List<ProVideo> mListPrograms = new ArrayList<>();
    private BlockPolicyClient blockPolicy;
    protected CarManager carManager;
    private IRefreshUI iRefreshUI;
    /**
     * Current Play Position
     */
    protected int mPlayPos = 0;//代表当前播放的视频文件所处的位置
    /**
     * 目标自动Seek进度，当视频加载完成后，可以据此跳转进度
     */
    public long mTargetAutoSeekProgress = -1;
    /**
     * FLAG - 播放器是否被释放了
     */
    protected boolean mIsPlayerReleased = true;
    protected VolumeFadeController mVolumeFadeController;
    protected static Handler mDelayPlayHandler = new Handler();
    public boolean isInit = false;
    private IMediaScanListener iMediaScanListener;
    private MediaScanDataChange mediaScanDataChange;

    private VideoPresent() {
    }

    public static VideoPresent getInstance() {
        if (mVideoPresent == null) {
            mVideoPresent = new VideoPresent();
        }
        return mVideoPresent;
    }

    public void init(VideoTextureView mVideoview) {
        LogUtil.i(TAG, "init=" + mVideoview);
        initScanService();
        mVideoTextureView = mVideoview;
        mVideoTextureView.setPlayStateListener(this);
        mVolumeFadeController = new VolumeFadeController(mVideoTextureView);
        if (!mVideoPresent.isPlaying() && !isScanning()) {
            LoadFristData();
        }
        isInit = true;
    }

    /**
     * 初始化扫描服务
     */
    public void initScanService() {
        if (mEgarApiScanner == null) {
            LogUtil.i(TAG, "initScanService");
            mEgarApiScanner = new EgarApiScanner(App.getContext(), this);
            //   mediaScanDataChange = new MediaScanDataChange();
            iMediaScanListener = new MediaScanRespFactory(this).getRespCallback();
        }
        mEgarApiScanner.bindScanService();


        if (carManager == null) {
            carManager = new CarManager(App.getContext());
            blockPolicy = carManager.getBlockPolicy(App.getContext());
        }
    }

    public void starScanVideo() {
        if (mEgarApiScanner != null) {
            LogUtil.i(TAG, "starScanVideo");
            mEgarApiScanner.startScan();
        }
    }

    public void removeScanListener() {
        if (mEgarApiScanner != null) {
            LogUtil.i(TAG, "removeScanListener");
            mEgarApiScanner.unbindScanService();
            mEgarApiScanner.removeScanListener("Video", mediaScanDataChange);
            mEgarApiScanner = null;
        }
    }

    public void setRefreshUI(IRefreshUI iRefreshUI) {
        this.iRefreshUI = iRefreshUI;
    }

    public void removeRefreshUI() {
        this.iRefreshUI = null;
    }

    /**
     * 取消Volume 设置事件，防止handler 内存泄漏
     */
    public void removeVolumeFadeController() {
        if (mVolumeFadeController != null) {
            mVolumeFadeController.destroy();
        }
    }

    //====================EgarApiScanner==================================
    @Override
    public void onMediaScanServiceConnected() { //
        LogUtil.i(TAG, "onMediaScanServiceConnected");
        if (mEgarApiScanner != null) {
            LogUtil.i(TAG, "addScanListener");
            mEgarApiScanner.addScanListener(MediaType.VIDEO, true, "Video", iMediaScanListener);
        }

        //  starScanVideo();
    }

    @Override
    public void onMediaScanServiceDisconnected() {
        LogUtil.i(TAG, "onMediaScanServiceConnected");
    }

    /**
     * 是否正在扫描
     */
    public boolean isScanning() {
        return mEgarApiScanner != null && mEgarApiScanner.isScanning(MediaType.VIDEO) && mEgarApiScanner.isScanning(MediaType.AUDIO) &&
                mEgarApiScanner.isScanning(MediaType.IMAGE);
    }

    @Override
    public void onRespScanState(int state) throws RemoteException {
        if(iRefreshUI !=null){
            iRefreshUI.onRespScanState(state);
        }
        LogUtil.i(TAG, "onRespScanState  扫描回调 state =" + state);
        if (MediaScanState.SCAN_VIDEO_END == state) {
            LoadFristData();
        }

    }

    @Override
    public void onRespMountChange(List list) throws RemoteException {
        LogUtil.i(TAG, "  U盘挂载事件改变");
    }

    @Override
    public void onRespDeltaMedias(List list) throws RemoteException {
        LogUtil.i(TAG, "onRespDeltaMedias");
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    //=========================================================================
    public class MediaScanDataChange implements IMediaScanListener {
        /**
         * <p>1 START</p>
         * <p>2 REFRESHING</p>
         * <p>3 END</p>
         */
        @Override
        public void onRespScanState(int state) throws RemoteException {
            LogUtil.i(TAG, "onRespScanState  扫描回调 state =" + state);
            if (MediaScanState.SCAN_VIDEO_END == state || MediaScanState.END == state) {
                LoadFristData();
            }
        }

        /**
         * 上报挂载状态发生改变的设备
         * 发生变化的存储设备列表
         */
        @Override
        public void onRespMountChange(List list) throws RemoteException {
            LogUtil.i(TAG, "  U盘挂载事件改变");
        }

        /**
         * 增量数据回调
         * 增量数据列表，根据需要转换对应的正确媒体列表
         */
        @Override
        public void onRespDeltaMedias(List list) throws RemoteException {
            LogUtil.i(TAG, "onRespDeltaMedias");
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }
    //=======================================================================================

    /**
     * 播放器是否被释放
     */
    protected boolean isPlayerReleased() {
        return mIsPlayerReleased;
    }

    //屏幕保持是否常亮
    public void makeScreenOn(boolean isMakeOn) {
        // CommonUtils.keepScreenLongLight(this, isMakeOn);
        if (blockPolicy != null) {
            if (isMakeOn) {
                LogUtil.d(TAG, "blockPolicy.keepBlockOff(true)");
                blockPolicy.keepBlockOff(true);//关闭屏保
            } else {
                LogUtil.d(TAG, "blockPolicy.keepBlockOff(false)");
                blockPolicy.keepBlockOff(false);//打开屏保
            }
        }
    }

    //========================PlayDelegate=============================
    @Override
    public void setPlayListener(PlayDelegate delegate) {

    }

    @Override
    public void removePlayListener(PlayDelegate delegate) {

    }

    @Override
    public void setListSrcMedias(List<? extends MediaBase> listSrcMedias) {

    }

    @Override
    public List<? extends MediaBase> getListSrcMedias() {
        return null;
    }

    @Override
    public void setPlayList(List<? extends MediaBase> mediasToPlay) {

    }

    @Override
    public List<? extends MediaBase> getListMedias() {
        return null;
    }

    @Override
    public void setPlayPosition(int position) {

    }

    @Override
    public int getTotalCount() {
        if (mListPrograms != null) {
            return mListPrograms.size();
        }
        return 0;
    }

    public List<ProVideo> getListPrograms() {
        return mListPrograms;
    }

    @Override
    public int getCurrIdx() {
        return mPlayPos;
    }

    @Override
    public ProVideo getCurrMedia() {
        if (mVideoTextureView != null) {
            getCurrProVideo();
        }
        return null;
    }


    @Override
    public String getCurrMediaPath() {
        LogUtil.d(TAG, "getCurrMediaPath()");
        if (mVideoTextureView != null) {
            return mVideoTextureView.getMediaPath();
        }
        return "";
    }

    @Override
    public long getProgress() {
        if (mVideoTextureView != null) {
            return mVideoTextureView.getMediaProgress();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mVideoTextureView != null) {
            return mVideoTextureView.getMediaDuration();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (mVideoTextureView != null) {
            return mVideoTextureView.isPlaying();
        }
        return false;
    }

    @Override
    public void play() {
        if (mVideoTextureView != null) {
            mIsPlayerReleased = false;
            mVideoTextureView.play();
            mVideoTextureView.requestFocus();

        }
    }

    @Override
    public void play(String mediaPath) {
        if (mVideoTextureView != null) {
            mIsPlayerReleased = false;
            mVideoTextureView.play(mediaPath);
        }
    }


    @Override
    public void play(int pos) {
        LogUtil.d(TAG, "play()_actions");
        if (mVideoTextureView != null) {
            mIsPlayerReleased = false;
            mVideoTextureView.play();
            mVideoTextureView.requestFocus();
        }
    }

    @Override
    public void playPrev() {
        LogUtil.d(TAG, "playPrev()_actions");
        PlayEnableController.pauseByUser(false);
        if (PlayEnableController.isPlayEnable()) {
            try {
                mPlayPos--;
                //按上一曲小于0 则跳转到列表最后一个文件
                if (mPlayPos < 0) {
                    mPlayPos = mListPrograms.size() - 1;
                }
                clearPlayedMediaInfo();
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
            }
        }
        mDelayPlayHandler.removeCallbacksAndMessages(null);
        mVolumeFadeController.resetAndFadeOut();
        mDelayPlayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "playPrev()_extend");
                execPlay(mPlayPos);
            }
        }, 0);
    }

    @Override
    public void playNext() {
        LogUtil.d(TAG, "playNext()_actions()");
        PlayEnableController.pauseByUser(false);
        if (PlayEnableController.isPlayEnable()) {
            mPlayPos++;
            //按下一曲如果当前是列表最后一个文件，则跳转到第一个文件
            if (mPlayPos >= mListPrograms.size()) {
                mPlayPos = 0;
            }
            clearPlayedMediaInfo();
        }
        mVolumeFadeController.resetAndFadeOut();
        mDelayPlayHandler.removeCallbacksAndMessages(null);

        mDelayPlayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "playNext()_extend");
                execPlay(mPlayPos);
            }
        }, 800);
    }

    @Override
    public void pause() {
        PlayEnableController.pauseByUser(true);
        mDelayPlayHandler.removeCallbacksAndMessages(null);
        if (mVideoTextureView != null) {
            mVideoTextureView.pause();
        }
    }

    @Override
    public void resume() {
        PlayEnableController.pauseByUser(false);
        if (PlayEnableController.isPlayEnable()) {
            if (!isPlaying()) {
                play();
                makeScreenOn(true);
            }
        }
    }

    @Override
    public void release() {
        LogUtil.d(TAG, "release()_actions");
        if (mVideoTextureView != null) {
            mIsPlayerReleased = true;
            mVideoTextureView.clearFocus();
            mVideoTextureView.release();
            mVideoTextureView = null;
        }
        mDelayPlayHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void seekTo(int time) {
        if (mVideoTextureView != null) {
            mVideoTextureView.seekTo(time);
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (mVideoTextureView != null) {
            mVideoTextureView.setVolume(leftVolume, rightVolume);
        }

    }

    @Override
    public void switchPlayMode() {
        int playMode = VideoPreferUtils.getPlayMode();
        LogUtil.d(TAG, "switchPlayMode_playMode : " + playMode);
        switch (playMode) {
            case PlayMode.SINGLE:
                VideoPreferUtils.savePlayMode(PlayMode.LOOP);
                break;
            case PlayMode.LOOP:
                VideoPreferUtils.savePlayMode(PlayMode.SINGLE);
                break;
            default:
                VideoPreferUtils.savePlayMode(PlayMode.LOOP);
                break;
        }
        onPlayModeChange();
    }

    @Override
    public boolean isSeeking() {
        return mVideoTextureView != null && mVideoTextureView.isSeeking();
    }

    @Override
    public int getPlayMode() {
        return VideoPreferUtils.getPlayMode();
    }

    @Override
    public void onPlayModeChange() {
        if (iRefreshUI != null) {
            iRefreshUI.onPlayModeChange();
        }
    }

    @Override
    public void saveTargetMediaPath(String mediaPath) {
        LogUtil.i(TAG, "saveTargetMediaPath=" + mediaPath);
        VideoPreferUtils.saveLastTargetMediaUrl(mediaPath);
    }

    @Override
    public String getLastTargetMediaPath() {
        return VideoPreferUtils.getLastTargetMediaUrl();
    }

    @Override
    public String getLastMediaPath() {
        LogUtil.i(TAG, "getLastTargetMediaPath=" + getLastTargetMediaPath());
        return getLastTargetMediaPath();
    }

    @Override
    public long getLastProgress() {
        int lastProgress = 0;
        try {
            String lastTargetMediaPath = getLastTargetMediaPath();
            //    LogUtil.d(TAG, "getLastProgress() - > lastTargetMediaPath : " + lastTargetMediaPath);
            String[] mediaInfo = getPlayedMediaInfo();
            if (mediaInfo != null) {
                //    LogUtil.d(TAG, "getLastProgress() - > mediaInfo[0] : " + mediaInfo[0]);
                if (mediaInfo[0].equals(lastTargetMediaPath)) {
                    lastProgress = Integer.valueOf(mediaInfo[1]);
                } else {
                    clearPlayedMediaInfo();
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
        LogUtil.d(TAG, "getLastProgress() - > lastProgress= " + lastProgress);
        return lastProgress;
    }

    @Override
    public void savePlayMediaInfo(String mediaPath, int progress) {
        VideoPreferUtils.saveLastPlayedMediaInfo(mediaPath, progress);
    }

    @Override
    public String[] getPlayedMediaInfo() {
        return VideoPreferUtils.getLastPlayedMediaInfo();
    }

    @Override
    public void clearPlayedMediaInfo() {
        VideoPreferUtils.saveLastPlayedMediaInfo("", 0);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        LogUtil.i(TAG, "onPlayStateChanged ::" + playState);
        if (mVideoTextureView == null || isPlayerReleased()) {
            return;
        }
        switch (playState) {
            case PlayState.PLAY:
                LogUtil.i(TAG, "PlayState.PLAY:");
                if (iRefreshUI != null) {
                    iRefreshUI.updatePlayStatus(PlayState.PLAY);
                    iRefreshUI.onPlayStateChanged$Play();
                }
                break;
            case PlayState.PREPARED:
                if (iRefreshUI != null) {
                    iRefreshUI.updatePlayStatus(PlayState.PREPARED);
                    iRefreshUI.onPlayStateChanged$Prepared();
                    mVolumeFadeController.resetAndFadeIn();
                }
                break;
            case PlayState.PAUSE:
                if (iRefreshUI != null) {
                    iRefreshUI.updatePlayStatus(PlayState.PAUSE);
                }

                break;
            case PlayState.COMPLETE:
                if (iRefreshUI != null) {
                    iRefreshUI.updatePlayStatus(PlayState.COMPLETE);
                }

                onPlayStateChanged$Complete();
                break;
            case PlayState.ERROR:
                if (iRefreshUI != null) {
                    iRefreshUI.updatePlayStatus(PlayState.ERROR);
                }
                onPlayStateChanged$Error();
                break;
            case PlayState.SEEK_COMPLETED:
                if (iRefreshUI != null) {
                    iRefreshUI.updateSeekTime((int) getProgress(), (int) getDuration());
                }
                break;
            default:
                if (iRefreshUI != null) {
                    iRefreshUI.updatePlayStatus(PlayState.PAUSE);
                }

                break;
        }
    }

    private void onPlayStateChanged$Complete() {
        LogUtil.i(TAG, "onPlayStateChanged$Complete");
        int playMode = VideoPreferUtils.getPlayMode();
        if (playMode == PlayMode.LOOP) {
            //列表循环
            //需要播放下一个
            playNext();
        } else {
            //单曲循环
            clearPlayedMediaInfo();
            play(mListPrograms.get(mPlayPos).getMediaUrl());
        }
    }

    private void onPlayStateChanged$Error() {
        //遇到文件错误，播放下一个文件
        LogUtil.i(TAG, "this file is error");
        if (!EmptyUtil.isEmpty(mListPrograms)) {
            playNext();
        }
    }

    @Override
    public void onProgressChanged(String s, int i, int i1) throws RemoteException {
        if (iRefreshUI != null) {
            iRefreshUI.onVideoProgressChanged(s, i, i1);
        }

    }

    //=== = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
    public void savePlayInfo() {
        if (isPlaying() && getProgress() > 0) {
            String currMediaUrl = mVideoTextureView.getMediaPath();
            if (!EmptyUtil.isEmpty(currMediaUrl)) {
                savePlayMediaInfo(currMediaUrl, (int) getProgress());
            }
        }
    }

    protected void execPlay(String mediaUrl) {
        PlayEnableController.pauseByUser(false);
        execPlay(getPlayPosByMediaUrl(mediaUrl));
    }

    public void execPlay(int playPos) {
        LogUtil.d(TAG, "execPlay( " + playPos + " )");
        String errorinfo = "";
        PlayEnableController.pauseByUser(false);
        if (mListPrograms.size() == 0 || playPos >= mListPrograms.size() || mVideoTextureView == null) {
            if (mListPrograms.size() == 0) {
                errorinfo = "mListPrograms.size() is 0";
            } else if (playPos >= mListPrograms.size()) {
                errorinfo = "playPos >mListPrograms.size()";
            } else if (mVideoTextureView == null) {

            }
            LogUtil.e(TAG, "Error1:Cannot play because is  =" + errorinfo);
            return;
        }
        mPlayPos = playPos;
        ProVideo toPlayProgram = mListPrograms.get(mPlayPos);
        if (toPlayProgram == null || EmptyUtil.isEmpty(toPlayProgram.getMediaUrl())) {
            LogUtil.e(TAG, "Error2:toPlayProgram is null");
            return;
        }
        //保存 播放
        saveTargetMediaPath(toPlayProgram.getMediaUrl());
        mTargetAutoSeekProgress = getLastProgress();//-1
        mVideoTextureView.setVisibility(View.INVISIBLE);
        //保存播放的目标对象
        mVideoTextureView.setTag(toPlayProgram);
        mVideoTextureView.setMediaPath(toPlayProgram.getMediaUrl());
        mVideoTextureView.setVisibility(View.VISIBLE);

        play();
        makeScreenOn(true);

    }

    //获取播放视频在列表中的位置
    protected int getPlayPosByMediaUrl(String mediaUrl) {
        int playPos = -1;
        for (int idx = 0; idx < mListPrograms.size(); idx++) {
            ProVideo proVideo = mListPrograms.get(idx);
            if (proVideo.getMediaUrl().equals(mediaUrl)) {
                playPos = idx;
                break;
            }
        }
        if (playPos < 0) {
            playPos = 0;
            clearPlayedMediaInfo();
        }
        return playPos;
    }

    public void autoPlay() {
        LogUtil.i(TAG, "autoPlay() ==>>" + "\n" /*+ LogUtil.getStackTrace()*/);
        String path = getLastMediaPath();
        LogUtil.i(TAG, "autoPlay ::path =" + path + "/n" + "isExit =" + UdiskUtil.isFile(path));
        if (UdiskUtil.isFile(path) && mListPrograms.size() > 0) {
            LogUtil.i(TAG, "the this path is  exist");
            String mediaUrl = PlayDataCache.getMediaUrlToPlay();
            mListPrograms = (ArrayList)PlayDataCache.getMediasToPlay();
            LogUtil.i(TAG, "mListPrograms size =" + mListPrograms.size());
            if (mediaUrl != null && mListPrograms.size() > 0) {
                if (!EmptyUtil.isEmpty(mListPrograms)) {
                    execPlay(mediaUrl);
                    LogUtil.i(TAG, "execPlay()　mediaUrl　＋" + mediaUrl);
                }
            }
            return;
        }else {
            if (mListPrograms.size() > 0) {
                execPlay(mListPrograms.get(0).getMediaUrl());
            }
        }
    }


    /**
     * 获取视频文件
     *
     * @param type
     * @param sortBy
     * @param params
     *
     * @return
     */
    public List getAllMedias(int type, int sortBy, String[] params) {
        if (mEgarApiScanner != null) {
            return mEgarApiScanner.getAllMedias(type, sortBy, params);
        }
        return null;
    }

    public List getFilterFolders(int i) {
        if (mEgarApiScanner != null) {
            return mEgarApiScanner.getFilterFolders(i);
        }
        return null;
    }

    public List getMediasByColumns(int i, Map<String, String> map, String s) {
        if (mEgarApiScanner != null) {
            return mEgarApiScanner.getMediasByColumns(i, map, s);
        }
        return null;
    }

    /**
     * 根据path 获取集合中的position
     *
     * @param mediaUrl
     *
     * @return
     */
    public int getPosAtList(String mediaUrl) {
        int pos = -1;
        try {
            if (mListPrograms != null) {
                int loop = mListPrograms.size();
                for (int idx = 0; idx < loop; idx++) {
                    ProVideo media = mListPrograms.get(idx);
                    if (TextUtils.equals(media.getMediaUrl(), mediaUrl)) {
                        pos = idx;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "ERROR-getPosAtList(" + mediaUrl + ") :: " + e.getMessage());
            e.printStackTrace();
        }
        return pos;
    }

    public ProVideo getCurrProVideo() {

        try {
            String currMediaUrl = getCurrMediaPath();
            for (ProVideo program : mListPrograms) {
                if (program.getMediaUrl().equals(currMediaUrl)) {
                    return program;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG + "_getCurrProgram()", e.getMessage());
        }
        return null;
    }


    //=====================================================
    public void LoadFristData() {
        Log.i(TAG, "refreshData()");
        if (mLoadLocalMediasTask != null) {
            mLoadLocalMediasTask.cancel(true);
            mLoadLocalMediasTask = null;
        }
        mLoadLocalMediasTask = new LoadLocalMediasTask();
        mLoadLocalMediasTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    class LoadLocalMediasTask extends AsyncTask<Object, Integer, List<ProVideo>> {
        @Override
        protected List<ProVideo> doInBackground(Object... objects) {
            try {
                mListPrograms.clear();
                mListPrograms.addAll(getAllMedias(MediaType.VIDEO, FilterType.MEDIA_NAME, null));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return mListPrograms;
        }


        @Override
        protected void onPostExecute(List<ProVideo> videos) {
            super.onPostExecute(videos);
            LogUtil.i(TAG, "videos size =" + videos.size());
            PlayDataCache.cache(getLastMediaPath(), mListPrograms);
            autoPlay();
        }
    }
}
