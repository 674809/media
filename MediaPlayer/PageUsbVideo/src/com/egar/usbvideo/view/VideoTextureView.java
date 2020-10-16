package com.egar.usbvideo.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.egar.mediaui.App;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbvideo.interfaces.PlayDelegate;
import com.egar.usbvideo.present.VideoPresent;
import com.egar.usbvideo.utils.DateFormatUtil;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import juns.lib.media.flags.PlayState;
import juns.lib.media.player.MediaUtils;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/11/13 16:37
 * @see {@link }
 */
public class VideoTextureView extends TextureView {
    private String TAG = "VideoTexture";

    /**
     * 媒体播放器
     */
    private MediaPlayer mMediaPlayer;

    /**
     * 画布容器 / 目标输出图像画布容器
     */
    private SurfaceTexture mSurfaceTexture;

    // 正在播放的媒体文件路径
    private String mMediaPath = "";
    // Play listener
    private PlayDelegate mPlayDelegate;
    // 进度计时器
    private ProgressTimer mProgressTimer;

    //  private OnProgressChangeListener mProgressListener;
    // 播放器状态
    private int mPlayStatus = PlayState.NONE;

    private Handler mHandler = new Handler();


    /**
     * 播放器对象是否在执行Seek动作
     */
    private boolean mIsSeeking = false;

    public String getMediaPath() {
        return mMediaPath;
    }

    /**
     * 设置媒体播放路径
     */
    public void setMediaPath(String path) {
        mMediaPath = path;
        Log.i(TAG, "mMediaPath : " + mMediaPath);
    }

    public VideoTextureView(Context context) {
        super(context);
        init();
    }

    public VideoTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VideoTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init( ) {
        if (mProgressTimer == null) {
            mProgressTimer = new ProgressTimer();
        } else {
            mProgressTimer.cancel();
        }
        setSurfaceTextureListener(mSurfaceTextureListener);
    }


    TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            LogUtil.i(TAG, "onSurfaceTextureAvailable");
            mSurfaceTexture = surfaceTexture;
            if(mMediaPlayer !=null){
                play(false);
            }

         /*   if (mMediaPlayer != null) {
                mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
                return;
            }*/

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            LogUtil.i(TAG, "onSurfaceTextureDestroyed");
            if (mMediaPlayer != null) {
                mSurfaceTexture = null;
                mMediaPlayer.setSurface(null);
            }
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    /**
     * SurfaceTexture是否销毁了
     */
    public boolean isSurfaceDestroyed() {
        return (mSurfaceTexture == null);
    }

    /**
     * 执行播放流程，播放指定媒体文件
     */
    public void play(String mediaPath) {
        LogUtil.d(TAG, "^^ play(" + mediaPath + ") ^^");
        setMediaPath(mediaPath);
        play();
    }

    /**
     * 执行播放流程
     */
    public void play() {
        LogUtil.d(TAG, "^^ play() ^^");
        if (TextUtils.isEmpty(mMediaPath) || TextUtils.isEmpty(mMediaPath.trim())) {
        } else if (mPlayStatus == PlayState.PAUSE) {
            resume();
        } else {
            boolean isNewCreated = createMediaPlayer();
            if (isNewCreated) {
                mIsSeeking = false;//Seek动作执行完了
                play(true);
            } else {
                play(false);
            }
        }
        notifyPlayState(PlayState.PLAY);
    }

    /**
     * 从播放状态暂停
     */
    public void pause() {
        LogUtil.d(TAG, "^^ pause() ^^");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mProgressTimer.cancel();
            mMediaPlayer.pause();
            mPlayStatus = PlayState.PAUSE;
            notifyPlayState(PlayState.PAUSE);
        }
    }

    /**
     * 播放媒体
     */
    private void start() {
        LogUtil.d(TAG, "^^ start() ^^");
        if (mMediaPlayer != null && mSurfaceTexture != null) {
            LogUtil.d(TAG, "^^ start() ^^ -EXEC-");
            mMediaPlayer.start();
            mPlayStatus = PlayState.PLAY;
            mProgressTimer.scheduleRun();
            notifyPlayState(PlayState.PLAY);
        }
    }

    /**
     * 从暂停状态恢复播放
     */
    private void resume() {
        LogUtil.d(TAG, "^^ resume() ^^");
        if (mPlayStatus == PlayState.PAUSE) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    start();
                }
            }, 1000);
            notifyPlayState(PlayState.PLAY);
        }
    }

    /**
     * 播放视频
     * @param isJustPlay  是否重新初始化
     */
    private void play(boolean isJustPlay) {
        LogUtil.d(TAG, "^^ play(" + isJustPlay + ") ^^");
        try {
            if (mMediaPlayer != null && !TextUtils.isEmpty(mMediaPath)) {
                if (isJustPlay) {
                    mMediaPlayer.setSurface(new Surface(mSurfaceTexture));//设置屏幕
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            start();
                        }
                    }, 1000);
                    notifyPlayState(PlayState.PLAY);
                } else {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(mMediaPath);
                    mMediaPlayer.setSurface(new Surface(mSurfaceTexture));
                    mMediaPlayer.prepareAsync();
                    mPlayStatus = PlayState.PREPARED;
                    notifyPlayState(PlayState.PREPARED);
                }
            }
        } catch (Exception e) {
            LogUtil.d(TAG, "----Exception----" + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 释放MediaPlayer
     */
    public void release() {
        LogUtil.d(TAG, "^^ release() ^^");
        if (mMediaPlayer != null) {
            mProgressTimer.cancel();
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            setSurfaceTextureListener(null);
            mMediaPlayer = null;
            mPlayStatus = PlayState.NONE;
            notifyPlayState(PlayState.NONE);
            notifyPlayState(PlayState.RELEASE);
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    /**
     * 跳到某一时刻播放
     * @param pos
     */
    public void seekTo(int pos) {
        LogUtil.d(TAG, "^^ seekTo(" + pos + ") ^^");
        if (mMediaPlayer != null) {
            mIsSeeking = true;//Seek动作是异步的,此时表示开始执行seek动作
            mMediaPlayer.seekTo(pos);
        }
    }

    /**
     * 获取视频总时间长度
     * @return
     */
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获取当我播放position
     * @return
     */
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 判断是否播放
     * @return
     */
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    /**
     * 是否正在执行Seek动作
     * <p>
     * 实践中发现: 如果一个视频在某个时间段编码有异常,那么正好拖动到此事件段时,会导致系统卡死;
     * 为了在避免这种情况发生,需要有个标记为来判断此时是否正在执行Seek动作.
     * </P>
     */
    public boolean isSeeking() {
        return mIsSeeking;
    }

    public int getMediaDuration() {
        return DateFormatUtil.getIntSecondMsec(getDuration());
    }

    public int getMediaProgress() {
        return DateFormatUtil.getIntSecondMsec(getCurrentPosition());
    }

    /**
     * 创建媒体播放器，添加注册监听
     * @return
     */
    private boolean createMediaPlayer() {
        LogUtil.i(TAG, "createMediaPlayer");
        boolean isNewCreated = false;
        try {
            if (mMediaPlayer == null) {
                //Check null
                if (TextUtils.isEmpty(mMediaPath) || TextUtils.isEmpty(mMediaPath.trim())) {
                    return false;
                }
                //Check exist
                File mediaF = new File(mMediaPath);
                if (!mediaF.isFile() || !mediaF.exists()) {
                    return false;
                }
                // 创建
                mMediaPlayer = MediaPlayer.create(App.getContext(), Uri.parse(mMediaPath));
                if (mMediaPlayer == null) {
                    if (mOnErrorListener != null) {
                        mOnErrorListener.onError(null, MediaPlayer.MEDIA_ERROR_UNKNOWN, -1);
                    }
                    return false;
                } else {
                    isNewCreated = true;
                }
                LogUtil.i(TAG, "setListener");
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                // 设置监听加载
                mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                mMediaPlayer.setOnErrorListener(mOnErrorListener);
                mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
                mMediaPlayer.setOnInfoListener(mOnInfoListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i(TAG, "Exception" + e.toString());
            notifyPlayState(PlayState.ERROR);
        }
        return isNewCreated;
    }

    /**
     * 准备播放的监听回调
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            LogUtil.d(TAG, "onPrepared() -> [mStatus:" + mPlayStatus + "]");
            // 异步加载完成后，启动播放
            mPlayStatus = PlayState.PREPARED;
            notifyPlayState(PlayState.PREPARED);
            Long position = VideoPresent.getInstance().getLastProgress();
            Log.i(TAG,"prepare  getLastProgress ="+position +"    getCurrentPosition ="+getCurrentPosition());
            if( position <= getCurrentPosition()){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        start();
                    }
                }, 800);
            }else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        start();
                    }
                }, 1500);
            }
            if (mPlayStatus == PlayState.PREPARED || mPlayStatus == PlayState.PLAY) {

            }


        }
    };
    /**
     * 播放完成的监听回调
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mProgressTimer.cancel();
            LogUtil.d(TAG, " onCompletion() -> [mStatus:" + mPlayStatus + "]");
            mIsSeeking = false;//Seek动作执行完了
            if (mPlayStatus != PlayState.PREPARED) {
                notifyPlayState(PlayState.COMPLETE);
            }


        }
    };
    /**
     * 播放错误的监听回调
     */
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            LogUtil.d(TAG, " onError ->(mp," + what + "," + extra + ")");
            MediaUtils.printError(mediaPlayer, what, extra);

            //Cancel progress timer
            mProgressTimer.cancel();

            // Process Error
            boolean isProcessError = true;
            switch (what) {
                // 未发现该问题有何用处，暂不处理该错误
                case -38:
                    isProcessError = false;
                    break;

                //Media server died. In this case, the application must release the
                //MediaPlayer object and instantiate a new one.
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    release();
                    break;
            }
            if (isProcessError) {
                notifyPlayState(PlayState.ERROR);
            }

            return false;
        }
    };
    /**
     * seek 完成的监听回调
     */
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            LogUtil.d(TAG, "onSeekComplete() -> [mStatus:" + mPlayStatus + "]");
            mIsSeeking = false;//Seek动作执行完了
            if (mPlayStatus != PlayState.PREPARED) {
                notifyPlayState(PlayState.SEEK_COMPLETED);
            }

        }
    };
    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
            return false;
        }
    };

    /**
     * 设置渐变音量
     * @param leftVolume
     * @param rightVolume
     */
    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null) {
            try {
                LogUtil.d(TAG, "setVolume(leftVolume,rightVolume) -> [leftVolume:" + leftVolume + "; rightVolume:" + rightVolume
                        + "]");
                mMediaPlayer.setVolume(leftVolume, rightVolume);
            } catch (Throwable e) {
                // Logs.printStackTrace(TAG + "setVolume()", e);
                LogUtil.e(TAG, "setVolume()" + e.getMessage());
            }
        }
    }

    /**
     * 播放时间获取进度
     */
    private class ProgressTimer {
        //TAG
        final String MM_TAG = "ProgressTimer";
        ScheduledThreadPoolExecutor mmExecutor;

        ProgressTimer() {
        }

        void scheduleRun() {
            // 隔DELAY_PERIOD后开始执行任务，并且在上一次任务开始后隔REFRESH_PERIOD再执行一次
            cancel();
            Log.i(MM_TAG, "scheduleRun()");
            mmExecutor = new ScheduledThreadPoolExecutor(5);
            mmExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        int duration = getDuration();
                        int progress = getCurrentPosition();
                        mPlayDelegate.onProgressChanged(mMediaPath, progress, duration);
                    } catch (Exception e) {
                        Log.i(TAG, "EXCEPTION :: " + e.getMessage());
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        void cancel() {
            Log.i(MM_TAG, "cancel()");
            if (mmExecutor != null) {
                try {
                    mmExecutor.shutdown();
                    mmExecutor = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 设置播放监听
     * @param l
     */
    public void setPlayStateListener(PlayDelegate l) {
        this.mPlayDelegate = l;
    }

    /**
     * 通知播放器状态
     */
    private void notifyPlayState(int playState) {
        LogUtil.i(TAG, "notifyPlayState =" + playState);
        if (mPlayDelegate != null) {
            mPlayDelegate.onPlayStateChanged(playState);
        }
    }
}
