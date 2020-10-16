package com.egar.mediaui.fragment;

import android.content.Context;
import android.view.MotionEvent;

import com.egar.mediaui.Icallback.IKeyBack;
import com.egar.mediaui.Icallback.ITouchListener;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.receiver.MediaBoardcast;
import com.egar.mediaui.util.LogUtil;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/11/1 09:56
 * @see {@link }
 */
public class BaseUsbScrollLimitFragment extends BaseLazyLoadFragment implements ITouchListener, IKeyBack, MediaBoardcast.IMediaReceiver {
    public boolean isScroll = true;
    private BaseUsbFragment mFragment;
    private  String TAG = "BaseUsbScrollLimitFragment";
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public int getPageIdx() {
        return 0;
    }

    @Override
    public void onWindowChangeFull() {

    }

    @Override
    public void onWindowChangeHalf() {

    }

    @Override
    public void initView() {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void onPageResume() {

    }

    @Override
    public void onPageStop() {

    }

    @Override
    public void onPageLoadStart() {
        mFragment = (BaseUsbFragment) getMainActivity().getMainPresent().getCurrenFragmen(Configs.PAGE_INDX_USB);
        mFragment.registerMyTouchListener(this);
        mFragment.registBackEvent(this);
        getMainActivity().regiestUdiskChange(TAG,this);
    }

    @Override
    public void onPageLoadStop() {
        if (mFragment != null) {
            mFragment.unRegisterMyTouchListener(this);
            mFragment.unRegistBackEvent();
            getMainActivity().removeUdiskChage(TAG);
        }

    }

    private float mDownX, mTargetX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                //     Log.i(TAG, "mDownX : " + mDownX);
                break;
            case MotionEvent.ACTION_MOVE:
                mTargetX = event.getX();
                //    Log.i(TAG, "ACTION_MOVE >> mDownX - mUpX = " + (mDownX - mTargetX));
                break;
            case MotionEvent.ACTION_UP:
                mTargetX = event.getX();
                // Log.i(TAG, "ACTION_UP >> mDownX - mUpX = " + (mDownX - mTargetX));

                break;
        }
        //  LogUtil.i(TAG,"windowidth"+mAttachedActivity.getWindowsWidth());
        if (mDownX >= 860 || mDownX <= 100) {
            return isScroll;
        } else {
            return false;
        }
    }

    /**
     * 按键返回事件
     */
    @Override
    public void onBack() {
        LogUtil.i(TAG, "onBack");
    }

    @Override
    public void onUdiskStateChange(boolean state) {

    }
}
