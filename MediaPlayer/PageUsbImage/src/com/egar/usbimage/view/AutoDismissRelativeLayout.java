package com.egar.usbimage.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class AutoDismissRelativeLayout extends RelativeLayout {

    private Handler mHandler;
    private static final int DISMISS_WAIT_TIME = 3000;// Unit is second.

    public AutoDismissRelativeLayout(Context context) {
        super(context);
    }

    public AutoDismissRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoDismissRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Dismiss
     */
    public void autoDismiss() {
        animGone();
    }

    public void animGone() {
        setVisibility(GONE);
        if (mRelativeLayoutAutoDimissListener != null){
            mRelativeLayoutAutoDimissListener.onRelativeLayoutDimiss(true);
        }
    }
    public interface RelativeLayoutAutoDimissListener{
        void onRelativeLayoutDimiss(boolean isDimiss);
    }

    private RelativeLayoutAutoDimissListener mRelativeLayoutAutoDimissListener;

    public void setRelativeLayoutAutoDimissListener(RelativeLayoutAutoDimissListener autoDimissListener){
        this.mRelativeLayoutAutoDimissListener = autoDimissListener;
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

    public void animShow() {
        setVisibility(VISIBLE);
        if (mRelativeLayoutAutoDimissListener != null){
            mRelativeLayoutAutoDimissListener.onRelativeLayoutDimiss(false);
        }
    }

    public void clear() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
