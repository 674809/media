package com.egar.usbmusic.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.egar.mediaui.R;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/11 14:20
 * @see {@link }
 */
public class MySeekBar extends android.support.v7.widget.AppCompatSeekBar{
    // 比例对应的原点分辨率
    private Drawable thumb;
    private Resources res;
    private Paint paint;
    private Bitmap bmp;
    private Drawable mThumb;

    public MySeekBar(Context context) {
        this(context, null);
    }

    @SuppressWarnings("deprecation")
    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(getResources().getColor(R.color.wiht));
        res = context.getResources();

        bmp = BitmapFactory.decodeResource(res, R.drawable.seekbar_btn);
        Bitmap mbmp = zoomImg(bmp,15,15);
        thumb = new BitmapDrawable(mbmp);

        paint.setTextSize(25);
        // 设置拖动的图片

        setThumb(thumb);
        // 图片的位置
        setThumbOffset(thumb.getIntrinsicWidth());
    }

    /*等比缩放图片*/
    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    @Override
    public void setThumb(Drawable thumb) {
        // TODO Auto-generated method stub
        super.setThumb(thumb);
        this.mThumb = thumb;
    }

    public Drawable getSeekBarThumb() {
        return mThumb;
    }

    // 设置thumb的偏移数值
    @Override
    public void setThumbOffset(int thumbOffset) {
        // TODO Auto-generated method stub
        super.setThumbOffset(thumbOffset / 4);
    }

    String temp_str = "0";

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.save();
        int data = Integer.parseInt(temp_str);

        Rect rect = getSeekBarThumb().getBounds();
        float fontwidth = paint.measureText(temp_str);
   /*     if (data < 10) {
            canvas.drawText(temp_str, rect.left + (rect.width()) / 2.0F, rect.top - paint.ascent()
                    + (rect.height() - (paint.descent() - paint.ascent())) / 2.0F, paint);
        } else {
            canvas.drawText(temp_str, rect.left + (rect.width()) / 2.0F, rect.top - paint.ascent()
                    + (rect.height() - (paint.descent() - paint.ascent())) / 2.0F, paint);
        }*/

        canvas.restore();
    }

    public void SetValue(String value) {
        StringBuffer sb = new StringBuffer();
        sb.append(value);
        temp_str = sb.toString();
        invalidate();
    }

    @SuppressLint("NewApi")
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setOnSeekBarChangeListener(final OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (l != null) {
                    l.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (l != null) {
                    l.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (l != null) {
                    l.onStopTrackingTouch(seekBar);
                }
            }
        });
    }
}
