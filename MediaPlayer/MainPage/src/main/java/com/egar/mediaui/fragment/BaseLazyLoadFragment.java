package com.egar.mediaui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.egar.mediaui.Icallback.IWindowChange;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.receiver.MediaBoardcast;
import com.egar.mediaui.util.LogUtil;

/**
 * Created by ybf on 2019/4/30.
 */
public abstract class BaseLazyLoadFragment extends Fragment implements IWindowChange {

    public abstract int getPageIdx();

    public abstract void onWindowChangeFull();

    public abstract void onWindowChangeHalf();
/**
 * Fragment预加载问题的解决方案：
 * 1.可以懒加载的Fragment
 * 2.切换到其他页面时停止加载数据（可选）
 */


    /**
     * 视图是否已经初初始化
     */
    protected boolean isInit = false;
    protected boolean isLoad = false;
    protected final String TAG = "LazyLoadFragment";
    protected boolean isVisible = false;//标识fragment是否可见；
    private View mView;
    private  MainActivity mMainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,"onAttach");
        //注册全屏或半屏监听
        mMainActivity = (MainActivity)context;
        Log.i(TAG,"onAttach = "+System.currentTimeMillis());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //防止第二次加载重复调用onCreateView,重新new了一个PagerAdapter 导致Fragment不显示
        LogUtil.i(TAG, "onCreateView");
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
                LogUtil.i(TAG, "removeView");
            }
            return mView;
        }

        mView = inflater.inflate(getLayoutId(), container, false);
        isInit = true;
        initView();
        /**初始化的时候去加载数据**/

        if(isAdded()){
            isCanLoadData();
        }

        return mView;
    }


    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG,"setUserVisibleHint ="+isVisibleToUser);
        isVisible = isVisibleToUser;
        if(isAdded()){
            isCanLoadData();
        }

    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }
        if (isVisible) {
            onPageLoadStart();
            isLoad = true;
        } else {
            if (isLoad) {
                onPageLoadStop();
            }
        }
    }

    /**
     * 初始化view
     * 在onCreate中调用了initView
     */
    public abstract void initView();


    /**
     * 视图销毁的时候讲Fragment是否初始化的状态变为false
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;

    }

    protected void showToast(String message) {
        if (!TextUtils.isEmpty(message)) {
     //       Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 设置Fragment要显示的布局
     *
     * @return 布局的layoutId
     */
    protected abstract int getLayoutId();

    /**
     * 获取设置的布局
     *
     * @return
     */
    protected View getContentView() {
        return mView;
    }

    /**
     * onResume
     */
    public abstract void onPageResume();

    /**
     * onStop
     */
    public abstract void onPageStop();

    /**
     * 找出对应的控件
     *
     * @param id
     * @param <T>
     *
     * @return
     */
    public <T extends View> T findViewById(int id) {

        return (T) getContentView().findViewById(id);
    }

    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    public abstract void onPageLoadStart();

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
     */
    public abstract void onPageLoadStop();


    protected int getPageFirstIndex(int index) {
        if (index % 5 > 0) {
            return (index / 5) * 5;
        }
        return index;

    }

    /**
     * 根据数据位置，计算并获取该数据所在页的第一个数据索引
     */
    protected int getPageFirstPos(int idx) {
        int pageDelta = getPageItemCount();
        if (idx % pageDelta > 0) {
            return (idx / pageDelta) * pageDelta;
        }

        // 0 -> return 0;
        // 1 -> return 0;
        // 2 -> return 0;
        // 6 -> return 5;
        // 10 -> return 10;
        // 11 -> return 10;
        return idx;
    }

    /**
     * Get rows num of records per page.
     *
     * @return rows num of records per page.
     */
    protected int getPageItemCount() {
        return 4;
    }

    public MainActivity getMainActivity(){
        if(mMainActivity == null){
            mMainActivity = (MainActivity)getActivity();
        }
        return mMainActivity;
    }

}
