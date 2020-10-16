package com.egar.mediaui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egar.btmusic.fragment.BtMusicMainFragment;
import com.egar.mediaui.Icallback.IFinishActivity;
import com.egar.mediaui.adapter.MainFragAdapter;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.lib.NoScrollViewPager;
import com.egar.mediaui.present.MainPresent;
import com.egar.mediaui.present.MediaBtnPresenter;
import com.egar.mediaui.receiver.MediaBoardcast;
import com.egar.mediaui.receiver.MediaSource;
import com.egar.mediaui.util.LogUtil;
import com.egar.mediaui.util.SharedPreferencesUtils;
import com.egar.mediaui.util.Utils;
import com.egar.mediaui.view.IrregularImageView;
import com.egar.radio.fragment.RadioMainFragment;

import java.util.List;

public class MainActivity extends BaseSubActivity implements View.OnClickListener, MediaBtnPresenter.IMediaButton,
        MediaSource.IMediaSorce {
    //TAG
    private static final String TAG = "MainActivity";

    //==========Widgets in this Activity==========
    //View pager
    private NoScrollViewPager mVPager;
    private MainFragAdapter<BaseLazyLoadFragment> mVpFragStateAdapter;
    private ViewPagerOnChange mViewPagerOnChange;
    // Current page
    private BaseLazyLoadFragment mFragCurrent;
    // Fragment list
    private List<BaseLazyLoadFragment> mListFrags;
    //present;
    private MainPresent mMainPresent;
    //Button
    private IrregularImageView mStarClose, mBtnRadio,mBtnSound,mSwitchSize;
    private ImageView mSwitchIcon;
    private Button  mBtnUsb,  mBtnBluebooth;
    private TextView mTvUsb;
    //full layout
    private RelativeLayout mRelativeLayout;
    //当前屏幕标志位 1,半屏，0全屏
    private int currentScreen = 1;
    //当前usb标志位
    private int usbCurrentPage = Configs.PAGE_IDX_USB_MUSIC;
    //底部指示点
    private ImageView[] mImageView;
    //全屏标记
    public static boolean isFull = false;
    //屏幕宽带
    public int windowsWidth = 0;

    private MediaBtnPresenter mMediaBtnPresenter;

    private IFinishActivity iFinishActivity;
    //U盘事件
    private MediaBoardcast udiskBoardcast;
    private IntentFilter intentFilter;


    private static final String BUNDLE_FRAGMENTS_KEY = "android:support:fragments";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.i(TAG, "onCreate");
        overridePendingTransition(0, 0);
        mMainPresent = new MainPresent();
        mMainPresent.initFragment();
        initView();
        initButton();
       initData();
        //   TestSetVolice();
    }


    public void TestSetVolice() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 4, AudioManager.FLAG_PLAY_SOUND);
        LogUtil.i(TAG, "currentVolume =" + currentVolume);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void initData() {
        mMediaBtnPresenter = new MediaBtnPresenter(this);
        mMediaBtnPresenter.regiestMediaBtnBoardcast();
        mMediaBtnPresenter.register();
        mMediaBtnPresenter.setMediaButListener(this);
        MediaSource.registerNotify("source", this);
        mMainPresent.registActivitState();
        mMainPresent.registReverse();
        initCurrentScreen();
        //注册U盘事件
        regiestUdiskBoardcast();
    }

    /**
     * 初始化屏幕全屏或半屏标志位
     */
    public void initCurrentScreen() {
        currentScreen = mMainPresent.getActivityPosition(this);
        if (currentScreen == 0) {
            mSwitchIcon.setImageResource(R.drawable.icon_shrink);
            isFull = true;
        } else {
            mSwitchIcon.setImageResource(R.drawable.icon_spread);
            isFull = false;
        }
    }

    private void initView() {
        mRelativeLayout = (RelativeLayout) findViewById(R.id.main);
        mStarClose = (IrregularImageView) findViewById(R.id.close);
        mBtnRadio = (IrregularImageView) findViewById(R.id.btn_radio);
        mBtnUsb = (Button) findViewById(R.id.btn_usb);
        mBtnBluebooth = (Button) findViewById(R.id.btn_bluebooth);
        mBtnSound = (IrregularImageView) findViewById(R.id.btn_sound);

        mSwitchSize = (IrregularImageView) findViewById(R.id.switch_size);
        mSwitchIcon = (ImageView) findViewById(R.id.switch_icon);

        windowsWidth = Utils.getWindowWidth(this);
        LogUtil.i(TAG, "dpi=" + getDensity());
    }


    private float getDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }


    private void initButton() {
        // -- Variables --
        // -- Widgets --
        // Button
        mStarClose.setOnClickListener(this);
        mBtnRadio.setOnClickListener(this);
        mBtnUsb.setOnClickListener(this);
        mBtnBluebooth.setOnClickListener(this);
        mBtnSound.setOnClickListener(this);
        mSwitchSize.setOnClickListener(this);




        // View pager
       mVPager = (NoScrollViewPager) findViewById(R.id.v_pager);
        mVPager.setOffscreenPageLimit(3);
        mVpFragStateAdapter = new MainFragAdapter<>(getSupportFragmentManager());
        mVPager.setAdapter(mVpFragStateAdapter);
        mVpFragStateAdapter.refresh(mMainPresent.getMainFragmentList());
        mVPager.addOnPageChangeListener((mViewPagerOnChange = new ViewPagerOnChange()));
       initPage();
         requestPermission();
        setStatusBar();
    }

    private void setStatusBar() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        ViewGroup mContentView = (ViewGroup) this.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, true);
        }
    }


    /**
     * 初始化页面
     */
    public void initPage() {
        int position = (int) SharedPreferencesUtils.getParam(getApplicationContext(), "currentPage", 0);
        mFragCurrent = mMainPresent.getCurrenFragmen(position);
        mVPager.setCurrentItem(position, false);
        setButtonBackground(position);
    }

    public MainPresent getMainPresent() {
        if (mMainPresent == null) {
            mMainPresent = new MainPresent();
        }
        return mMainPresent;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mFragCurrent instanceof BaseUsbFragment) {
            ((BaseUsbFragment) mFragCurrent).dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (mFragCurrent instanceof BaseUsbFragment) {
            ((BaseUsbFragment) mFragCurrent).onBackPressed();
            return;
        } else if (mFragCurrent instanceof RadioMainFragment) {
            ((RadioMainFragment) mFragCurrent).onBackPressed();
            return;
        } else if (mFragCurrent instanceof BtMusicMainFragment) {
            ((BtMusicMainFragment) mFragCurrent).onBackPressed();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        currentScreen = mMainPresent.getActivityPosition(this);
        LogUtil.i(TAG, "onClick -- currentScreen =" + currentScreen);
        switch (v.getId()) {
            case R.id.btn_radio:
                mVPager.setCurrentItem(Configs.PAGE_IDX_RADIO, false);
                setButtonBackground(Configs.PAGE_IDX_RADIO);
                break;
            case R.id.btn_usb:
                mVPager.setCurrentItem(Configs.PAGE_INDX_USB, false);
                setButtonBackground(Configs.PAGE_INDX_USB);
                break;
            case R.id.btn_bluebooth:
                mVPager.setCurrentItem(Configs.PAGE_IDX_BT_MUSIC, false);
                setButtonBackground(Configs.PAGE_IDX_BT_MUSIC);
                break;
            case R.id.btn_sound:
                openSound();
                break;
            case R.id.close:
                try{
                    if (iFinishActivity != null) {
                        iFinishActivity.onFinishActivity();
                    }
                }catch(Exception e){
                }finally {
                    exitApp();
                }
              //  exitApp();
                break;
            case R.id.switch_size:
                if (currentScreen == 0) {
                    mMainPresent.checkFullOrHalf(this, false);
                } else if (currentScreen == 1) {
                    mMainPresent.checkFullOrHalf(this, true);
                }
                break;
        }
    }

    private void openSound() {
        Intent intent = new Intent();
        intent.setAction("com.egar.settings.open");
        intent.putExtra("fragment_name", "Sound effect");
        sendBroadcast(intent);
    }

    public void exitApp() {
        finish();
    }


    public void setFinishActivitListener(IFinishActivity iFinishActivity) {
        LogUtil.i(TAG, " 注册类iFinishActivity 类 =" + iFinishActivity.toString());
        this.iFinishActivity = iFinishActivity;
    }


    public void removeFinishActivitListener() {
        LogUtil.i(TAG, " 反注册类iFinishActivity 类");
        this.iFinishActivity = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  LogUtil.i("onResume");
        if (mFragCurrent instanceof BaseUsbFragment ||
                mFragCurrent instanceof RadioMainFragment ||
                mFragCurrent instanceof BtMusicMainFragment) {
            mFragCurrent.onPageResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // LogUtil.i("onStop");
        if (mFragCurrent instanceof BaseUsbFragment ||
                mFragCurrent instanceof RadioMainFragment ||
                mFragCurrent instanceof BtMusicMainFragment) {
            mFragCurrent.onPageStop();
        }

    }


    /**
     * 设置按钮背景颜色
     */
    public void setButtonBackground(int position) {
       if (position == Configs.PAGE_IDX_RADIO) {
            LogUtil.i(TAG,"radio");
            mBtnRadio.setBackgroundResource(R.drawable.radio_bt_bg_p);
            mBtnUsb.setBackgroundResource(R.drawable.usb_bt_bg);
            mBtnBluebooth.setBackgroundResource(R.drawable.usb_bt_bg);

       } else if (position == Configs.PAGE_INDX_USB) {
            mBtnRadio.setBackgroundResource(R.drawable.radio_bt_bg);
            mBtnUsb.setBackgroundResource(R.drawable.usb_booth);
            mBtnBluebooth.setBackgroundResource(R.drawable.usb_bt_bg);
            // mTvUsb.setCompoundDrawables(null,null,null,null);
        } else if (position == Configs.PAGE_IDX_BT_MUSIC) {
            mBtnRadio.setBackgroundResource(R.drawable.radio_bt_bg);
            mBtnUsb.setBackgroundResource(R.drawable.usb_bt_bg);
            mBtnBluebooth.setBackgroundResource(R.drawable.usb_booth);
        }

    }

    @Override
    public void onNextLongClick() {
        if (mFragCurrent instanceof BaseUsbFragment) {
            ((BaseUsbFragment) mFragCurrent).onNextLongClick();
        }
    }

    @Override
    public void onNextClick() {
        if (mFragCurrent instanceof BaseUsbFragment) {
            ((BaseUsbFragment) mFragCurrent).onNextClick();
        }
    }

    @Override
    public void onPrevLongClick() {
        if (mFragCurrent instanceof BaseUsbFragment) {
            ((BaseUsbFragment) mFragCurrent).onPrevLongClick();
        }
    }

    @Override
    public void onPrevClick() {
        if (mFragCurrent instanceof BaseUsbFragment) {
            ((BaseUsbFragment) mFragCurrent).onPrevClick();
        }
    }

    @Override
    public void onSouceChange() {
        LogUtil.d(TAG, "source change");
        int position = mVPager.getCurrentItem();
        if (position == 0) { //radio
            mVPager.setCurrentItem(1, false);
            setButtonBackground(1);
            ((BaseUsbFragment) mFragCurrent).setCurrentMusicPage();
        } else if (position == 1) {//usb
            mVPager.setCurrentItem(2, false);
            setButtonBackground(2);
        } else if (position == 2) { //bt
            mVPager.setCurrentItem(0, false);
            setButtonBackground(0);
        }
    }


    private class ViewPagerOnChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
            //Log.i(TAG, "onPageScrollStateChanged(" + state + ")");
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            //可以同步按钮状态
            SharedPreferencesUtils.setParam(getApplicationContext(), "currentPage", position);
            showNativigtion();
              Log.i(TAG, "onPageSelected(" + position + ")");
            try {
                mFragCurrent = mMainPresent.getCurrenFragmen(position);// mListFrags.get(position);
                if (mFragCurrent instanceof BaseUsbFragment) {

                }
            } catch (Exception e) {
                Log.i(TAG, "ViewPagerOnChange >> onPageSelected() >> [e: " + e.getMessage());
            }
        }
    }


    /**
     * 模拟
     * 设置全屏或半屏
     *
     * @param size
     */
    public void setWindowSize(float size) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.gravity = Gravity.TOP;
        p.height = (int) (dm.heightPixels * size);
        p.width = (int) (dm.widthPixels * 1);
        getWindow().setAttributes(p);
    }

    /**
     * 获取屏幕位置
     */
    public Rect getStackBound() {
        if (mMainPresent != null) {
            return mMainPresent.getStackBound(this);
        }
        return null;
    }


    /**
     * 返回屏幕是否为全屏
     */
    public boolean getScreenState() {
        return isFull;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mFragCurrent instanceof RadioMainFragment) {
            ((RadioMainFragment) mFragCurrent).onActivityResults(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");
        overridePendingTransition(0, 0);
        MediaSource.removeNotify("source");
        removeFinishActivitListener();
        if (udiskBoardcast != null) {
            udiskBoardcast.cleanMap();
            // udiskBoardcast.registerNotify(iMediaReceiver);
        }
        unRegiestUdiskBoardcast();
      

        if (mMainPresent != null) {
            mMainPresent.Destory();
            mMainPresent.unRegistActivityState();
            mMainPresent.removeFragment();
            mMainPresent = null;
        }
        if (mMediaBtnPresenter != null) {
            mMediaBtnPresenter.unRegiestMediaBtnBoardcast();
            mMediaBtnPresenter.unregister();
        }

 		 

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        currentScreen = mMainPresent.getActivityPosition(this);
        LogUtil.i(TAG, "currentScreen =" + currentScreen);
        if (currentScreen == 0) {
            mMainPresent.setOnWindowChangeFull();
            mSwitchIcon.setImageResource(R.drawable.icon_shrink);
            isFull = true;
        } else if (currentScreen == 1) {
            mMainPresent.setOnWindowChangeHalf();
            mSwitchIcon.setImageResource(R.drawable.icon_spread);
            isFull = false;
        }
    }

    public int getWindowsWidth() {
        return windowsWidth;
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                String[] requestPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                this.requestPermissions(requestPermissions, 0);
            }
        }
    }


    /**
     * 注册U盘插拔广播
     */
    private void regiestUdiskBoardcast() {
        udiskBoardcast = new MediaBoardcast();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter.addDataScheme("file");
        App.getContext().registerReceiver(udiskBoardcast, intentFilter);
    }

    private void unRegiestUdiskBoardcast() {
        App.getContext().unregisterReceiver(udiskBoardcast);
    }

    public void regiestUdiskChange(String fragmentName ,MediaBoardcast.IMediaReceiver iMediaReceiver) {
        if (udiskBoardcast != null) {
            udiskBoardcast.registerNotify(fragmentName,iMediaReceiver);
        }
    }

    public void removeUdiskChage(String fragmentName) {
        if (udiskBoardcast != null) {
            udiskBoardcast.removeNotify(fragmentName);
        }
    }

}
