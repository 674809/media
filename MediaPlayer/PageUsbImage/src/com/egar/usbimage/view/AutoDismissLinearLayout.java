package com.egar.usbimage.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AutoDismissLinearLayout extends LinearLayout {

    private Handler mHandler;
    private static final int DISMISS_WAIT_TIME = 3000;// Unit is second.

    public AutoDismissLinearLayout(Context context) {
        super(context);
    }

    public AutoDismissLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoDismissLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void autoDismiss() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        animGone();
    }

    public interface LinearLayoutAutoDimissListener{
        void onLinearLayoutDimiss(boolean isDimiss);
    }

    private LinearLayoutAutoDimissListener mLinearLayoutAutoDimissListener;

    public void setLinearLayoutAutoDimissListener(LinearLayoutAutoDimissListener autoDimissListener){
        this.mLinearLayoutAutoDimissListener = autoDimissListener;
    }

    private void animGone() {
        setVisibility(GONE);
        if (mLinearLayoutAutoDimissListener != null){
            mLinearLayoutAutoDimissListener.onLinearLayoutDimiss(true);
        }
    }

    public void autoShow() {
        animShow();

        //
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animGone();
            }
        }, DISMISS_WAIT_TIME);
    }

    private void animShow() {
        setVisibility(VISIBLE);
        if (mLinearLayoutAutoDimissListener != null){
            mLinearLayoutAutoDimissListener.onLinearLayoutDimiss(false);
        }
    }

    public void clear() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
