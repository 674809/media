package com.egar.usbvideo.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.egar.ICarSpeed;
import android.egar.MediaStatus;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egar.mediaui.App;
import com.egar.mediaui.Icallback.IFinishActivity;
import com.egar.mediaui.Icallback.IMediaBtuClick;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.util.LogUtil;
import com.egar.mediaui.view.MyButton;
import com.egar.usbmusic.utils.UdiskUtil;
import com.egar.usbvideo.engine.AudioPresent;
import com.egar.usbvideo.engine.MediaLightModeController;
import com.egar.usbvideo.interfaces.IAudioFocusListener;
import com.egar.usbvideo.interfaces.IRefreshUI;
import com.egar.usbvideo.present.VideoPresent;
import com.egar.usbvideo.utils.VideoPreferUtils;
import com.egar.usbvideo.view.PanelTouchImpl;
import com.egar.usbvideo.view.SeekBarImpl;
import com.egar.usbvideo.view.VideoTextureView;

import java.io.File;

import juns.lib.java.utils.date.DateFormatUtil;
import juns.lib.media.bean.ProVideo;
import juns.lib.media.flags.PlayMode;
import juns.lib.media.flags.PlayState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/12/12 13:48
 * @see {@link }
 */
public class VideoPlayFragment extends Fragment implements View.OnClickListener,
        IAudioFocusListener, IMediaBtuClick, IRefreshUI, IFinishActivity, MainPresent.ICarSpeedListener {

    private String TAG = "VideoPlayFragment";
    private MainActivity mMainActivity;
    private View mView, mLayoutRoot, mLayoutTop, mCoverPanel, mControlPanel, mLayoutWarning;
    private ImageView mArrowRight, mArrowLeft;
    private ImageView mImgPlayPre, mImgPlay, mImgPlayNext;
    private TextView mTvFolder, mTvPosition, mTvName, mTvStartTime, mTvEndTime;
    private MyButton mBtPlayModeSet, mBTList;
    private PanelTouchResp mPanelTouchResp;
    private RelativeLayout mRLayoutPlayerBorder, mRLayoutPlay, mRLayoutUdisk;
    private VideoTextureView mVideoPlayer;
    private VideoPresent mVideoPresent;
    public MediaLightModeController mLightModeController;
    private MediaLightModeOnChange mMediaLightModeOnChange;
    private AudioPresent mAudioPresent;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;
    private VideoFoldersFragment mFoldersFragment;
    private SeekBarImpl mSeekBar;
    private SeekBarOnChange mSeekBarOnChange;
    private SeekOnTouch mSeekOnTouch;
    private static Handler handler = new Handler();
    private boolean isFinish = false;
    private BaseUsbFragment mUsbFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) getActivity();
    }

    public void onWindowChangeFull() {
        LogUtil.i(TAG, "onWindowChangeFull");
        if (mVideoPresent != null) {
            ProVideo video = mVideoPresent.getCurrProVideo();
            scaleScreen(video);
        }
    }


    public void onWindowChangeHalf() {
        LogUtil.i(TAG, "onWindowChangeHalf");
        if (mVideoPresent != null) {
            ProVideo video = mVideoPresent.getCurrProVideo();
            scaleScreen(video);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.usb_video_frag_play, container, false);
        initView();
        return mView;
    }

    private void initView() {
        LogUtil.i(TAG, "initView");
        mLayoutRoot = findViewById(R.id.v_root);
      //  mLayoutTop = findViewById(R.id.layout_top);
        mArrowLeft = (ImageView) findViewById(R.id.v_arrow_left);
        mArrowRight = (ImageView) findViewById(R.id.v_arrow_right);

        mCoverPanel = findViewById(R.id.vv_cover);

        mControlPanel = findViewById(R.id.v_control_panel);

        /*mTvFolder = (TextView) findViewById(R.id.v_folder_name);
        mTvFolder.setText("mTvFolder");*/

        mTvPosition = (TextView) findViewById(R.id.v_sort);
        mTvPosition.setText("0/0");

        mTvName = findViewById(R.id.v_name);
        mTvName.setText("name");
        // Cover panel
        PanelTouchImpl mPanelTouchImpl = new PanelTouchImpl();
        mPanelTouchImpl.init(mMainActivity);
        mPanelTouchImpl.addCallback((mPanelTouchResp = new PanelTouchResp()));
        mCoverPanel.setOnTouchListener(mPanelTouchImpl);

        mRLayoutPlayerBorder = (RelativeLayout) findViewById(R.id.rl_vv_border);
        mVideoPlayer = findViewById(R.id.vv_player);
        mVideoPlayer.setKeepScreenOn(true);
        mVideoPlayer.setDrawingCacheEnabled(false);


        mSeekBar = findViewById(R.id.seekbar);
        mSeekBarOnChange = new SeekBarOnChange();
        mSeekBar.setOnSeekBarChangeListener(mSeekBarOnChange);
        mSeekOnTouch = new SeekOnTouch();
        mSeekBar.setOnTouchListener(mSeekOnTouch);

        mTvStartTime = findViewById(R.id.tv_play_start_time);
        mTvEndTime = findViewById(R.id.tv_play_end_time);

        mImgPlayPre = findViewById(R.id.iv_play_pre);
        mImgPlayPre.setOnClickListener(this);

        mImgPlay = findViewById(R.id.iv_play);
        mImgPlay.setOnClickListener(this);

        mImgPlayNext = findViewById(R.id.iv_play_next);
        mImgPlayNext.setOnClickListener(this);

        mBtPlayModeSet = (MyButton) findViewById(R.id.iv_play_mode_set);
        mBtPlayModeSet.setOnClickListener(this);

        mBTList = (MyButton) findViewById(R.id.v_list);
        mBTList.setOnClickListener(this);

        mRLayoutUdisk = findViewById(R.id.layout_udisk);
        mRLayoutPlay = findViewById(R.id.layout_play);

        mLayoutWarning = findViewById(R.id.layout_warning);
        mLayoutWarning.setVisibility(View.GONE);
        updatePlayModeUI();
    }

    public void onPageLoadStart() {
        LogUtil.i(TAG, "onPageLoadStart :isAdd ="+isAdded());
        if (mAudioPresent != null) {
            if (!mAudioPresent.isAudioFocuseGained()) {
                mAudioPresent.registerAudioFocus(1);
            }
        }
        mUsbFragment = (BaseUsbFragment)getParentFragment();
        if (mVideoPresent != null) {
            ProVideo video = mVideoPresent.getCurrProVideo();
            scaleScreen(video);
        }
        //baseFragment 类统一处理
       boolean ismount = UdiskUtil.isHasSupperUDisk(App.getContext());
        upDataUi(ismount);
        if(ismount){
             init();
        }
        mMainActivity.getMainPresent().setCarSpeedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG,"onResume isAdd ="+isAdded());
    }

    //=====================================封装 api ==============================================

    /**
     * 初始化视频播放逻辑
     */
    private void init() {
        LogUtil.i(TAG, "init");
        if (mVideoPresent == null) {
            mVideoPresent = VideoPresent.getInstance();
            mVideoPresent.init(mVideoPlayer);
        }else {
            mVideoPresent.initScanService();
        }
        initRegiestUIState();
    }

    /**
     * 初始化状态改变监听
     */
    public void initRegiestUIState() {
        LogUtil.i(TAG,"initRegiestUIState");
        if (mAudioPresent == null) {
            mAudioPresent = new AudioPresent();
            mAudioPresent.setAudioFocusListener(this);
        }

        if (mVideoPresent == null) {
            LogUtil.i(TAG, "mVideoPlayPresent  = null");
            return;
        }
        //Ui状态注册
        if(mLightModeController == null){
            mLightModeController = new MediaLightModeController();
            if(mMediaLightModeOnChange == null){
                mMediaLightModeOnChange = new MediaLightModeOnChange();
            }
            mLightModeController.addModeListener(mMediaLightModeOnChange);
            mLightModeController.resetLightMode();
        }
        mMainActivity.setFinishActivitListener(this);
        mVideoPresent.setRefreshUI(this);
        ((BaseUsbFragment) mMainActivity.getMainPresent().getCurrenFragmen(Configs.PAGE_INDX_USB)).setMediaBtnClickListener(this);

    }

    /**
     * 关闭应用
     */
    public void stopPlay() {
        if(mMainActivity !=null){
            if(mLightModeController !=null){
                mLightModeController.addModeListener(null);
                mLightModeController.destroy();
                mLightModeController.makeLightOn();
                mLightModeController = null;
                if(mSeekBar !=null){
                    mSeekBar.setOnTouchListener(null);
                }
            }
            BaseUsbFragment baseUsbFragment = ((BaseUsbFragment) mMainActivity.getMainPresent().getCurrenFragmen(Configs.PAGE_INDX_USB));
            if(baseUsbFragment !=null){
                baseUsbFragment.removeMediaBtnClick();
            }
        }
        try {
            if (mFragmentManager != null) {
                if (isFinish) {
                    mFragmentManager.getFragments().clear();
                } else {
                    mFragmentManager.popBackStack();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (mVideoPresent != null) {
            LogUtil.i(TAG, "stopPlay");
            mVideoPresent.release();
            mVideoPresent.removeRefreshUI();
            mVideoPresent.removeScanListener();
            mVideoPresent.removeVolumeFadeController();
            mVideoPresent = null;
            mMainActivity.removeFinishActivitListener();
            handler.removeCallbacksAndMessages(null);
        }
        if(mAudioPresent !=null){
            mAudioPresent.removeAudioFocusListener();
        }
        mMainActivity.getMainPresent().setCarSpeedListener(null);
        mMainActivity.showNativigtion();
    }

    private void updatePlayModeUI() {
        int playMode = VideoPreferUtils.getPlayMode();
        if (playMode == PlayMode.SINGLE) {
            mBtPlayModeSet.setImageResource(R.drawable.repeat_ico);
        } else if (playMode == PlayMode.LOOP) {
            mBtPlayModeSet.setImageResource(R.drawable.usb_loop);
        }
    }


    /**
     * 打开文件列表页面
     */
    private void openVideoFolerFragment() {
        mFragmentManager = getFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();
        if (mFoldersFragment == null) {
            mFoldersFragment = new VideoFoldersFragment();
        }

        mTransaction.hide(this);
        mTransaction.add(R.id.video_content, mFoldersFragment,"videoFolder");
        mTransaction.addToBackStack(null);
        mTransaction.commitAllowingStateLoss();
        ((BaseUsbFragment)getParentFragment()).setIndicatorVisib(false);

    }

    //重置进度条
    private void resetSeekBar() {
        try {
            LogUtil.d(TAG, "resetSeekBar()");
            if (mSeekBar != null) {
                mSeekBar.setEnabled(true);
                //mSeekBar.setMax((int) mListPrograms.get(mPlayPos).duration);
                mSeekBar.setProgress(0);
            }
        } catch (Exception e) {
            LogUtil.e(TAG + "_resetSeekBar", e.getMessage());
        }
    }


    /**
     * 屏幕缩放
     *
     * @param media
     */
    private void scaleScreen(final ProVideo media) {
        LogUtil.i(TAG, "scaleScreen");
        if (media == null) {
            return;
        }
        if (media.getWidth() == 0 || media.getHeight() == 0) {
            ProVideo.parseMediaScaleInfo(mMainActivity, media);
        }
        LogUtil.d(TAG, "media :: W = " + media.getWidth() + "H =" + media.getHeight());
        if (media.getWidth() == 0 || media.getHeight() == 0) {
            return;
        }
        if (mLayoutRoot == null) {
            return;
        }
        //
        mLayoutRoot.post(new Runnable() {
            @Override
            public void run() {
                int rootW = mLayoutRoot.getWidth();
                int rootH = mLayoutRoot.getHeight();//获取画布大小
                LogUtil.d(TAG, "media ::layout W = " + rootW + "layout H =" + rootH );
                //  LogUtil.d(TAG, "root :: resolution - " + rootW + "x" + rootH);

                //计算视频画面的大小，与视频原来的大小和屏幕的大小有关
                int targetW = 0, targetH = 0;
                double mediaRate = ((double) media.getWidth()) / media.getHeight(); //获取视频宽高比例
                double rootRate = ((double) rootW) / rootH;//获取画布的宽高比例
                if (mediaRate > rootRate) {
                    targetW = rootW;
                    targetH = (int) (targetW / mediaRate);
                    LogUtil.d(TAG, "视频画布大 target1 :: targetW = " + targetW + ">targetW =" + targetH);

                } else if (mediaRate < rootRate) {
                    targetH = rootH;
                    targetW = (int) (targetH * mediaRate);
                     LogUtil.d(TAG, " 视频画布小 target1 :: targetW = " + targetW + ">targetW =" + targetH);
                }

                if (targetW > 0 && targetH > 0) {
                    ViewGroup.LayoutParams lps = mRLayoutPlayerBorder.getLayoutParams();
                    lps.width = targetW;
                    lps.height = targetH;
                    mRLayoutPlayerBorder.setLayoutParams(lps);
                }
            }
        });

    }
//=======================================end =======================================================


    public void onPageLoadStop() {
        Log.i(TAG,"onPageLoadStop");
        stopPlay();
    }

    /**
     * U盘挂载事件
     *
     * @param state 状态 mount guaz ,unmount 卸载
     */
    public void onUdiskStateChange(boolean state) {
        LogUtil.i(TAG, "onUdiskStateChange =" + state +"::isAdd ="+isAdded());
//        Toast.makeText(App.getContext(),"state = "+state,Toast.LENGTH_SHORT).show();
        boolean ismount = UdiskUtil.isHasSupperUDisk(App.getContext());
       // updataUdiskPage(state);
       if (state) {//挂载
            if (isAdded()) {
                init();
            }
        } else  { //卸载
            LogUtil.i(TAG, "停止播放");
           if(mFoldersFragment !=null){
               mFragmentManager.popBackStack();
           }
           if(mLightModeController !=null){
               mLightModeController.keepLightOn();
           }
            if (isAdded()) {
                stopPlay();
            }
        }
       upDataUi(ismount);
    }

    private void upDataUi(boolean isShow) {
        if(isShow){
            mRLayoutUdisk.setVisibility(View.GONE);
            mRLayoutPlay.setVisibility(View.VISIBLE);
        }else {
            mRLayoutUdisk.setVisibility(View.VISIBLE);
            mRLayoutPlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_list:
                openVideoFolerFragment();
                if(mLightModeController !=null){
                    mLightModeController.destroy();
                    mMainActivity.showNativigtion();
                }
                break;
            case R.id.iv_play_pre:
                if (mVideoPresent != null) {
                    mVideoPresent.playPrev();
                }
                break;
            case R.id.iv_play:
                if (mVideoPresent != null) {
                    if (mVideoPresent.isPlaying()) {
                        mVideoPresent.pause();
                    } else {
                        mVideoPresent.resume();
                    }
                }
                break;
            case R.id.iv_play_next:
                if (mVideoPresent != null) {
                    mVideoPresent.playNext();
                }
                break;
            case R.id.iv_play_mode_set:
                if (mVideoPresent != null) {
                    mVideoPresent.switchPlayMode();
                }
                break;
        }
    }

    @Override
    public void onFinishActivity() {
        LogUtil.i(TAG, "onFinishActivity");
        isFinish = true;
        stopPlay();
    }


    //========================Audio  start ======================================
    @Override
    public void onAudioFocusDuck() {

    }

    @Override
    public void onAudioFocusTransient() {

    }

    @Override
    public void onAudioFocusGain() {
        LogUtil.i(TAG, "onAudioFocusGain");
    }

    @Override
    public void onAudioFocusLoss() {
        LogUtil.i(TAG, "onAudioFocusLoss");
        if (mVideoPresent != null) {
            mVideoPresent.pause();
        }
    }

    @Override
    public void onAudioFocus(int flag) {
        LogUtil.i(TAG, "onAudioFocus=" + flag);
        if (flag != 1) {
            if (mVideoPresent != null) {
                mVideoPresent.pause();
            }
        }
    }

    //========================Audio   end====================================
    //=======================mediaButton  start =============================================
    @Override
    public void onNextLongClick() {
        Log.i(TAG,"onNextLongClick");
    }

    @Override
    public void onNextClick() {
        Log.i(TAG,"onNextClick");
    }

    @Override
    public void onPrevLongClick() {

    }

    @Override
    public void onPrevClick() {

    }
    //=======================mediaButton  end =============================================

    //========================= 播放状态  start ==============================================
    @Override
    public void onResetSeekBar() {
        resetSeekBar();
    }

    @Override
    public void updatePlayStatus(int state) {
        ProVideo currProgram = mVideoPresent.getCurrProVideo();
        switch (state) {
            case PlayState.PLAY:
                mImgPlay.setImageResource(R.drawable.btn_op_pause_selector);

                break;
            case PlayState.PAUSE:
                mImgPlay.setImageResource(R.drawable.btn_op_play_selector);
                break;
        }
        int playMode = mVideoPresent.getPlayMode();
        switch (playMode) {
            case PlayMode.LOOP:
                mBtPlayModeSet.setImageResource(R.drawable.usb_loop);
                break;
            case PlayMode.SINGLE:
                mBtPlayModeSet.setImageResource(R.drawable.repeat_ico);
                break;
        }
        if(currProgram == null){
            mMainActivity.getMainPresent().setMediaStatusInfo(
                    MediaStatus.MEDIA_TYPE_LOCALVIDEO,
                    state == PlayState.PLAY ? MediaStatus.MEDIA_STATUS_PLAYING :MediaStatus.MEDIA_STATUS_PAUSE,
                    "No MedaiInfo" );
        }else{
            mMainActivity.getMainPresent().setMediaStatusInfo(
                    MediaStatus.MEDIA_TYPE_LOCALVIDEO,
                    state == PlayState.PLAY ? MediaStatus.MEDIA_STATUS_PLAYING :MediaStatus.MEDIA_STATUS_PAUSE,
                    currProgram.getTitle() );
        }


    }

    @Override
    public void onPlayStateChanged$Play() {
        LogUtil.d(TAG, "onNotifyPlayState$Play()");
        ProVideo currProgram = mVideoPresent.getCurrProVideo();
        if (currProgram == null) {
            LogUtil.d(TAG, "currProgram is null");
            return;
        }
        File file = new File(currProgram.getMediaUrl());
        if (file.exists()) {
            File folder = file.getParentFile();
            if (folder != null) {
             //   mTvFolder.setText(folder.getName());
            }
            mTvName.setText(currProgram.getTitle());

            // Position
            String formatStr = getString(R.string.video_pos_str);
            String currPosStr = String.valueOf((mVideoPresent.getCurrIdx() + 1));
            String totalCountStr = String.valueOf(mVideoPresent.getTotalCount());
            mTvPosition.setText(String.format(formatStr, currPosStr, totalCountStr));

            scaleScreen(currProgram);

        }
        mMainActivity.getMainPresent().setMediaStatusInfo(
                MediaStatus.MEDIA_TYPE_LOCALVIDEO,
                MediaStatus.MEDIA_STATUS_PLAYING,
                currProgram.getTitle() );

    }

    @Override
    public void onPlayStateChanged$Prepared() {
        LogUtil.i(TAG, "onPlayStateChanged$Prepared =" + mVideoPresent.mTargetAutoSeekProgress);
        mSeekBar.setMax((int) mVideoPresent.getDuration());
        if (mVideoPresent.mTargetAutoSeekProgress > 0 && mVideoPresent.mTargetAutoSeekProgress < mSeekBar.getMax()) {
            mVideoPresent.seekTo((int) mVideoPresent.mTargetAutoSeekProgress);
            mVideoPresent.mTargetAutoSeekProgress = -1;
        }
    }

    @Override
    public void updateSeekTime(final int progress, final int duration) {
        //此处出现了TextView无法setText赋值的问题
        if (mTvStartTime != null) {
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvStartTime.setText(DateFormatUtil.getFormatHHmmss(progress));
                }
            });

        }
        if (mTvEndTime != null) {
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvEndTime.setText(DateFormatUtil.getFormatHHmmss(duration));
                }
            });

        }
    }

    @Override
    public void onVideoProgressChanged(String s, int progress, int duration) {
        mSeekBar.setMax(duration);
        //   LogUtil.i(TAG, "onProgressChanged=" + progress);
        mSeekBar.setMax(duration);
        mSeekBar.setProgress(progress);//更新进度条
        updateSeekTime(progress, duration);
        mVideoPresent.savePlayInfo();
    }

    @Override
    public void onPlayModeChange() {
        LogUtil.i(TAG, "onPlayModeChange");
        updatePlayModeUI();
    }

    @Override
    public void onRespScanState(int state) {
        Log.i(TAG,"扫描信息 ="+state);
      /*  mTvFolder.setText(getResources().getString(R.string.video_scaning));
        if (MediaScanState.SCAN_VIDEO_END == state) {
            mTvFolder.setText(getResources().getString(R.string.video_scan_end));
        }*/
    }


    public void onBack() {

    }

    @Override
    public void onCarSpeedchange() {

    }


    //========================= 播放状态  end ==============================================


    //================================PanelTouchResp start=====================================
    private class PanelTouchResp implements PanelTouchImpl.PanelTouchCallback {
        private Handler mmHandler = new Handler();
        private boolean mIsSeekRunning = false;

        @Override
        public void onActionDown() {
            LogUtil.i(TAG, "onActionDown");
            cancelRunnable();
        }

        @Override
        public void onActionUp() {
            LogUtil.i(TAG, "onActionUp");
            if (mIsSeekRunning) {
                delayInvisible();
            }
        }

        @Override
        public void onSingleTapUp() {
            LogUtil.i(TAG, "onSingleTapUp");
            if (mLightModeController != null) {
                mLightModeController.switchLightMode();
            }
        }

        @Override
        public void onPrepareAdjustBrightness() {
            LogUtil.i(TAG, "onPrepareAdjustBrightness");
        }

        @Override
        public void onAdjustBrightness(double rate) {
            LogUtil.i(TAG, "onAdjustBrightness");
        }

        @Override
        public void onPrepareAdjustVol() {
            LogUtil.i(TAG, "onPrepareAdjustVol");
        }

        @Override
        public void onAdjustVol(int vol, int maxVol) {
            LogUtil.i(TAG, "onAdjustVol");
        }

        @Override
        public void onPrepareAdjustProgress() {
            LogUtil.i(TAG, "onPrepareAdjustProgress");
        }

        @Override
        public void onAdjustProgress(int direction, int progressDelta) {

        }

        @Override
        public void seekProgress(int direction, int progressDelta) {
            LogUtil.i(TAG, "seekProgress" + direction); //0 right 1 left
            if(mVideoPresent ==null){
                LogUtil.i(TAG, "mVideoPresent is null"); //0 right 1 left
                return;
            }
            switch (direction) {
                case 0:
                    //
                    mArrowLeft.setVisibility(View.INVISIBLE);
                    mArrowRight.setVisibility(View.VISIBLE);
                    mArrowRight.invalidate();
                    //

                    long targetProgress = mVideoPresent.getProgress() + 15 * 1000;
                    if (targetProgress > mVideoPresent.getDuration()) {
                        targetProgress = mVideoPresent.getDuration();
                    }
                    mVideoPresent.seekTo((int) targetProgress);
                    break;
                case 1:
                    //
                    mArrowLeft.setVisibility(View.VISIBLE);
                    mArrowLeft.invalidate();
                    mArrowRight.setVisibility(View.INVISIBLE);
                    //
                    targetProgress = mVideoPresent.getProgress() - 15 * 1000;
                    if (targetProgress < 0) {
                        targetProgress = 0;
                    }
                    mVideoPresent.seekTo((int) targetProgress);
                    break;
            }
            mIsSeekRunning = true;
            delayInvisible();
        }

        void delayInvisible() {
            cancelRunnable();
            mmHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsSeekRunning = false;
                    mArrowLeft.setVisibility(View.INVISIBLE);
                    mArrowRight.setVisibility(View.INVISIBLE);
                }
            }, 1000);
        }

        void cancelRunnable() {
            mmHandler.removeCallbacksAndMessages(null);
        }

        void destroy() {
            cancelRunnable();
        }
    }

    //=========================================== end =====================================


    //=====================================显示与隐藏操作按键=====================================
    private class MediaLightModeOnChange implements MediaLightModeController.MediaLightModeListener {
        @Override
        public void onLightOn() {
            LogUtil.d(TAG, "onLightOn()");
            boolean isMount = UdiskUtil.isHasSupperUDisk(App.getContext());
            //isMount = true;
            if (isMount) {
                mControlPanel.setVisibility(View.VISIBLE);
                ((BaseUsbFragment)getParentFragment()).setIndicatorVisib(true);
                mMainActivity.showNativigtion();
            } else {
                mControlPanel.setVisibility(View.GONE);
                ((BaseUsbFragment)getParentFragment()).setIndicatorVisib(false);
            }
            // CommonUtils.setNavigationBar(VideoPlayerActivity.this, 1);
        }

        @Override
        public void onLightOff() {
            LogUtil.d(TAG, "onLightOff()");

            mControlPanel.setVisibility(View.GONE);
            if (isAdded()) {
                ((BaseUsbFragment)getParentFragment()).setIndicatorVisib(false);
                if(mUsbFragment.getUsbChildFragment() == 1){
                    mMainActivity.hideNatvigtion();
                }

            }
            //  CommonUtils.setNavigationBar(VideoPlayerActivity.this, 0);
        }
    }

    //================================== seek bar========================================
    public class SeekBarOnChange implements SeekBar.OnSeekBarChangeListener {

        int mmProgress = 0;
        boolean mmIsTrackingTouch = false;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            LogUtil.d(TAG, "SeekBarOnChange - onStartTrackingTouch(SeekBar)");
            mmIsTrackingTouch = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            LogUtil.d(TAG, "SeekBarOnChange - onStopTrackingTouch(SeekBar)");
            if (mmIsTrackingTouch) {
                mmIsTrackingTouch = false;
                mVideoPresent.seekTo(mmProgress);
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //  LogUtil.d(TAG, "SeekBarOnChange - onProgressChanged(SeekBar," + progress + "," + fromUser + ")");
            if (fromUser) {
                mmProgress = progress;
            }
        }

        boolean isTrackingTouch() {
            return mmIsTrackingTouch;
        }
    }

    //=======================     end    ==================

    //========================SeekOnTouch   ==========================
    public class SeekOnTouch implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    LogUtil.d(TAG, "mSeekBar -> SeekOnTouch -> ACTION_DOWN =");
                    if(mLightModeController !=null){
                        mLightModeController.keepLightOn();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtil.d(TAG, "mSeekBar -> SeekOnTouch -> ACTION_UP");
                    if(mLightModeController !=null){
                        mLightModeController.resetLightMode();
                    }
                    break;
            }
            return false;
        }
    }


    //=======================================================================
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.i(TAG, "onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPageLoadStop();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG,"onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");

    }

    public <T extends View> T findViewById(int id) {
        return (T) mView.findViewById(id);
    }
}
