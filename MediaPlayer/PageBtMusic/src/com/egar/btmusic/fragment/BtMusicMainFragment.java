package com.egar.btmusic.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egar.btproxy.common.BTDevice;
import com.egar.btproxy.common.BTLog;
import com.egar.btproxy.common.BTMusicConstants;
import com.egar.btproxy.music.BTMusicSongInfo;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.util.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;


/**
 * PAGE - BT Music
 */
public class BtMusicMainFragment extends BaseLazyLoadFragment {
    // TAG
    private static final String TAG = "BtMusicMainFrag";


    //==========Variables in this Fragment==========
    // Attached activity of this fragment.
    private MainActivity mAttachedActivity;

    //显示区域
    private RelativeLayout mRLCover;
    private RelativeLayout mRLContent;

    //三个区域
    private RelativeLayout mRLPhoto, mRLPlayer;
    private LinearLayout mLLInfo;

    //播放按键
    private ImageView mIVPlayOrPause, mIVPrev, mIVNext;

    //歌曲信息区域
    private TextView mTVSongName, mTVSonger, mTVSongAblum;

    private ImageView mIVAblum;

    private UIHandler mUIHandler;

    private Button mBtnSettings;

    @Override
    public int getPageIdx() {
        return Configs.PAGE_IDX_BT_MUSIC;
    }

    @Override
    public void onWindowChangeFull() {
        LogUtil.i("onWindowChangeFull");
        changeToFull();

    }

    @Override
    public void onWindowChangeHalf() {
        LogUtil.i("onWindowChangeHalf");
        changeToHalf();
    }

    public void onBackPressed() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.bt_frag_main;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAttachedActivity = (MainActivity) context;
    }

    @Override
    public void initView() {
        mRLCover = findViewById(R.id.rl_cover);
        mRLContent = findViewById(R.id.rl_content);

        mBtnSettings = findViewById(R.id.btn_goto_setting);

        mRLPhoto = findViewById(R.id.rl_photo);
        mRLPlayer = findViewById(R.id.rl_player);
        mLLInfo = findViewById(R.id.ll_info);

        mIVAblum = findViewById(R.id.iv_album);
        mTVSongName = findViewById(R.id.tv_song_name);
        mTVSonger = findViewById(R.id.tv_songer);
        mTVSongAblum = findViewById(R.id.tv_song_ablum);

        mIVPrev = findViewById(R.id.iv_prev);
        mIVNext = findViewById(R.id.iv_next);
        mIVPlayOrPause = findViewById(R.id.iv_playOrPause);

        BTMusicApp.registerMusicObserver(observer);
        mUIHandler = new UIHandler(this);

        mIVPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BTMusicApp.isAvailable()) {
                    int playbackState = BTMusicApp.sMusicClient.getPlaybackState();
                    if (playbackState == BTMusicConstants.PLAYBACK_PAUSE) {
                        BTMusicApp.sMusicClient.playMusic();
                    } else if (playbackState == BTMusicConstants.PLAYBACK_PLAY) {
                        BTMusicApp.sMusicClient.pauseMusic();
                    }
                }
            }
        });

        mIVPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BTMusicApp.isAvailable()) {
                    BTMusicApp.sMusicClient.preMusic();
                }
            }
        });

        mIVNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BTMusicApp.isAvailable()) {
                    BTMusicApp.sMusicClient.nextMusic();
                }
            }
        });

        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("com.egar.settings.open");
                intent.putExtra("fragment_name", "Bluetooth");
                getActivity().sendBroadcast(intent);
            }
        });

    }

    @Override
    public void onPageResume() {
        if (BTMusicApp.isAvailable()) {
            BTMusicApp.sMusicClient.requestFocus();
        }
    }

    @Override
    public void onPageLoadStart() {
        Log.i(TAG,"onPageLoadStart");
        if (BTMusicApp.isAvailable()) {
            BTMusicApp.sMusicClient.requestFocus();
        }
        mAttachedActivity.getMainPresent().setOnWindowChange(this);
        initAllState();
    }

    @Override
    public void onPageStop() {

    }

    @Override
    public void onPageLoadStop() {
        mAttachedActivity.getMainPresent().removerWindowChange(this);
    }

    /**
     * 初始更新状态
     */
    private void initAllState() {

    }

    /**
     * 初始化界面到全屏
     */
    private void changeToFull() {

        mRLPhoto.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLLInfo
                .getLayoutParams();
        layoutParams.setMargins(0, 77, 0, 0);
        mLLInfo.requestLayout();

        mTVSongName.setTextSize(77);

    }

    /**
     * 初始化界面到半屏
     */
    private void changeToHalf() {

        mRLPhoto.setVisibility(View.GONE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLLInfo
                .getLayoutParams();
        layoutParams.setMargins(0, 20, 0, 0);
        mLLInfo.requestLayout();

        mTVSongName.setTextSize(50);

    }

    private void refreshConnectionState() {
        if (!BTMusicApp.isAvailable() || BTMusicApp.sMusicClient.getCurrentDevice() == null) {
            //显示提示窗口
            mRLCover.setVisibility(View.VISIBLE);
            mRLContent.setVisibility(View.INVISIBLE);
        } else {
            //显示内容
            mRLCover.setVisibility(View.INVISIBLE);
            mRLContent.setVisibility(View.VISIBLE);
        }
    }

    private void refreshPlayButton(int state) {

        BTLog.e("state : " + state);

        if (state == BTMusicConstants.PLAYBACK_PLAY) {

            mIVPlayOrPause.setImageResource(R.drawable.pause_ico);

        } else if (state == BTMusicConstants.PLAYBACK_PAUSE) {

            mIVPlayOrPause.setImageResource(R.drawable.play_ico);

        } else {
            BTLog.e("UNKnow the play state");
        }
    }

    private long mDuration = 0l;

    private void refreshMetaData(BTMusicSongInfo info) {

        mTVSongName.setText(info.getTitle());
        mTVSonger.setText(info.getArtist());
        mTVSongAblum.setText(info.getAlbum());

        if (info.getPhotoUrl() != null) {
            File file = new File(info.getPhotoUrl());
            if (file.exists()) {
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity()
                            .getContentResolver(), Uri.fromFile(file));
                    mIVAblum.setImageBitmap(bmp);
                } catch (FileNotFoundException e) {

                } catch (IOException e) {

                }
            } else {
                mIVAblum.setImageResource(R.drawable.bt_album_bg_em);
            }
        }
        mDuration = info.getDuration();

    }

    private BTMusicApp.OnMusicObserver observer = new BTMusicApp.OnMusicObserver() {
        @Override
        public void onConnectionStateChanged(BTDevice device, int state) {
            mUIHandler.sendEmptyMessage(MSG_CONNECTION_CHANGED);
        }

        @Override
        public void onPlayStateChanged(int state) {
            mUIHandler.sendMessage(mUIHandler.obtainMessage(MSG_PLAYSTATE_CHANGED, state));
        }

        @Override
        public void onMetadataChanged(BTMusicSongInfo songInfo) {
            mUIHandler.sendMessage(mUIHandler.obtainMessage(MSG_METADATA_CHANGED, songInfo));
        }

        @Override
        public void onPlaybackPositionChanged(long position) {
            //无进度条暂时不用
        }
    };

    private static final int MSG_CONNECTION_CHANGED = 1;

    private static final int MSG_PLAYSTATE_CHANGED = 2;

    private static final int MSG_METADATA_CHANGED = 3;

    private static final int MSG_PLAYBACKPOSITION_CHANGED = 4;

    private static class UIHandler extends Handler {

        private final WeakReference<BtMusicMainFragment> wrf_fragment;

        UIHandler(BtMusicMainFragment fragment) {
            wrf_fragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            BTLog.i("msg id " + what);
            BtMusicMainFragment fragment = wrf_fragment.get();
            if (fragment != null) {
                switch (what) {
                    case MSG_CONNECTION_CHANGED:
                        fragment.refreshConnectionState();
                        break;
                    case MSG_PLAYSTATE_CHANGED:
                        fragment.refreshPlayButton((Integer) msg.obj);
                        break;
                    case MSG_METADATA_CHANGED:
                        fragment.refreshMetaData((BTMusicSongInfo) msg.obj);
                        break;
                    case MSG_PLAYBACKPOSITION_CHANGED:
                        break;
                }
            }
        }
    }
}