package com.egar.mediaui.present;

import android.content.ComponentName;
import android.egar.ActivityPolicyClient;
import android.egar.CanbusProxyClient;
import android.egar.CarManager;
import android.egar.EventProxyClient;
import android.egar.IActivityStatus;
import android.egar.ICarSpeed;
import android.egar.IReverseStatus;
import android.egar.MediaStatus;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;

import com.egar.mediaui.App;
import com.egar.mediaui.Icallback.IWindowChange;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.model.FragmentFactory;
import com.egar.mediaui.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/23 14:42
 * @see {@link }
 */
public class MainPresent {
    private String TAG = "MainPresent";
    private static MainPresent mPresnet;
    private CarManager mCarManager;
    private FragmentFactory mFragmentFactory;
    private ActivityPolicyClient mActivityPolicyClient;
    private EventProxyClient mEventProxyClient;
    private  CanbusProxyClient canbusProxyClient;
    private ActivityState mActivityState;
    private Revers mRevers;
    private CanSpeed carpeed;
    private IWindowChange miWindowChange;
    private  MediaStatus mediaStatus = new MediaStatus();
    //监听集合
    private List<IWindowChange> mWindowChanges = new ArrayList<>();

    /**
     * 初始化页面
     */
    public MainPresent() {
    }

    /* public static MainPresent getInstatnce() {
         if (mPresnet == null) {
             synchronized (MainPresent.class) {
                 mPresnet = new MainPresent();
             }
         }
         return mPresnet;
     }*/
    public void initFragment() {
        if (mFragmentFactory == null) {
            mFragmentFactory = new FragmentFactory();
        }
        if (mCarManager == null) {
            mCarManager = new CarManager(App.getContext());
        }
        if (mActivityPolicyClient == null) {
            mActivityPolicyClient = mCarManager.getActivityPolicy(App.getContext());
        }

    }



    /**
     * 清理集合，防止内存泄漏
     */
    public void removeFragment() {
        if (mFragmentFactory != null) {
            mFragmentFactory.cleanFragment();
        }
    }

    /**
     * 注册倒车事件
     */
    public void registReverse() {
        mEventProxyClient = mCarManager.getEventProxy(App.getContext());
         mRevers =  new Revers();
        mEventProxyClient.registerReverseStatus(mRevers);

    }

    /**
     * 注册车速改变回调
     */
    public void regiestCanSpeed(){
         canbusProxyClient = mCarManager.getCanbusProxy(App.getContext());
         carpeed = new CanSpeed();
         canbusProxyClient.registerCarSpeed(carpeed);
    }

    public void setMediaStatusInfo(int type,int status,String title){
        if(mediaStatus !=null){
            mediaStatus.mMediaType = type;//MediaStatus.MEDIA_TYPE_LOCALMUSIC;
            mediaStatus.mMediaStatus = status;//MediaStatus.MEDIA_STATUS_PLAYING;
            mediaStatus.mTitle =title;
            LogUtil.i(TAG, "mediaStatus =" + mediaStatus.toString());
        }
        if (mEventProxyClient != null) {
            mEventProxyClient.setMediaStatus(mediaStatus);
        }
    }

    /**
     * 注册全屏半屏监听
     *
     * @param
     */
    public void registActivitState() {
        LogUtil.i(TAG, "mActivityPolicyClient : " + mActivityPolicyClient);
        if (mActivityPolicyClient == null) {
            LogUtil.i(TAG, "mActivityPolicyClient is null ");
            return;
        } else {
            mActivityState = new ActivityState();
            mActivityPolicyClient.registerActivityCallback(mActivityState);

        }

    }

    /**
     * 反注册 全半屏监听
     *
     * @param
     */
    public void unRegistActivityState() {
        if (mActivityPolicyClient != null) {
            mActivityPolicyClient.unregisterActivityCallback(mActivityState);
        }

    }

    /**
     * 切换半屏或全屏
     *
     * @param activity
     * @param isfull
     */
    public void checkFullOrHalf(MainActivity activity, boolean isfull) {
        Log.i(TAG,"checkFullOrHalf ="+isfull);
        if (mActivityPolicyClient != null) {
            mActivityPolicyClient.switchLayout(activity, isfull);
        }
    }

    /**
     * 获取应用位置
     */
    public Rect getStackBound(MainActivity activity){
        if(mActivityPolicyClient !=null){
            return mActivityPolicyClient.getStackBounds( activity);
        }
        return null;
    }

    public int getActivityPosition(MainActivity activity) {
        LogUtil.i(TAG, "getActivityPosition");
        if (mActivityPolicyClient != null) {
            LogUtil.i(TAG, "mActivityPolicyClient = " + mActivityPolicyClient.getActivityPosition(activity));
            return mActivityPolicyClient.getActivityPosition(activity);
        } else {
            return 0;
        }

    }




    /**
     * 获取MainFragment数据
     *
     * @return
     */
    public List<BaseLazyLoadFragment> getMainFragmentList() {
        return mFragmentFactory.loadFragments();
    }


    /**
     * 获取UsbFragment数据
     *
     * @return
     */
    public List<BaseLazyLoadFragment> getUsbFragmentList() {
        return mFragmentFactory.loadUsbFragments();
    }


    /**
     * 获取Main当前fragment
     *
     * @param position
     *
     * @return
     */
    public BaseLazyLoadFragment getCurrenFragmen(int position) {
        return mFragmentFactory.getMainCurrentFragmet(position);
    }


    /**
     * 获取Usb当前fragment
     *
     * @param position
     *
     * @return
     */
    public BaseLazyLoadFragment getCurrentUsbFragmen(int position) {
        return mFragmentFactory.getUsbCurrentFragmet(position);
    }

    /**
     * 获取UsbFragment
     *
     * @return
     */
    public BaseUsbFragment getUSbFragment() {
        return (BaseUsbFragment) mFragmentFactory.getMainCurrentFragmet(Configs.PAGE_INDX_USB);
    }

    /**
     * 设置usbIinditer 隐藏与显示
     *
     * @param ishide true 可见  false  不可见
     */
    public void setInditeHide(boolean ishide) {
        BaseUsbFragment fragment1 = (BaseUsbFragment) getUSbFragment();
        if (fragment1 != null) {
            fragment1.setIndicatorVisib(ishide);
        }
    }

    /**
     * 添加全屏半屏监听
     */
    public void setOnWindowChange(IWindowChange iWindowChange) {
        LogUtil.i(TAG, "setOnWindowChange　Listener current is >>>" + iWindowChange.toString());
        // miWindowChange = iWindowChange;
        mWindowChanges.add(iWindowChange);
    }

    public void removerWindowChange(IWindowChange iWindowChange) {
        mWindowChanges.remove(iWindowChange);
        LogUtil.i(TAG, "setOnWindowChange　Listener current is null>");
        // miWindowChange = null;
    }

    /**
     * 添加半屏监听
     */
    public void setOnWindowChangeHalf() {
       /* if (miWindowChange != null) {
            miWindowChange.onWindowChangeHalf();
        }*/
        for (IWindowChange windowChange : mWindowChanges){
            windowChange.onWindowChangeHalf();
        }
    }


    /**
     * 添加全屏监听
     */
    public void setOnWindowChangeFull() {
       /* if (miWindowChange != null) {
            miWindowChange.onWindowChangeFull();
        }*/
        for (IWindowChange windowChange : mWindowChanges){
            windowChange.onWindowChangeFull();
        }
    }


    public void Destory() {
        mWindowChanges.clear();
        if(mEventProxyClient !=null){
            mEventProxyClient.unregisterReverseStatus(mRevers);
        }
        if(canbusProxyClient !=null){
            canbusProxyClient.unregisterCarSpeed(carpeed);
        }
        mPresnet = null;
    }


    class Revers extends IReverseStatus.Stub {

        @Override
        public void onReverseStatus(boolean isRevers) throws RemoteException {
            LogUtil.d(TAG, " isRevers: " + isRevers); //是否倒车状态
        }
    }

    class ActivityState extends IActivityStatus.Stub {

        @Override
        public void onActivityStatus(ComponentName componentName) throws RemoteException {
            LogUtil.i(TAG, "ComponentName :" + componentName);
        }
    }


    class CanSpeed extends ICarSpeed.Stub{
        @Override
        public void onCarSpeed(float v) throws RemoteException {
            if(iICarSpeedListener !=null){
                iICarSpeedListener.onCarSpeedchange();
            }
        }
    }

   public interface ICarSpeedListener{
       /**
        * 车速改变
        */
       void onCarSpeedchange();
    }
    private ICarSpeedListener iICarSpeedListener;
    public void setCarSpeedListener(ICarSpeedListener carSpeedListener){
        this.iICarSpeedListener = carSpeedListener;
    }

}
