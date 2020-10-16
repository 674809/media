package com.egar.mediaui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.egar.mediaui.util.LogUtil;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/11 19:24
 * @see {@link }
 */
@SuppressLint("AppCompatCustomView")
public class MyButton extends ImageButton {
    private String TAG ="MyButton";
    private Bitmap bitmap;
    private   Canvas canvas;
    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isTouchPointInView(event.getX(), event.getY()) || event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

    protected boolean isTouchPointInView(float localX, float localY) {
        LogUtil.i(TAG,"isTouchPointInView");
        if(bitmap == null){
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
            canvas = new Canvas(bitmap);
        }
        draw(canvas);
        int x = (int) localX;
        int y = (int) localY;
        if (x < 0 || x >= getWidth())
            return false;
        if (y < 0 || y >= getHeight())
            return false;
        int pixel = bitmap.getPixel(x, y);
        if ((pixel & 0xff000000) != 0) { // 点在非透明区
            return true;
        } else {
            return false;
        }
    }

}
