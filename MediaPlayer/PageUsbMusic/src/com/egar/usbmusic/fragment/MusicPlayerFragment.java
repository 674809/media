package com.egar.usbmusic.fragment;

import android.content.Context;
import android.egar.MediaStatus;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egar.mediaui.App;
import com.egar.mediaui.Icallback.IFinishActivity;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.util.LogUtil;
import com.egar.mediaui.util.date.DateFormatUtil;
import com.egar.mediaui.view.MyButton;
import com.egar.music.api.EgarApiMusic;
import com.egar.usbmusic.interfaces.IPlayerState;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.utils.AudioUtils;
import com.egar.usbmusic.utils.UdiskUtil;
import com.egar.usbvideo.engine.MediaLightModeController;
import com.egar.usbvideo.view.PanelTouchImpl;
import com.egar.usbvideo.view.SeekBarImpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import juns.lib.android.utils.Logs;
import juns.lib.java.utils.EmptyUtil;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.PlayMode;
import juns.lib.media.flags.PlayState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/12/11 10:19
 * @see {@link }
 */
public class MusicPlayerFragment extends Fragment implements View.OnClickListener, IPlayerState, IFinishActivity  {

    private String TAG = "MusicPlayerFragment";
    private MainActivity mMainActivity;
    private View mView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;
    private MusicFoldersFragment mFoldersFragment;
    private ImageView mBtPrev, mBtPalyOrPause, mBtNext;
    private MyButton mBtLoop, mBtFolder;
    private TextView mTvName, mTvSonger, mTvStartTime, mTvEndTime, mTvNoUsb;
    private SeekBarImpl mSeekbar;
    private ImageView mImgPhone;
    private LinearLayout mLayPlay ,mlyImgage;
    private RelativeLayout mLayUdisk;
    private SeekBarOnChange mSeekBarOnChange;
    private Bitmap bitmap;
    private boolean isFinish = false;
    private FileInputStream fileInputStream;
    private StringBuilder sbInfo  = new StringBuilder();;
    private String strTitle ="";
    public MediaLightModeController mLightModeController;
    private PanelTouchResp mPanelTouchResp;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) getActivity();
        LogUtil.i(TAG, "onAttach");
    }


    public void onWindowChangeFull() {
        LogUtil.i(TAG, "onWindowChangeFull");
        if (mFoldersFragment != null) {
            mFoldersFragment.onWindowChangeFull();
        }
        if (mlyImgage != null) {
            mlyImgage.setVisibility(View.VISIBLE);
        }
        updateAudioIcon();
    }


    public void onWindowChangeHalf() {
        LogUtil.i(TAG, "onWindowChangeHalf");
        if (mFoldersFragment != null) {
            mFoldersFragment.onWindowChangeHalf();
        }
        if (mlyImgage != null) {
            mlyImgage.setVisibility(View.GONE);
        }
        updateAudioIcon();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
    }

    public void onPageLoadStart() {
        MusicPresent.getInstance().setMusicPage(true);
        LogUtil.i(TAG, "onPageLoadStart");
        initMusic();
        boolean isPlayServiceConnected = MusicPresent.getInstance().isPlayServiceConnected();
        LogUtil.i(TAG, "isPlayServiceConnected =" + isPlayServiceConnected);
        if (isPlayServiceConnected) {
            MusicPresent.getInstance().focusPlayer();
            MusicPresent.getInstance().firstAutoPlay();
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        mMainActivity.setFinishActivitListener(MusicPlayerFragment.this);
        mMainActivity.getMainPresent().setMediaStatusInfo(
                    MediaStatus.MEDIA_TYPE_LOCALMUSIC,
                    MusicPresent.getInstance().isPlaying() ? MediaStatus.MEDIA_STATUS_PLAYING :MediaStatus.MEDIA_STATUS_PAUSE,
                    strTitle);
        boolean ismount = UdiskUtil.isHasSupperUDisk(App.getContext());
        Log.i(TAG,"ismount ="+ismount);
        upDateUdiskUi(ismount);
        updateAudioIcon();
    }

    private void initMusic() {
        MusicPresent.getInstance().initEgarMusicApi();
        MusicPresent.getInstance().setPlayerStateLinsteren(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.usb_music_frag_player, container, false);
        initView();
        return mView;

    }

    private void initView() {
        LogUtil.i(TAG, "initView");
        mBtFolder = findViewById(R.id.bt_folder);
        mBtFolder.setOnClickListener(this);

        mBtPrev = findViewById(R.id.bt_prev);
        mBtPrev.setOnClickListener(this);

        mBtNext = findViewById(R.id.bt_next);
        mBtNext.setOnClickListener(this);

        mBtPalyOrPause = findViewById(R.id.bt_palyOrPause);
        mBtPalyOrPause.setOnClickListener(this);

        mBtLoop = findViewById(R.id.bt_loop);
        mBtLoop.setOnClickListener(this);

        mTvName = findViewById(R.id.tv_name);
        mTvSonger = findViewById(R.id.tv_songer);
        mTvStartTime = findViewById(R.id.tv_start_time);
        mTvEndTime = findViewById(R.id.tv_end_time);
        mSeekbar = findViewById(R.id.seekbar);
        mImgPhone = findViewById(R.id.img_phone);
        mLayUdisk = findViewById(R.id.lay_udisk);
        mLayPlay = findViewById(R.id.lay_play);
        mTvNoUsb = findViewById(R.id.tv_no_usb);
        mlyImgage = findViewById(R.id.layout_imag);
        mSeekBarOnChange = new SeekBarOnChange();
        mSeekbar.setOnSeekBarChangeListener(mSeekBarOnChange);
        PanelTouchImpl mPanelTouchImpl = new PanelTouchImpl();
        mPanelTouchImpl.init(mMainActivity);
        mPanelTouchImpl.addCallback((mPanelTouchResp = new PanelTouchResp()));
        mLayPlay.setOnTouchListener(mPanelTouchImpl);
        //Ui状态注册
        if(mLightModeController == null){
            mLightModeController = new MediaLightModeController();

            mLightModeController.addModeListener(new MediaLightModeOnChange());
           // mLightModeController.resetLightMode();
        }

    }

    private void updateAudioIcon(){
        if(mMainActivity.getMainPresent().getActivityPosition(mMainActivity) == 0){
            Log.i(TAG,"imgphone visible");
            mImgPhone.setVisibility(View.VISIBLE);
        }else {
            mImgPhone.setVisibility(View.GONE);
            Log.i(TAG,"imgphone gone");
        }
    }

    //================================PanelTouchResp start=====================================
    private class PanelTouchResp implements PanelTouchImpl.PanelTouchCallback {
        private Handler mmHandler = new Handler();

        @Override
        public void onActionDown() {
            LogUtil.i(TAG, "onActionDown");
            cancelRunnable();
        }

        @Override
        public void onActionUp() {
            LogUtil.i(TAG, "onActionUp");

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

        }
        void cancelRunnable() {
            mmHandler.removeCallbacksAndMessages(null);
        }
    }


    private class MediaLightModeOnChange implements MediaLightModeController.MediaLightModeListener {

        @Override
        public void onLightOn() {
            Log.i(TAG,"onLightOn");
            ((BaseUsbFragment)getParentFragment()).setIndicatorVisib(true);
        }

        @Override
        public void onLightOff() {
            Log.i(TAG,"onLightOff");
            ((BaseUsbFragment)getParentFragment()).setIndicatorVisib(false);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_folder:
                if(isVisible()){
                    openFolderPage();
                }
                break;
            case R.id.bt_prev:
                MusicPresent.getInstance().playPrevByUser();
                break;
            case R.id.bt_next:
                MusicPresent.getInstance().playNextByUser();
                break;
            case R.id.bt_palyOrPause:
                MusicPresent.getInstance().playOrPauseByUser();
                break;
            case R.id.bt_loop:
                MusicPresent.getInstance().switchPlayMode();
                break;
        }
    }

    /**
     * 打开文件列表页面
     */
    private void openFolderPage() {
        mFragmentManager = getFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();
        if (mFoldersFragment == null) {
            mFoldersFragment = new MusicFoldersFragment();
        }
       // mTransaction.hide(this);
        mTransaction.add(R.id.music_content, mFoldersFragment);
        mTransaction.addToBackStack(null);
        mTransaction.commitAllowingStateLoss();
        ((BaseUsbFragment) getParentFragment()).setIndicatorVisib(false);
    }

    /**
     * 收到广播 回调
     * U 盘挂载事件
     * MusicPlayerFragment#onUdiskStateChange(state)
     * @param state
     */
    public void onUdiskStateChange(boolean state) {
        boolean ismount = UdiskUtil.isHasSupperUDisk(App.getContext());
       // Toast.makeText(App.getContext(), "state =" + state, Toast.LENGTH_LONG).show();
        LogUtil.d(TAG, "ismount =" + ismount);
        if (!ismount || !state) {
            LogUtil.d(TAG, "mLayPlay setVisibility"  );
            // 如果在列表页面，则退出
            if(mFoldersFragment !=null){
                mFragmentManager.popBackStack();
            }
        }
        upDateUdiskUi(ismount);
    }

    private void upDateUdiskUi(boolean  isshow){
        if(isshow){
            mLayUdisk.setVisibility(View.GONE);
            if(mLightModeController !=null){
                mLightModeController.resetLightMode();
            }
        }else {
            mLayUdisk.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.i(TAG, "onStop()");
    }

    public void onPageLoadStop() {
        LogUtil.i(TAG, "onPageLoadStop()");
        MusicPresent.getInstance().setMusicPage(false);
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MusicPresent.getInstance().isPlaying()) {
                    MusicPresent.getInstance().playOrPauseByUser();
                }
            }
        });
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

        MusicPresent.getInstance().removePlayerStateLinsteren();
        MusicPresent.getInstance().destoryEgarMusicApi();
        //   MusicPresent.getInstance().removePlayListener();
        
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.i(TAG, "onDestroyView()");
        try {
            if(mFragmentManager !=null){
                try {
                    mFragmentManager.popBackStack();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.i(TAG, "onDetach()");
    }

    public <T extends View> T findViewById(int id) {
        return (T) mView.findViewById(id);
    }

    //=====================================chage ui========================================
    @Override
    public void playStateChange(int state) {
        LogUtil.i(TAG, "playStateChange()=" + state);
        if (isAdded()) {
            updatePlayStateChangeUI(state);
        }
    }


    @Override
    public void playProgressChanged(String path, int progress, int duration) {
        //  LogUtil.i(TAG, "playProgressChanged()");
        if (isAdded()) {
            refreshFrameInfo(2, progress, duration);
        }

    }

    @Override
    public void playModeChange(int state) {
        LogUtil.i(TAG, "playModeChange()");
        switch (state) {
            case PlayMode.LOOP:
                mBtLoop.setImageResource(R.drawable.usb_loop);
                break;
            case PlayMode.SINGLE:
                mBtLoop.setImageResource(R.drawable.repeat_ico);
                break;
            case PlayMode.RANDOM:
                mBtLoop.setImageResource(R.drawable.random_ico);
                break;
        }
    }

    @Override
    public void scanStateChanged(int state) {
        LogUtil.i(TAG, "scanStateChanged = "+  state +"-->"+ (state == 3 || state == 6 ? "音乐解析完成" : ""));
        updateScanState(state);
        if (!MusicPresent.getInstance().isPlaying() && (state == 3)) {
            MusicPresent.getInstance().firstAutoPlay();
        }
    }

    @Override
    public void MountStateChanged(List list) {

    }

    @Override
    public void onAudioPlayServiceConnected() {
        LogUtil.i(TAG, "onAudioPlayServiceConnected");
        // 更新UI
        mMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MusicPresent.getInstance().focusPlayer();
                loadLocalMedias();
            }
        });

    }
//=================================end ==========================================

    /**
     * 播放状态改变更新UI
     *
     * @param state
     */
    private void updatePlayStateChangeUI(int state) {
        setMediaInformation();
        playModeChange(MusicPresent.getInstance().getPlayMode());
        switch (state) {
            case PlayState.PLAY:
                LogUtil.i(TAG,"PlayState.PLAY");
                 updatePlayOrPause();
                break;
            case PlayState.PREPARED:
                LogUtil.i(TAG,"PlayState.PREPARED");
                if (EgarApiMusic.isPlayEnable(mMainActivity)) {
                    updatePlayOrPause();
                    refreshFrameInfo(0, 0, 0);
                } else {
                    updatePlayOrPause();
                }
                break;
            case PlayState.SEEK_COMPLETED:
                break;
            case PlayState.PAUSE:
                LogUtil.i(TAG,"PlayState.PAUSE");
                updatePlayOrPause();
                break;
            //暂停音频的情况下,将进度条拖动到最后也有可能触发该事件,所以此时不能重置时间
            case PlayState.COMPLETE:
            case PlayState.RELEASE:
                LogUtil.i(TAG,"PlayState.RELEASE");
                updatePlayOrPause();
                break;
            case PlayState.ERROR:
                LogUtil.i(TAG,"PlayState.ERROR");
                updatePlayOrPause();
                //Toast error message.
                ProAudio mediaWithError = MusicPresent.getInstance().getCurrMedia();
                if (mediaWithError != null) {
                    Logs.i(TAG, "onNotifyPlayState$Error :: " + mediaWithError.getMediaUrl());
                    //  AudioUtils.toastPlayError(mMainActivity, mediaWithError.getTitle());
                }
                break;
        }


    }

    /**
     * 扫描更新
     */
    public void updateScanState(int state) {
        if (!UdiskUtil.isHasSupperUDisk(App.getContext())) {
            return;
        }
        if (!MusicPresent.getInstance().isPlaying()) {
            switch (state) {
                case 1:
                    mTvName.setVisibility(View.INVISIBLE);
                    mTvSonger.setText(R.string.scann_start);
                    break;
                case 2:
                    mTvName.setVisibility(View.INVISIBLE);
                    mTvSonger.setText(R.string.scanning);
                    break;
                case 3:
                    mTvSonger.setText(R.string.music_scann_end);
                    mTvName.setVisibility(View.VISIBLE);
                    break;
                default:
                    mTvName.setVisibility(View.VISIBLE);
                    Log.i(TAG," update scan default ="+state);
                    break;
            }
        }

    }

    /**
     * Load current media information.
     */
    private void loadLocalMedias() {
        Log.i(TAG, "loadLocalMedias(更新UI)");
        playModeChange(MusicPresent.getInstance().getPlayMode());
        setMediaInformation();
        updatePlayOrPause();
        refreshFrameInfo(1, 0, 0);
    }

    /**
     * 加载本地图片
     *
     * @param url
     *
     * @return
     */
    public Bitmap getLoacalBitmap(String url) {
        try {
             fileInputStream = new FileInputStream(url);
            return BitmapFactory.decodeStream(fileInputStream);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //  Bitmap bitmap = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.album_bg_em);
            return null;
        }
    }

    /**
     * 更新歌曲显示信息
     */
    private void setMediaInformation() {
        LogUtil.i(TAG,"setMediaInformation");
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        final ProAudio media = MusicPresent.getInstance().getCurrMedia();
        if (media != null) {
            String phonePath = media.getCoverUrl();
            mSeekbar.setMax((int) media.getDuration());
            LogUtil.d(TAG, "图片地址  =" + phonePath);
            if (phonePath != null) {
                bitmap = getLoacalBitmap(phonePath);
                if (bitmap != null) {
                    mImgPhone.setImageBitmap(bitmap);
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    mImgPhone.setImageResource(R.drawable.album_bg_em);
                }
            } else {
                mImgPhone.setImageResource(R.drawable.album_bg_em);
            }
        }
        if (media != null) {

             strTitle = AudioUtils.getMediaTitle(mMainActivity, -1, media, true);
            if (ProAudio.UNKNOWN.equals(strTitle)) {
                mTvName.setText(getString(R.string.unknown_title));
            } else {
                mTvName.setVisibility(View.VISIBLE);
                mTvName.setText(strTitle);
            }
            //Artist
            String artist = media.getArtist();
            if (ProAudio.UNKNOWN.equals(artist) || EmptyUtil.isEmpty(artist)) {
              //  sbInfo.append("::").append(getString(R.string.unknow));
                mTvSonger.setText(getString(R.string.unknow));
            } else {
                mTvSonger.setText(artist);
              //  sbInfo.append("::").append(artist);
            }
            //Album
            String strAlbum = media.getAlbum();
            if (ProAudio.UNKNOWN.equals(strAlbum) || EmptyUtil.isEmpty(strAlbum)) {
              //  sbInfo.append("::").append(getString(R.string.unknow));
            } else {
              //  sbInfo.append("::").append(strAlbum);
            }
           // mTvName.setText(sbInfo);

            mMainActivity.getMainPresent().setMediaStatusInfo(
                    MediaStatus.MEDIA_TYPE_LOCALMUSIC,
                    MusicPresent.getInstance().isPlaying() ? MediaStatus.MEDIA_STATUS_PLAYING :MediaStatus.MEDIA_STATUS_PAUSE,
                    strTitle);
        } else {
            LogUtil.i(TAG, "Media =" + media);
        }
        updateAudioIcon();
    }

    /**
     * 更新播放按钮
     */
    public void updatePlayOrPause() {
        if (MusicPresent.getInstance().isPlaying()) {
            mBtPalyOrPause.setImageResource(R.drawable.pause_ico);
            //   mMuiscPresent.autoPlay();
        } else {
            mBtPalyOrPause.setImageResource(R.drawable.play_ico);
        }
    }

    /**
     * 更新进度条和时间
     *
     * @param flag
     * @param paramProgress
     * @param paramDuration
     */
    private void refreshFrameInfo(int flag, int paramProgress, int paramDuration) {
        switch (flag) {
            //Set on player prepared
            case 0:
                LogUtil.i(TAG, "refreshFrameInfo[0] - Reset.");
                //Duration
                int duration = (int) MusicPresent.getInstance().getDuration();
                String endtime = DateFormatUtil.getFormatHHmmss(duration);
                LogUtil.i(TAG, "EndTime=" + endtime);
                mTvEndTime.setText(endtime);
                mSeekbar.setMax(duration);

                //Progress
                mSeekbar.setProgress(0);
                mTvStartTime.setText(DateFormatUtil.getFormatHHmmss(0));
                break;

            //Refresh current progress/duration
            case 1:
                boolean isPlaying = MusicPresent.getInstance().isPlaying();
                if (isPlaying) {
                    Log.i(TAG, "refreshSeekBar[1] - audio is playing now.");
                    //Duration
                    // duration = (int) mMuiscPresent.getDuration();
                    ProAudio mediaWith = MusicPresent.getInstance().getCurrMedia();
                    long durations = mediaWith.getDuration();
                    mSeekbar.setMax((int) durations);
                    mTvEndTime.setText(DateFormatUtil.getFormatHHmmss(durations));
                    //Progress
                    int currProgress = (int) MusicPresent.getInstance().getProgress();
                    if (currProgress <= durations) {
                        mSeekbar.setProgress(currProgress);
                        mTvStartTime.setText(DateFormatUtil.getFormatHHmmss(currProgress));
                    } else {
                        mSeekbar.setProgress((int) durations);
                        mTvStartTime.setText(DateFormatUtil.getFormatHHmmss(durations));
                    }
                } else {
                    Log.i(TAG, "refreshSeekBar[1] - audio is paused now.");
                    ProAudio currMedia = MusicPresent.getInstance().getCurrMedia();
                    if (currMedia == null) {
                        //Duration
                        mSeekbar.setMax(0);
                        mTvEndTime.setText(DateFormatUtil.getFormatHHmmss(0));
                        //Progress
                        mSeekbar.setProgress(0);
                        mTvStartTime.setText(DateFormatUtil.getFormatHHmmss(0));
                    } else {
                        //Duration
                        duration = (int) (currMedia.getDuration() > 0 ? currMedia.getDuration() : MusicPresent.getInstance().getDuration());
                        mSeekbar.setMax(duration);
                        mTvEndTime.setText(DateFormatUtil.getFormatHHmmss(duration));
                        //Progress
                        int currProgress = mSeekbar.getProgress();
                        if (currProgress <= duration) {
                            mSeekbar.setProgress(currProgress);
                            mTvStartTime.setText(DateFormatUtil.getFormatHHmmss(currProgress));
                        } else {
                            mSeekbar.setProgress(duration);
                            mTvStartTime.setText(DateFormatUtil.getFormatHHmmss(duration));
                        }
                    }
                }
                break;

            case 2:
                //Set SeekBar-Progress
                //   if (!mSeekBarOnChange.isTrackingTouch()) {
                Logs.debugI(TAG, "refreshSeekBar[2] - update progress.");
                //Duration
                if (paramProgress > mSeekbar.getMax()) {
                    paramDuration = paramProgress;
                }
                mSeekbar.setMax(paramDuration);
                mTvEndTime.setText(DateFormatUtil.getFormatHHmmss(paramDuration));
                //Progress
                mSeekbar.setProgress(paramProgress);
                mTvStartTime.setText(DateFormatUtil.getFormatHHmmss(paramProgress));
                //   }
                break;
        }
    }

    @Override
    public void onFinishActivity() {
        LogUtil.i(TAG, "onFinishActivity");
        onPageLoadStop();
        isFinish = true;
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    //================================up ui end==================================================

    //====================================seekbar ================================================
    private final class SeekBarOnChange implements SeekBar.OnSeekBarChangeListener {

        int mmProgress;
        boolean mmIsTracking = false;

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //     LogUtil.i(TAG, "SeekBarOnChange - onStartTrackingTouch");
            mmIsTracking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //      LogUtil.i(TAG, "SeekBarOnChange - onStopTrackingTouch");
            if (mmIsTracking) {
                mmIsTracking = false;
                MusicPresent.getInstance().seekTo(mmProgress);

                //Refresh UI
                mTvStartTime.setText(DateFormatUtil.getFormatHHmmss(mmProgress));
                mTvEndTime.setText(DateFormatUtil.getFormatHHmmss(seekBar.getMax()));
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //     LogUtil.d(TAG, "SeekBarOnChange - onProgressChanged(SeekBar," + progress + "," + fromUser + ")");
            if (fromUser) {
                mmProgress = progress;
            }
        }

        boolean isTrackingTouch() {
            return mmIsTracking;
        }

    }

    public void onBack() {
        if (mFoldersFragment != null) {
            mFoldersFragment.onBackKey();
        }
    }
}
