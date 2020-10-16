package com.egar.radio.presenter;




import com.egar.mediaui.util.LogUtil;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public abstract class RadioUIBasePresenter<T>  {
    private  static final String TAG = "RadioBasePresenter";
    protected Reference<T> mViewRef;
    public void attachView(T view){
        LogUtil.i(TAG,"attachView()  view: " + view);
        mViewRef = new WeakReference<T>(view);

    }
    protected T getView(){
        LogUtil.i(TAG,"getView()  mViewRef: " + mViewRef);
        return mViewRef.get();
    }


    public boolean isActivityAttached(){
        boolean isAttached = mViewRef != null && mViewRef.get() != null;
        LogUtil.i(TAG,"isActivityAttached()  isAttached: " + isAttached);
        return isAttached;
    }

    public void detachView(){
        LogUtil.i(TAG,"detachView()");
        if(mViewRef != null){
            mViewRef.clear();
            mViewRef=null;
        }
    }


}
