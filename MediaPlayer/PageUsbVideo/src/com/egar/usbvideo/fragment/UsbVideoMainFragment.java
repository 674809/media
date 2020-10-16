package com.egar.usbvideo.fragment;

import android.content.Context;
import android.egar.MediaStatus;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.egar.mediaui.Icallback.IFinishActivity;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseUsbScrollLimitFragment;
import com.egar.mediaui.receiver.MediaBoardcast;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbvideo.present.VideoPresent;
import com.egar.usbvideo.utils.PreferenceHelper;


/**
 * PAGE - Usb Video
 */
public class UsbVideoMainFragment extends BaseUsbScrollLimitFragment implements MediaBoardcast.IMediaReceiver, IFinishActivity {
    // TAG
    private static final String TAG = "UsbVideoMainFragment";

    //==========Variables in this Fragment==========
    // Attached activity of this mFragment.
   // private MainActivity mAttachedActivity;
    private boolean isFrist = true;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private VideoPlayFragment mVideoPlayFragment;
    private static Handler handler = new Handler();

    @Override
    public int getPageIdx() {
        super.getPageIdx();
        return Configs.PAGE_IDX_USB_VIDEO;
    }

    @Override
    public void onWindowChangeFull() {
        LogUtil.i(TAG,"onWindowChangeFull");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mVideoPlayFragment != null && isAdded()) {
                    mVideoPlayFragment.onWindowChangeFull();
                }
            }
        }, 500);

    }

    @Override
    public void onWindowChangeHalf() {
        LogUtil.i(TAG,"onWindowChangeHalf");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mVideoPlayFragment != null && isAdded()) {
                    mVideoPlayFragment.onWindowChangeHalf();
                }
            }
        }, 500);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }


    @Override
    public void initView() {
        super.initView();
        PreferenceHelper.init(getMainActivity());
    }

    @Override
    protected int getLayoutId() {
        super.getLayoutId();
        isFrist = true;
        return R.layout.usb_video_frag_main;
    }

    @Override
    public void onPageResume() {
        super.onPageResume();
        LogUtil.i(TAG,"onPageResume");

    }

    @Override
    public void onPageStop() {
        super.onPageStop();
        LogUtil.i(TAG,"onPageStop");

    }

    @Override
    public void onPageLoadStart() {
        super.onPageLoadStart();
        LogUtil.i(TAG, "onPageLoadStart");
        initPlayerAndFolderFragment();
        getMainActivity().regiestUdiskChange(TAG,this);
      //  ((BaseUsbFragment)getParentFragment()).setIndicatorVisib(true);
        getMainActivity().regiestUdiskChange(TAG, this);
        getMainActivity().getMainPresent().setMediaStatusInfo(
                MediaStatus.MEDIA_TYPE_LOCALVIDEO,
                VideoPresent.getInstance().isPlaying() ? MediaStatus.MEDIA_STATUS_PLAYING :MediaStatus.MEDIA_STATUS_PAUSE,
                "");
    }

    /**
     * 初始化Fragment
     */
    private void initPlayerAndFolderFragment() {
        if (isFrist) {
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if (mVideoPlayFragment == null) {
                mVideoPlayFragment = new VideoPlayFragment();
                transaction.add(R.id.video_content, mVideoPlayFragment,"videoPlayer");
            } else {
                transaction.show(mVideoPlayFragment);
            }
            transaction.commitAllowingStateLoss();
            isFrist = false;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reLoadeLazyChild();
            }
        }, 100);


    }

    private void reLoadeLazyChild() {
        if (mVideoPlayFragment != null && isAdded()) {
            mVideoPlayFragment.onPageLoadStart();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();
        LogUtil.i(TAG,"onPageLoadStop video");
        if (mVideoPlayFragment != null && isAdded()) {
            mVideoPlayFragment.onPageLoadStop();
            getMainActivity().removeUdiskChage(TAG);
        }
    }


    @Override
    public void onFinishActivity() {
        LogUtil.i(TAG, "onFinishActivity");
        getMainActivity().regiestUdiskChange(TAG, this);
        getMainActivity().getMainPresent().setMediaStatusInfo(
                MediaStatus.MEDIA_TYPE_LOCALVIDEO,
                MediaStatus.MEDIA_STATUS_STOP,
                "");
        getMainActivity().exitApp();
    }


    @Override
    public void onUdiskStateChange(final boolean state) {
        LogUtil.i(TAG,"onUdiskStateChange");
        if (mVideoPlayFragment != null) {
            mVideoPlayFragment.onUdiskStateChange(state);
        }
    }

    public void onActivityResults(String data){

    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPageLoadStop();
        LogUtil.i(TAG, "onDetach");
    }

    @Override
    public void onBack() {
        super.onBack();
        if (mVideoPlayFragment != null) {
            mVideoPlayFragment.onBack();
        }
    }
}