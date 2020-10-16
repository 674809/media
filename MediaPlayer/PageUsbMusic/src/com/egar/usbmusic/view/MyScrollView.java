package com.egar.usbmusic.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.egar.mediaui.App;
import com.egar.mediaui.MainActivity;
import com.egar.usbmusic.fragment.folder_fragment.FavoritesFragment;

public class MyScrollView extends ScrollView {
    private String TAG ="MyScrollView";

    public IOnScrollXY iOnScrollXY;
    public MyScrollView(Context context) {
        super(context);

    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
     Log.i(TAG,"isFull ="+MainActivity.isFull);
       if(MainActivity.isFull){
           return super.onInterceptTouchEvent(ev);
       }else {
           return false;
       }
    }


    private float downX;
    private float downY;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!MainActivity.isFull){
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                downX = ev.getRawX();
                downY = getScrollY()+ev.getRawY();
                if(iOnScrollXY !=null){
                    iOnScrollXY.onTouchScrollXY(downX,downY);
                }
                Log.i(TAG, "onTouchEvent  downX  = " + downX +"   downY = "+downY);
            }
        }

        return super.onTouchEvent(ev);
    }

    public void setOnScrollXYLinstener(IOnScrollXY xy){
        this.iOnScrollXY = xy;
    }
    public interface IOnScrollXY{
        void onTouchScrollXY(float downx ,float downY);
    }
}
