package com.egar.usbimage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;

public class DefaultTxtImageView extends AppCompatImageView {

    private Paint mPaint;
    private static final int DEFAULT_TXT_FONT_SIZE = 20;

    public DefaultTxtImageView(Context context) {
        super(context);
        init(context);
    }

    public DefaultTxtImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DefaultTxtImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setText(String txt) {
        setContentDescription(txt);
        invalidate();
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(context.getColor(android.R.color.black));
        mPaint.setTextSize(DEFAULT_TXT_FONT_SIZE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       /* Drawable srcDrawable = getDrawable();
        if (srcDrawable == null) {
            String contentDesc = getContentDescription().toString();
            if (!TextUtils.isEmpty(contentDesc)) {
                int width = getWidth();
                int height = getHeight();
                canvas.drawText(contentDesc, width / 2, height / 2, mPaint);
            }
        }*/
    }
}
