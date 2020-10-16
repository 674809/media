package com.egar.mediaui.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.egar.mediaui.App;
import com.egar.mediaui.Icallback.IKeyBack;
import com.egar.mediaui.Icallback.IMediaBtuClick;
import com.egar.mediaui.Icallback.ITouchListener;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.adapter.UsbFragAdapter;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.receiver.MediaBoardcast;
import com.egar.mediaui.util.LogUtil;
import com.egar.mediaui.util.SharedPreferencesUtils;
import com.egar.mediaui.view.CustomViewPager;
import com.egar.usbimage.fragment.UsbImageMainFragment;
import com.egar.usbmusic.fragment.UsbMusicMainFragment;
import com.egar.usbmusic.utils.UdiskUtil;
import com.egar.usbvideo.fragment.UsbVideoMainFragment;

import java.util.ArrayList;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/25 14:07
 * @see {@link }
 */
public class BaseUsbFragment extends BaseUsbScrollLimitFragment implements ViewPager.OnPageChangeListener, View.OnClickListener{
    private String TAG = "BaseUsbFragment";
    //==========Variables in this Fragment==========
    // Attached activity of this fragment.
    // Current page
    private CustomViewPager mUsbViewPager;
    private UsbFragAdapter mUsbFragAdapter;
    private BaseLazyLoadFragment mUsbCurrenfrag;
    private LinearLayout mUsb_frag;
    private RadioGroup mRadioGroup;
    private RadioButton mRabtnMusic, mRabtnVideo, mRabtnPic;
    private RelativeLayout mLayoutUdiskInfo;
    //回调touche事件
    private ArrayList<ITouchListener> mTouchListeners = new ArrayList<>();

    private ITouchListener iTouchListener;
    //back事件
    private IKeyBack iKeyBack;
    //点击事件
    private IMediaBtuClick iMediaBtuClick;
    private boolean isFrist = true;

    @Override
    public int getPageIdx() {
        return Configs.PAGE_INDX_USB;
    }

    @Override
    public void onWindowChangeFull() {
        // LogUtil.i(TAG,"onWindowChangeFull");
        if (getUsbCurrenfrag() instanceof UsbMusicMainFragment ||
                getUsbCurrenfrag() instanceof UsbVideoMainFragment ||
                getUsbCurrenfrag() instanceof UsbImageMainFragment) {
            getUsbCurrenfrag().onWindowChangeFull();
        }
    }

    @Override
    public void onWindowChangeHalf() {
        //  LogUtil.i(TAG,"onWindowChangeHalf");
        if (getUsbCurrenfrag() instanceof UsbMusicMainFragment ||
                getUsbCurrenfrag() instanceof UsbVideoMainFragment ||
                getUsbCurrenfrag() instanceof UsbImageMainFragment) {
            getUsbCurrenfrag().onWindowChangeHalf();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void initView() {
        Log.i(TAG,"initView");
        mRadioGroup = (RadioGroup) findViewById(R.id.usb_title_radiogroup);
        mRabtnMusic = (RadioButton) findViewById(R.id.radiobtn_usb_title_music);
        mRabtnVideo = (RadioButton) findViewById(R.id.radiobtn_usb_title_video);
        mRabtnPic = (RadioButton) findViewById(R.id.radiobtn_usb_title_pic);
        mLayoutUdiskInfo = findViewById(R.id.fralyout_udisk);
        mRabtnMusic.setOnClickListener(this);
        mRabtnVideo.setOnClickListener(this);
        mRabtnPic.setOnClickListener(this);
    }


    /**
     * 初始化页面
     */
    public void initPage() {
        int position = (int) SharedPreferencesUtils.getParam(getMainActivity(), "currentUsbPage", 0);
        mUsbCurrenfrag = getMainActivity().getMainPresent().getCurrentUsbFragmen(position);//usbmusic
        mUsbViewPager.setCurrentItem(position, false);
        setIndicatorSelect(position);
    }

    /**
     * 让Source按键切换使用
     */
    public void setCurrentMusicPage() {
        LogUtil.d(TAG, "setCurrentMusicPage");
        mUsbViewPager.setCurrentItem(0, false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_usb;
    }


    @Override
    public void onPageLoadStart() {
        if (isFrist) {

            mUsbViewPager = findViewById(R.id.usbViepager);
            mUsbFragAdapter = new UsbFragAdapter(getChildFragmentManager());
            mUsbFragAdapter.refresh(getMainActivity().getMainPresent().getUsbFragmentList());
            mUsbViewPager.setAdapter(mUsbFragAdapter);
            mUsbViewPager.setOffscreenPageLimit(3);
            mUsbViewPager.addOnPageChangeListener(this);
            mUsb_frag = findViewById(R.id.usb_frag);

            initPage();
            isFrist = false;
        } else {
            if (getUsbCurrenfrag() instanceof UsbMusicMainFragment ||
                    getUsbCurrenfrag() instanceof UsbVideoMainFragment ||
                    getUsbCurrenfrag() instanceof UsbImageMainFragment) {
                getUsbCurrenfrag().onPageLoadStart();
            }
        }
       /* boolean ismount = UdiskUtil.isHasSupperUDisk(App.getContext());
        updateUdiskPage(ismount);*/
        getMainActivity().showNativigtion();
        getMainActivity().getMainPresent().setOnWindowChange(this);
        getMainActivity().regiestUdiskChange(TAG,this);
        // LogUtil.i("Usb onPageLoadStart");
    }

    @Override
    public void onPageResume() {
        // LogUtil.i("onPageResume");
        if (getUsbCurrenfrag() instanceof UsbMusicMainFragment ||
                getUsbCurrenfrag() instanceof UsbVideoMainFragment ||
                getUsbCurrenfrag() instanceof UsbImageMainFragment) {
            getUsbCurrenfrag().onPageResume();
        }
    }

    @Override
    public void onPageStop() {
        LogUtil.i("onPageStop");
        if (getUsbCurrenfrag() instanceof UsbMusicMainFragment ||
                getUsbCurrenfrag() instanceof UsbVideoMainFragment ||
                getUsbCurrenfrag() instanceof UsbImageMainFragment) {
            getUsbCurrenfrag().onPageStop();
        }

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //  Log.i(TAG, "onPageScrolled(" + position + ")");
    }


    /**
     * 设置Indicator状态栏是否可
     * 见
     *
     * @param visable
     */
    public void setIndicatorVisib(boolean visable) {
        if (visable) {
            mRadioGroup.setVisibility(View.VISIBLE);
        } else {
            mRadioGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.i(TAG, "onPageSelected(" + position + ")");
        //可以同步按钮状态
        mUsbCurrenfrag = getMainActivity().getMainPresent().getCurrentUsbFragmen(position);
        SharedPreferencesUtils.setParam(getMainActivity(), "currentUsbPage", position);
        setIndicatorSelect(position);
        getMainActivity().showNativigtion();
    }

    /**
     * 设置选中按钮
     *
     * @param position
     */
    private void setIndicatorSelect(int position) {
        switch (position) {
            case 0:
                mRabtnMusic.setTextSize(35);
                mRabtnVideo.setTextSize(30);
                mRabtnPic.setTextSize(30);
                mRabtnMusic.setTextColor(getResources().getColor(R.color.white));
                mRabtnVideo.setTextColor(getResources().getColor(R.color.textblack_withe));
                mRabtnPic.setTextColor(getResources().getColor(R.color.textblack_withe));
                break;
            case 1:
                mRabtnMusic.setTextSize(30);
                mRabtnVideo.setTextSize(35);
                mRabtnPic.setTextSize(30);
                mRabtnMusic.setTextColor(getResources().getColor(R.color.textblack_withe));
                mRabtnVideo.setTextColor(getResources().getColor(R.color.white));
                mRabtnPic.setTextColor(getResources().getColor(R.color.textblack_withe));
                break;
            case 2:
                mRabtnMusic.setTextSize(30);
                mRabtnVideo.setTextSize(30);
                mRabtnPic.setTextSize(35);
                mRabtnMusic.setTextColor(getResources().getColor(R.color.textblack_withe));
                mRabtnVideo.setTextColor(getResources().getColor(R.color.textblack_withe));
                mRabtnPic.setTextColor(getResources().getColor(R.color.white));
                break;

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void dispatchTouchEvent(MotionEvent ev) {
        for (ITouchListener listener : mTouchListeners) {
            if (listener != null && mUsbViewPager != null) {
                //  LogUtil.i("dispatchTouchEvent ="+listener.onTouchEvent(ev));
                mUsbViewPager.setIsScanScroll(listener.onTouchEvent(ev));
            }
        }
    }

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     */
    public void registerMyTouchListener(ITouchListener listener) {
        // LogUtil.i("listener =>"+mTouchListeners.size());
        mTouchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     */
    public void unRegisterMyTouchListener(ITouchListener listener) {
        mTouchListeners.remove(listener);
    }

    // key back事件
    public void onBackPressed() {
        LogUtil.i(TAG, "onBackPressed");
        if (iKeyBack == null) {
            getMainActivity().exitApp();
        } else {
            iKeyBack.onBack();
        }
    }


    public void registBackEvent(IKeyBack iKeyBack) {
        this.iKeyBack = iKeyBack;
    }

    public void unRegistBackEvent() {
        this.iKeyBack = null;
    }

    public BaseLazyLoadFragment getUsbCurrenfrag() {
        return mUsbCurrenfrag;
    }

    @Override
    public void onPageLoadStop() {
        LogUtil.i("Usb onPageLoadStop");
        if (getUsbCurrenfrag() instanceof UsbMusicMainFragment ||
                getUsbCurrenfrag() instanceof UsbVideoMainFragment ||
                getUsbCurrenfrag() instanceof UsbImageMainFragment) {
            getUsbCurrenfrag().onPageLoadStop();
        }
        getMainActivity().getMainPresent().removerWindowChange(this);
        getMainActivity().removeUdiskChage(TAG);
    }


    public void onNextClick() {
        iMediaBtuClick.onNextClick();
    }

    public void onNextLongClick() {
        iMediaBtuClick.onNextLongClick();
    }

    public void onPrevClick() {
        iMediaBtuClick.onPrevClick();
    }

    public void onPrevLongClick() {
        iMediaBtuClick.onNextLongClick();
    }

    public void setMediaBtnClickListener(IMediaBtuClick iMediaBtuClick) {
        this.iMediaBtuClick = iMediaBtuClick;
    }

    public void removeMediaBtnClick() {
        iMediaBtuClick = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.i(TAG, "onDetach");
        getMainActivity().getMainPresent().removerWindowChange(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radiobtn_usb_title_music:
                mUsbViewPager.setCurrentItem(0, false);
                break;
            case R.id.radiobtn_usb_title_video:
                mUsbViewPager.setCurrentItem(1, false);
                break;
            case R.id.radiobtn_usb_title_pic:
                mUsbViewPager.setCurrentItem(2, false);
                break;
        }
    }

    @Override
    public void onUdiskStateChange(boolean state) {
        super.onUdiskStateChange(state);
        Log.i(TAG,"onUdiskStateChange ="+state);
      //  updateUdiskPage(state);
    }


    public void updateUdiskPage(boolean state){
        if(state){
            mLayoutUdiskInfo.setVisibility(View.GONE);
        }else {
            mLayoutUdiskInfo.setVisibility(View.VISIBLE);
            getMainActivity().showNativigtion();
        }
    }

    public int getUsbChildFragment(){
        return mUsbViewPager.getCurrentItem();
    }
}
