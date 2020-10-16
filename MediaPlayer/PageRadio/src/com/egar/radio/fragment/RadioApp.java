package com.egar.radio.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.egar.manager.radio.RadioManager;
import com.egar.manager.radio.ServiceListener;
import com.egar.mediaui.App;
import com.egar.mediaui.Icallback.IAppApplication;
import com.egar.mediaui.util.LogUtil;
import com.egar.radio.utils.EgarRadioPreferUtils;
import com.egar.radio.utils.PreferenceHelper;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/14 13:50
 * @see {@link }
 */
public class RadioApp implements IAppApplication,ServiceListener {


    private static  final  String TAG = "RadioApplication";
    private static RadioApp mRadioApp;
    private Context mContext;
    private RadioManager mRadioManager;
    private boolean mHasConnected = false;
    private  Set<ServiceConnectListener> mSetDelegates = new LinkedHashSet<>();

    private static final int MESSAGE_ONSERVICECONNECTE =1001 ;
    private static final int MESSAGE_ONSERVICEDISCONNECTE =1002 ;
    private static final int MESSAGE_APPONTERMINATE =1003 ;

    private Handler mRadioAppHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_ONSERVICECONNECTE :
                    mHasConnected = true;
                    for(ServiceConnectListener listener: mSetDelegates){
                        if(null != listener){
                            listener.onServiceConnected(mRadioManager);
                        }
                    }
                    break;
                case MESSAGE_ONSERVICEDISCONNECTE:
                    mHasConnected = false;
                    for(ServiceConnectListener listener: mSetDelegates){
                        if(null != listener){
                            listener.onServiceDisconnected();
                        }
                    }
                    break;
                case MESSAGE_APPONTERMINATE:
                    for(ServiceConnectListener listener: mSetDelegates){
                        if(null != listener){
                            listener.onAppOnTerminate();
                        }
                    }
                    break;
            }


        }
    };


    /**
     * application 初始化函数
     *
     */
    @Override
    public void appOnCreate() {
        mRadioApp = this;
        mContext = App.getContext();
        LogUtil.d(TAG,"AppOnCreate()     mContext:  " + mContext);
        PreferenceHelper.init(mContext);
        EgarRadioPreferUtils.init(mContext);
        RadioManager.create(mContext,this);
    }


    /**
     * 应用application销毁
     *
     */
    @Override
    public void appOnTerminate() {
        LogUtil.d(TAG,"AppOnTerminate() ");
        Message msg = new Message();
        msg.what = MESSAGE_APPONTERMINATE;
        mRadioAppHandler.sendMessage(msg);
    }


    /**
     * 提供给RadioUIModle的回调，
     * 获取RadioService端的服务连接状态，
     * 并获取RadioManger对象
     *
     */
    public interface ServiceConnectListener {
        void onServiceConnected(RadioManager radioManager);
        void onServiceDisconnected();
        void onAppOnTerminate();
    }

    /**
     * 注册回调
     *
     * @param listener
     */
    public  void registerServiceConnectListener(ServiceConnectListener listener) {
        LogUtil.d(TAG,"registerServiceConnectListener()    listener:  " + listener);
        if (listener != null) {
            mSetDelegates.add(listener);
        }
    }

    /**
     * 解除注册回调
     *
     * @param listener
     */
    public  void unregisterServiceConnectListener(ServiceConnectListener listener) {
        LogUtil.d(TAG,"unregisterServiceConnectListener()    listener:  " + listener);
        if (listener != null) {
            mSetDelegates.remove(listener);
        }
    }

    /**
     * RadioUIModel主动获取RadioManger对象
     *
     */
    public   void requestRadioManager(){
        if(mHasConnected){
            for(ServiceConnectListener listener: mSetDelegates){
                if(null != listener){
                    listener.onServiceConnected(mRadioManager);
                }
            }
        }
    }

    public  static  RadioApp getRadioApp(){
        return  mRadioApp;
    }


    /**
     * RadioService端服务建立连接回调
     *
     * @param radioManager
     */
    @Override
    public void onServiceConnected(RadioManager radioManager) {
        LogUtil.d(TAG,"onServiceConnected() ");
        mRadioManager = radioManager;
        Message msg = new Message();
        msg.what = MESSAGE_ONSERVICECONNECTE;
        mRadioAppHandler.sendMessage(msg);
    }

    /**
     * RadioService端服务断开连接回调
     *
     */
    @Override
    public void onServiceDisconnected() {
        LogUtil.d(TAG,"onServiceDisconnected() ");
        Message msg = new Message();
        msg.what = MESSAGE_ONSERVICEDISCONNECTE;
        mRadioAppHandler.sendMessage(msg);
    }
}
