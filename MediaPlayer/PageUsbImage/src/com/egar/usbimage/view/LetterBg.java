package com.egar.usbimage.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Letter with circle background
 *
 * @author Jun.Wang
 */
public class LetterBg extends View {

    //
    private Character mHlLetter;

    public LetterBg(Context context) {
        super(context);
        init(context);
    }

    public LetterBg(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LetterBg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
    }

    public void refreshLetter(Character letter) {
        if (mHlLetter != letter) {
            Log.i("LetterBg", "refreshLetter(" + letter + ")");
            mHlLetter = letter;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //
        final float measuredWidth = getMeasuredWidth(), measuredHeight = getMeasuredHeight();
        final float cX = measuredWidth / 2;
        final float cy = measuredHeight / 2;
        final float outerStrokeWidth = 3;
        final float radius = measuredWidth / 2 - outerStrokeWidth;


        //Draw outer
        @SuppressLint("DrawAllocation")
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(outerStrokeWidth);
        canvas.drawCircle(cX, cy, radius, paint);

        //Draw inner circle
        paint.setColor(Color.parseColor("#66FF0000"));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cX, cy, radius, paint);

        //Draw letter
        if (mHlLetter != null) {
            paint.setStyle(Paint.Style.FILL);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.WHITE);
            paint.setTextSize(70);
            canvas.drawText(String.valueOf(mHlLetter), cX, getTxtCenterY(paint, cy), paint);
        }
    }

    private float getTxtCenterY(Paint txtPaint, float originCenterY) {
        Paint.FontMetrics fontMetrics = txtPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离
        return originCenterY - top / 2 - bottom / 2;//基线中间点的y轴计算公式
    }
}
