package com.egar.mediaui.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/12 10:23
 * @see {@link }
 */
@SuppressLint("AppCompatCustomView")
public class PassThroughButton extends Button {


    private Bitmap mBitmap;

    public PassThroughButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int color = mBitmap.getPixel((int) event.getX(),
                    (int) event.getY());
            if (color == 0) {
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w == 0 && h == 0 && oldw == 0 && oldh == 0) {
            super.onSizeChanged(w, h, oldw, oldh);
        } else {
            final BitmapDrawable bkg = (BitmapDrawable) getBackground();
            if(mBitmap == null){
                mBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) bkg.getCurrent()).getBitmap(),
                        getWidth(), getHeight(), true);
            }

        }
    }

}
