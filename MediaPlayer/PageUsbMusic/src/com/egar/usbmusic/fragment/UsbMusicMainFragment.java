package com.egar.usbmusic.fragment;


import android.content.Context;
import android.content.Intent;
import android.egar.MediaStatus;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.egar.mediaui.Icallback.IFinishActivity;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseUsbScrollLimitFragment;
import com.egar.mediaui.receiver.MediaBoardcast;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.present.MusicPresent;


/**
 * PAGE - Usb Music
 */
public class UsbMusicMainFragment extends BaseUsbScrollLimitFragment implements IFinishActivity,
        MediaBoardcast.IMediaReceiver {
    // TAG
    private static final String TAG = "UsbMusicMainFragment";

    //==========Variables in this Fragment==========
    // Attached activity of this mFragment.
    private boolean isFrist = true;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private MusicPlayerFragment mMusicPlayerFragment;
    private static Handler mhandler = new Handler();


    @Override
    public int getPageIdx() {
        super.getPageIdx();
        return Configs.PAGE_IDX_USB_MUSIC;
    }

    @Override
    public void onWindowChangeFull() {
        //  LogUtil.i("onWindowChangeFull");
        if (mMusicPlayerFragment != null) {
            mMusicPlayerFragment.onWindowChangeFull();
        }
    }

    @Override
    public void onWindowChangeHalf() {
        //  LogUtil.i("onWindowChangeHalf");
        if (mMusicPlayerFragment != null) {
            mMusicPlayerFragment.onWindowChangeHalf();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.i(TAG, "onAttach");
    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    protected int getLayoutId() {
        super.getLayoutId();
        isFrist = true;
        return R.layout.usb_music_frag_main;
    }


    @Override
    public void onPageLoadStart() {
        super.onPageLoadStart();
        initPlayerAndFolderFragment();
        //  ((BaseUsbFragment) getParentFragment()).setIndicatorVisib(true);
        getMainActivity().regiestUdiskChange(TAG, this);
        getMainActivity().getMainPresent().setMediaStatusInfo(
                MediaStatus.MEDIA_TYPE_LOCALMUSIC,
                MusicPresent.getInstance().isPlaying() ? MediaStatus.MEDIA_STATUS_PLAYING :MediaStatus.MEDIA_STATUS_PAUSE,
                "");
    }

    @Override
    public void onPageResume() {
        super.onPageResume();
        LogUtil.i("onPageResume");

    }

    @Override
    public void onPageStop() {
        super.onPageStop();
        LogUtil.i(TAG, "onPageStop");
    }

    /**
     * 初始化Fragment
     */
    private void initPlayerAndFolderFragment() {
        if (isFrist) {
            //   LogUtil.w("init music Main");
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if (mMusicPlayerFragment == null) {
                mMusicPlayerFragment = new MusicPlayerFragment();
                transaction.add(R.id.music_content, mMusicPlayerFragment, "musicPlayer");
            } else {
                transaction.show(mMusicPlayerFragment);
            }
            transaction.commitAllowingStateLoss();
            isFrist = false;
        } //第一次默认会调用lazyLoad
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reLoadeLazyChild();
            }
        }, 300);


    }

    /**
     * 页面在Paly页面时，切换到usbVideo 回来后不会再调用paly 页面的loadLazy()函数
     * 页面可见时，调用当前页面的可见函数
     */
    private void reLoadeLazyChild() {
        if (mMusicPlayerFragment != null) {
            mMusicPlayerFragment.onPageLoadStart();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");

    }

    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMusicPlayerFragment != null) {
                    mMusicPlayerFragment.onPageLoadStop();
                    getMainActivity().removeUdiskChage(TAG);
                }
            }
        }, 0);

    }


    @Override
    public void onFinishActivity() {
        getMainActivity().getMainPresent().setMediaStatusInfo(
                MediaStatus.MEDIA_TYPE_LOCALMUSIC,
                MediaStatus.MEDIA_STATUS_STOP,
                "");
        getMainActivity().exitApp();
    }

    @Override
    public void onUdiskStateChange(final boolean state) {
        LogUtil.i(TAG, "onUdiskStateChange  isAdd =" + isAdded());
        if (isAdded()) {
            mhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mMusicPlayerFragment != null) {
                        mMusicPlayerFragment.onUdiskStateChange(state);
                    }
                }
            }, 0);
        }
    }

    public void onActivityResults(String data){

    }

    @Override
    public void onBack() {
        super.onBack();
        if (mMusicPlayerFragment != null) {
            mMusicPlayerFragment.onBack();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPageLoadStop();
        LogUtil.i(TAG, "onDetach");
    }
}