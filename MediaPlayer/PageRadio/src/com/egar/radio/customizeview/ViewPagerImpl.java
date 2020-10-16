package com.egar.radio.customizeview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.egar.radio.utils.LogUtil;

/**
 * {@link ViewPager} Implement
 */
public class ViewPagerImpl extends ViewPager {

    private static final  String TAG = "ViewPagerImpl";
    private boolean mIsScrollEnable = true;


    public ViewPagerImpl(Context context) {
        super(context);
    }

    public ViewPagerImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollEnable(boolean isScrollEnable) {
        mIsScrollEnable = isScrollEnable;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (mIsScrollEnable) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //LogUtil.d(TAG,"dispatchTouchEvent()   event: " + ev.getAction()    + "   /mIsScrollEnable:   " + mIsScrollEnable);
        // 是否允许滑动
        if (mIsScrollEnable) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }

    }
}
