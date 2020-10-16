package com.egar.usbimage.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class ImgSwitcherPager extends ViewPager {
    private boolean mCanScroll;

    public ImgSwitcherPager(Context context) {
        super(context);
    }

    public ImgSwitcherPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (mCanScroll) {
            return super.canScroll(v, checkV, dx, x, y);
        }
        return false;
    }

    public void setScroll(boolean canScroll) {
        mCanScroll = canScroll;
    }
}
