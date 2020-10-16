package com.js.sidebar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Letter bar
 *
 * @author Jun.Wang
 */
public class LetterSideBar extends View {
    //TAG
    private static final String TAG = "LetterSideBar";

    // Common paint
    private Paint mPaintCom;
    // Paint on touching.
    private Paint mPaintTouch;

    // 常规/高亮 颜色
    private int mFontColor, mHlFontColor;

    // 按压时选中的字母位置
    private int mLastTouchPos = -1;
    private int mTouchPos = -1;

    // 每个字母占有的区域高度
    private float mStepH;

    //Letters list
    private List<String> mListLetters = new ArrayList<>();
    private Character mHlLetter;

    /**
     * {@link LetterSideBarListener}
     */
    private LetterSideBarListener mListener;

    public interface LetterSideBarListener {
        /**
         * Callback when touch letters.
         *
         * @param pos    Letter position that u touched.
         * @param letter Letter that u touched.
         */
        void callback(int pos, String letter);

        /**
         * {@link MotionEvent#ACTION_DOWN}
         */
        void onTouchDown();

        /**
         * {@link MotionEvent#ACTION_MOVE}
         */
        void onTouchMove();

        /**
         * {@link MotionEvent#ACTION_UP}
         */
        void onTouchUp();
    }

    /*Constructor*/
    public LetterSideBar(Context context) {
        super(context);
        init(context);
    }

    /*Constructor*/
    public LetterSideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /*Constructor*/
    public LetterSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Initialize
     * <p>(1) [common font color] and [highlight font color]</p>
     *
     * @param context {@link Context}
     */
    private void init(Context context) {
        mFontColor = getResources().getColor(android.R.color.white, null);
        mHlFontColor = Color.parseColor("#FF0000");

        //
        mPaintCom = new Paint();
        mPaintTouch = new Paint();
    }

    /**
     * Set [common font color] and [highlight font color] by yourself.
     *
     * @param fontColor   Target [common font color].
     * @param hlFontColor Target [highlight font color].
     */
    public void setColor(int fontColor, int hlFontColor) {
        if (fontColor != -1) {
            mFontColor = fontColor;
        }
        if (hlFontColor != -1) {
            mHlFontColor = hlFontColor;
        }
    }

    public void refreshHlLetter(Character hlLetter) {
        if (mHlLetter != hlLetter) {
            Log.i(TAG, "refreshHlLetter(" + hlLetter + ")");
            mHlLetter = hlLetter;
            invalidate();
        }
    }

    public void refreshLetters(List<String> listLetters) {
        if (listLetters == null || listLetters.size() == 0) {
            String[] strArr = new String[]{"A", "B", "C", "D", "E", "F", "G",
                    "H", "I", "J", "K", "L", "M", "N",
                    "O", "P", "Q",
                    "R", "S", "T",
                    "U", "V", "W",
                    "X", "Y", "Z",
                    "#"};
            listLetters = Arrays.asList(strArr);
        }

        //
        mListLetters.clear();
        mListLetters.addAll(listLetters);

        //
        invalidate();
    }

    public void addCallback(LetterSideBarListener l) {
        mListener = l;
    }

    // [非高亮/高亮] 字符最大高度
    private static final float FONT_SIZE_MAX_COM = 30f, FONT_SIZE_MAX_HL = 40f;
    // [非高亮/高亮] 字符大小加权值，这是为了让[单个字符所占区域高度 >= 单个字符实际高度]
    private static final float HEIGHT_WEIGHT_COM = 0.93f, HEIGHT_WEIGHT_HL = 1f;
    // 字符描边宽度 加权值，即每个字符的笔划宽度粗细加权
    private static final float STROKE_WEIGHT = 0.7f;
    // 字符偏移量
    private static final int FONT_OFFSET_TO_BASE_POS = 10;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw(Canvas)");
        //Check null
        if (mListLetters == null || mListLetters.size() == 0) {
            return;
        }

        // 字母数
        final int loop = mListLetters.size();
        // 每个字母占有的区域高度
        mStepH = ((float) getMeasuredHeight()) / loop;
        // 每个字母大小
        float fontSize = mStepH * HEIGHT_WEIGHT_COM; // 高亮字符
        if (fontSize > FONT_SIZE_MAX_COM) {
            fontSize = FONT_SIZE_MAX_COM;
        }
        float fontSizeTouch = mStepH * HEIGHT_WEIGHT_HL; // 高亮字符
        if (fontSizeTouch > FONT_SIZE_MAX_HL) {
            fontSizeTouch = FONT_SIZE_MAX_HL;
        }
        // 每个字母绘制x坐标
        final float xCom = (float) getMeasuredWidth() / 2;

        // 画笔 - 用来绘制普通字符
        mPaintCom.setColor(mFontColor);
        mPaintCom.setTextAlign(Paint.Align.CENTER); // 文字对齐，CENTER表示绘制的文字居中显示
        mPaintCom.setTextSize(fontSize);
        mPaintCom.setStrokeWidth(STROKE_WEIGHT); //设置描边宽度
        mPaintCom.setAntiAlias(true);//抗锯齿 - [避免绘制的视图边缘有模糊毛边，呈锯齿形]
        mPaintCom.setStyle(Paint.Style.FILL_AND_STROKE);

        // 画笔 - 用来绘制高亮字符
        mPaintTouch.setColor(mHlFontColor);
        mPaintTouch.setTextAlign(Paint.Align.CENTER);
        mPaintTouch.setTextSize(fontSizeTouch);
        mPaintTouch.setStrokeWidth(STROKE_WEIGHT);
        mPaintTouch.setAntiAlias(true);
        mPaintTouch.setStyle(Paint.Style.FILL_AND_STROKE);

        // 遍历 并 绘制 所有字符
        for (int idx = 0; idx < loop; idx++) {
            String letter = mListLetters.get(idx);
            if (TextUtils.isEmpty(letter) || TextUtils.isEmpty(letter.trim())) {
                continue;
            }

            // A
            // B
            //   C
            //     D
            //   E
            // F
            // G
            float yDelta = (mStepH - fontSize) / 2; // Y方向绘制的偏差
            float y = mStepH * (idx + 1) - yDelta; // 字符的Y方向坐标
            float x = xCom; // 字符的X方向坐标
            if (mTouchPos != -1) {
                switch (Math.abs(mTouchPos - idx)) {
                    case 0:
                        x += FONT_OFFSET_TO_BASE_POS * 3; // 向右偏移
                        break;
                    case 1:
                        x += FONT_OFFSET_TO_BASE_POS * 2; // 向右偏移
                        break;
                    case 2:
                        x += FONT_OFFSET_TO_BASE_POS; // 向右偏移
                        break;
                }
            }

            // 1. [高亮字符] && [TOUCH生效中]，才执行
            if (mTouchPos == idx) {
                canvas.drawText(letter, x, y, mPaintTouch);

                // 绘制[普通字符] 及 [TOUCH未生效情况下的高亮字符]
            } else {
                String hlLetter = (mHlLetter == null) ? "" : mHlLetter.toString();
                // 2. [高亮字符] && [TOUCH未生效]，才执行
                if (TextUtils.equals(hlLetter, letter)) {
                    mPaintCom.setColor(mHlFontColor);
                    // 3. [普通字符]
                } else {
                    mPaintCom.setColor(mFontColor); // 判断是不是需要
                }
                canvas.drawText(letter, x, y, mPaintCom);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Log.i(TAG, "dispatchTouchEvent(" + action + ")");
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "-- ACTION_DOWN --");
                mHlLetter = null;
                refresh(event.getY());
                if (mListener != null) {
                    mListener.onTouchDown();
                }
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "-- ACTION_MOVE --");
                mHlLetter = null;
                refresh(event.getY());
                if (mListener != null) {
                    mListener.onTouchMove();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "-- ACTION_UP --");
                refresh(-1);
                if (mListener != null) {
                    mListener.onTouchUp();
                }
                break;
        }
//        return super.dispatchTouchEvent(event);
        return true;
    }

    private void refresh(float touchY) {
        Log.i(TAG, "refresh(" + touchY + ")");
        if (touchY < 0 || mStepH <= 0 || mListLetters == null || mListLetters.size() == 0) {
            mTouchPos = -1;
            mLastTouchPos = -1;
            invalidate();
            return;
        }

        //Get touch position
        float posFloat = touchY / mStepH;
        mTouchPos = (int) Math.floor(posFloat);
        //Filter multiply
        if (mLastTouchPos == mTouchPos) {
            return;
        }

        //Draw
        invalidate();

        //Callback
        if (mListener != null) {
            try {
                mListener.callback(mTouchPos, mListLetters.get(mTouchPos));
            } catch (Exception e) {
                Log.i(TAG, "refresh(touchY) > " + e.getMessage());
            }
        }

        //Record last position.
        mLastTouchPos = mTouchPos;
    }

    public Character getHlLetter() {
        return mHlLetter;
    }
}
