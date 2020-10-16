package com.egar.usbmusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.fragment.folder_fragment.FavoritesFragment;

import java.util.logging.Logger;

/**
 * 自定义的首字母滑动条,半屏时通过ScrollView滑动显示
 *
 * @author yangbofeng
 */
public class IndexTitleScrollView extends View {
    public static final String TAG = "IndexTitleScrollView";
    private int height;
    private int width;
    private final char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', '#'};// 5+13+9
    private int mIndex = 0;
    private OnIndexListener mChanged;
    Paint paint;
    private final int textSize = 14;
    private float iHeight;
    private boolean isScroll = false;
    private boolean isEnabl = true;


    public IndexTitleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Log.i(TAG, "IndexTitleScrollView ");
        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER); // 文字对齐，CENTER表示绘制的文字居中显示
        paint.setTextSize(textSize);
        paint.setStrokeWidth(0.7f); //设置描边宽度
        paint.setAntiAlias(true);//抗锯齿 - [避免绘制的视图边缘有模糊毛边，呈锯齿形]
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.BLACK);

    }

    /**
     * 光标位置，(0~26)
     *
     * @param index
     */
    public void setIndex(int index) {
        if (index >= 0 && index <= 26) {
            this.mIndex = index;
            invalidate();
        }
    }

    /**
     * 得到当前的位置
     *
     * @return
     */
    public int getCurrentIndex() {
        return mIndex;
    }

    /**
     * 得到当前的字符
     *
     * @return
     */
    public char getCurrentChar() {
        return chars[mIndex];
    }

    /**
     * 光标所指的字符，(A ~Z,#)
     *
     * @param c
     */
    public void setIndex(char c) {
        setIndex(findCharIndex(c));
    }

    public int findCharIndex(char c) {
        Log.i(TAG,"findCharIndex = "+c);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == c) {
                return i;
            }
        }
        return -1;
    }

    public void registIndexChanged(OnIndexListener mIndexChanged) {
        mChanged = mIndexChanged;
    }

    public void refresh(){
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (height == 0)
            height = 662;//;
       // height = getHeight();
        if (width == 0)
            width = getWidth();

        Log.i(TAG, "width->" + getWidth() +"::>height="+height);
        // Log.i(TAG, "height->" + getHeight());
        if (iHeight == 0) {
            iHeight = ((float) height / (float) (chars.length));
            int[] location = new int[2];
            getLocationOnScreen(location);
            view_x = location[0];
            view_y = location[1];
            view_x = width / 2;
            Log.i(TAG, "view_x->" + view_x);
            Log.i(TAG, "view_y->" + view_y);
        }
        // Log.i(TAG, "iHeight->" + iHeight);
        for (int i = 0; i < chars.length; i++) {

            paint.setColor(Color.TRANSPARENT);
            //canvas.drawRect(0, (float) i * iHeight, getRight(), iHeight * (float) (i + 1), paint);
            paint.setTextSize(textSize * 0.93f);
            if (i == mIndex) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.WHITE);
            }
            // cos30 = 0.866 cos60 = 0.5
            if(MainActivity.isFull){
                if (isCicle) {
                    if (i == mIndex - 2) {
                        canvas.drawText(chars[i] + "", view_x + 20, (float) i * iHeight+12, paint);
                    } else if (i == mIndex - 1) {
                        canvas.drawText(chars[i] + "", view_x + 30, (float) i * iHeight+12, paint);
                    } else if (i == mIndex) {
                        canvas.drawText(chars[i] + "", view_x + 40, (float) i * iHeight+12, paint);
                    } else if (i == mIndex + 1) {
                        canvas.drawText(chars[i] + "", view_x + 30, (float) i * iHeight+12, paint);
                    } else if (i == mIndex + 2) {
                        canvas.drawText(chars[i] + "", view_x + 20, (float) i * iHeight+12, paint);
                    } else {
                        canvas.drawText(chars[i] + "", view_x, (float) i * iHeight+12, paint);
                    }
                } else {
                    canvas.drawText(chars[i] + "", view_x, (float) i * iHeight+12, paint);
                }
            }else {
                canvas.drawText(chars[i] + "", view_x, (float) i * iHeight+12, paint);
            }

        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        Log.i(TAG,"dispatchTouchEvent ="+MainActivity.isFull);
        if(MainActivity.isFull){
            return super.dispatchTouchEvent(event);
        }else {
            return false;
        }


    }

    private float downX;
    private float downY;
    private boolean isCicle = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 记录按下的坐标
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getRawX();
            downY = event.getRawY();
            Log.i(TAG, "onTouchEvent  downX  = " + downX +"   downY = "+downY);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // x，y移动的距离小于10就出发点击事件
            if (Math.abs(downX - event.getRawX()) < 10 && Math.abs(downY - event.getRawY()) < 10) {

                int index = (int) ((event.getRawY() - view_y) / iHeight);
                Log.i(TAG, "onclick here index =" + index);
                setIndex(index);
                onClickChar(index);
            } else {
                isCicle = false;
                invalidate();
                onStopChanged(mIndex);
            }
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            isCicle = true;
            float x = event.getRawX();
            float y = event.getRawY();
            // Log.i(TAG, "; move x ->" + x + "; move y ->" +
            // y);

            if (x > getRight()) {
                Log.i(TAG, "IS OVER ");
                // do nothing
            } else {
                refreshIndex(x, y);
            }
        }
        return isEnabl;
    }

    private int view_x;
    private int view_y;

    // 刷新位置值，同时刷新视图
    private void refreshIndex(float x, float y) {
        // TODO Auto-generated method stub
        Log.i(TAG, "refreshIndex ");
        if (view_y + mIndex * iHeight < y
                && y < (mIndex + 1) * iHeight + view_y) {
            // index不需要改变，
            // do nothing
            // Log.i(TAG, "no need change");
        } else {
            float i = (y - view_y) / iHeight;
            // Log.i(TAG, "index will is  " + i);
            if (i < 0 || (int) i > chars.length - 1) {

            } else {
                mIndex = (int) i;
                Log.i(TAG, "refreshIndex mIndex = "+mIndex);
                invalidate();
                onIndexChanged(mIndex);
            }
        }
    }
    public void setChar(float Y){
        LogUtil.d(TAG,"set char is full ="+MainActivity.isFull);
        if(!MainActivity.isFull){
            int index = (int) ((Y - view_y) / iHeight);
            Log.i(TAG, "onclick here index =" + index);
            setIndex(index);
            onClickChar(index);
            invalidate();
        }

    }
    public float getDownX(){
    	return view_x;
    }

    public float getDownY(int i){
    	return (float)i*iHeight;
    }

    /**
     * 监听首字母定位事件
     */
    public interface OnIndexListener {
        public void onIndexChanged(int index, char c);

        public void onStopChanged(int index, char c);

        public void onClickChar(int index, char c);

    }


    private void onIndexChanged(int index) {
        isScroll = true;
        if (mChanged != null)
            mChanged.onIndexChanged(index, chars[index]);
    }

    private void onStopChanged(int index) {
        isScroll = false;
        if (mChanged != null)
            mChanged.onStopChanged(index, chars[index]);

    }

    private void onClickChar(int index) {
        isScroll = false;
        if (mChanged != null)
            mChanged.onClickChar(index, chars[index]);
    }

    public boolean isScroll() {
        return isScroll;
    }

    public void isEnabled(boolean isenabl ){
        isEnabl = isenabl;
    }
}
