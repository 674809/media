package com.egar.usbvideo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;


@SuppressLint("AppCompatCustomView")
public class SeekBarImpl extends SeekBar {
    //TAG
    private static final String TAG = "SeekBarImpl";

    private boolean mIsCanSeek = true;

    public SeekBarImpl(Context context) {
        super(context);
        init();
    }

    public SeekBarImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SeekBarImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        post(new Runnable() {
            @Override
            public void run() {
                try {
                    Rect bounds = getProgressDrawable().getBounds();
                    setTag(bounds);
                    Log.i(TAG, "Set TAG - Bounds:" + bounds);
                } catch (Exception e) {
                    Log.i(TAG, "Set TAG - Error :: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void setProgressDrawable(Drawable d) {
        super.setProgressDrawable(d);
        try {
            Object objOriginBounds = getTag();
            if (objOriginBounds != null) {
                getProgressDrawable().setBounds((Rect) objOriginBounds);
                Log.i(TAG, "Resume Bounds successfully !~!");
            }
        } catch (Exception e) {
            Log.i(TAG, "Resume Bounds - ERROR :: " + e.getMessage());
        }
    }

    public void setCanSeek(boolean isCanSeek) {
        mIsCanSeek = isCanSeek;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return !mIsCanSeek || super.dispatchTouchEvent(event);
    }
}
