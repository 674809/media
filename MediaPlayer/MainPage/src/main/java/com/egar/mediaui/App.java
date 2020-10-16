package com.egar.mediaui;

import android.app.Application;
import android.content.Context;

import com.egar.btmusic.fragment.BTMusicApp;
import com.egar.mediaui.util.LogUtil;
import com.egar.radio.fragment.RadioApp;


/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/23 10:09
 * @see {@link }
 */
public class App extends Application {
    private String TAG = "MyApplication";
    private BTMusicApp mBtMusicApp;
    private RadioApp mRadio;
    public static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initClient();
        CrashHandler.getInstance().init(mContext);

    //  LeakCanary.install(this);
    }

    private void initClient() {
        mBtMusicApp = new BTMusicApp();
        mBtMusicApp.appOnCreate();
        mRadio = new RadioApp();
        mRadio.appOnCreate();
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mBtMusicApp.appOnTerminate();
        mRadio.appOnTerminate();
        LogUtil.i(TAG, "onTerminate");
    }

}
