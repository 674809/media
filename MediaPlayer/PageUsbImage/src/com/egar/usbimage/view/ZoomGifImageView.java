package com.egar.usbimage.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class ZoomGifImageView extends ImageView {
    private static final String TAG = "ZoomGifImageView";

    /**
     * 是否是Debug模式
     */
    private static boolean IS_DEBUG = true;

    /**
     * 缩放工具
     */
    private Matrix mMatrix;

    /**
     * 缩放的 [最小值/中间值/最大值]
     * <p>mMaxScale 最大放大比例，这个是图片放大的上限；</p>
     * <p>mMidScale 这个是中间比例，一般是图片长或宽达到Layout的长或宽；</p>
     * <p>mMinScale 最小缩小比例，这个是图片缩小的下限；</p>
     */
    private float mMinScale, mMidScale, mMaxScale;

    /**
     * 多点手势触 控缩放比率分析器
     */
    private ScaleGestureDetector mScaleGestureDetector;

    /**
     * 记录上一次多点触控的数量
     */
    private int mLastPointerCount;

    private float mLastX;
    private float mLastY;
    private int mTouchSlop;
    private boolean isCanDrag;
    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    //实现双击放大与缩小
    private GestureDetector mGestureDetector;
    private boolean isScaling;
    private List<MotionEvent> events;
    private OnClickListener onClickListener;
    private int arae_img_id = -1;


    /**
     * {@link ZoomGifImgTabListener} Object.
     */
    private ZoomGifImgTabListener mZoomGifImgTabListener;

    public interface ZoomGifImgTabListener {
        void onSingleTap();

        void onDoubleTap();
    }


    public ZoomGifImageView(Context context) {
        this(context, null);
    }

    public ZoomGifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomGifImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.MATRIX);
        mMatrix = new Matrix();
        setOnTouchListener(new MyTouchCallback());
        mScaleGestureDetector = new ScaleGestureDetector(context, new MultipleGestureCallback());
        mGestureDetector = new GestureDetector(context, new GestureCallback());
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        events = new ArrayList<>();
    }

    public void setTapListener(ZoomGifImgTabListener l) {
        this.mZoomGifImgTabListener = l;
    }

    /**
     * Touch Event Callback of this view.
     */
    private class MyTouchCallback implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (mGestureDetector.onTouchEvent(motionEvent)) {
                return true;
            }

            //将触摸事件传递给ScaleGestureDetector
            if (motionEvent.getPointerCount() > 1) {
                mScaleGestureDetector.onTouchEvent(motionEvent);
            }


            // Finger touch point count
            int pointerCount = motionEvent.getPointerCount();
            //Log.i(TAG, "MyTouchCallback >> pointerCount: " + pointerCount);

            float x = 0;
            float y = 0;
            for (int i = 0; i < pointerCount; i++) {
                x += motionEvent.getX(i);
                y += motionEvent.getY(i);
            }
            x /= pointerCount;
            y /= pointerCount;

            if (mLastPointerCount != pointerCount) {
                isCanDrag = false;
                mLastX = x;
                mLastY = y;
            }
            mLastPointerCount = pointerCount;

            // MotionEvent process.
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    Log.i(TAG, "-ACTION_DOWN-");
                    transforTouchEvent();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    Log.i(TAG, "-ACTION_MOVE-" + getX());
                    //
                    float dx = x - mLastX;
                    float dy = y - mLastY;
                    if (!isCanDrag) {
                        isCanDrag = isMoveAction(dx, dy);
                    }
                    if (isCanDrag) {
                        RectF rectF = getMatrixRectF();
                        if (getDrawable() != null) {
                            isCheckLeftAndRight = true;
                            if (rectF.width() <= getWidth()) {
                                isCheckLeftAndRight = false;
                                dx = 0;
                            }

                            isCheckTopAndBottom = true;
                            if (rectF.height() <= getHeight()) {
                                isCheckTopAndBottom = false;
                                dy = 0;
                            }

                            mMatrix.postTranslate(dx, dy);
                            checkBorderWhenTranslate();
                            setImageMatrix(mMatrix);
                        }
                    }
                    mLastX = x;
                    mLastY = y;

                    // Check touch event.
                    transforTouchEvent();
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                    Log.i(TAG, "-ACTION_CANCEL-");
                    mLastPointerCount = 0;
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "-ACTION_UP-");
                    mLastPointerCount = 0;
                    break;
            }
            return true;
        }

        /**
         * 检测Touch事件的传递，即是否让父控件继续处理Touch事件，还是自行消费完毕
         */
        private void transforTouchEvent() {
            RectF rect = getMatrixRectF();
            Log.i(TAG, "(" + rect.left + ", " + rect.right + ") | " + getWidth() + " / " + rect.width());
            if ((rect.width() > getWidth() + 0.01f || (rect.height() > getHeight() + 0.01f))) {
                // 图片扩张后的宽度 <= Layout的宽度
                // 图片最左侧未超过Layout的最左侧 && 图片最右侧未超过Layout的最右侧。
                //      * * * * * * * * * * *
                //      *   *           *   *   Layout
                //      *   *           *   *
                //      *   *  IMG      *   *
                //      *   *           *   *
                //      *   *           *   *
                //      *   *           *   *
                //      * * * * * * * * * * *
                if (rect.left >= 0 && rect.right <= getWidth()) {
                    try {
                        // 由父控件决定是否要处理Touch事件
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } catch (Exception ignored) {
                    }

                    // 图片扩张后的宽度 > Layout的宽度
                    // 图片最右侧未滑到Layout的最右侧 && 图片的最左侧未滑到Layout的最左侧。
                    //      * * * * * * * * * * *
                    //      *     Layout        *
                    //  * * * * * * * * * * * * * * *
                    //  *   *     IMG           *   *
                    //  *   *                   *   *
                    //  * * * * * * * * * * * * * * *
                    //      *                   *
                    //      * * * * * * * * * * *
                } else if ((rect.right != getWidth()) && (rect.left != 0)) {
                    try {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } catch (Exception e) {
                        log(e.toString());
                    }
                }
            }
        }
    }

    /**
     * {@link ScaleGestureDetector} callback.
     */
    private class MultipleGestureCallback implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            Log.i(TAG, "onScaleBegin()");
            return true; //缩放开始,返回true 用于接收后续事件
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.i(TAG, "onScale()");
            float scaleFactor = detector.getScaleFactor();//获取用户手势判断出来的缩放值
            float scale = getScale();

            // 没有图片
            if (getDrawable() == null) {
                return true;
            }

            //缩放范围控制
            if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mMinScale && scaleFactor < 1.0f)) {
                if (scaleFactor * scale < mMinScale) {
                    scaleFactor = mMinScale / scale;
                }

                if (scale * scaleFactor > mMaxScale) {
                    scaleFactor = mMaxScale / scale;
                }

                mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                checkBorderAndCenterWhenScale();
                setImageMatrix(mMatrix);
            }
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            Log.i(TAG, "onScaleEnd()");
        }
    }

    /**
     * {@link GestureDetector} callback.
     */
    private class GestureCallback extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "onDoubleTap()");
            if (mZoomGifImgTabListener != null) {
                mZoomGifImgTabListener.onDoubleTap();
            }
            if (isScaling || getScale() >= mMaxScale)
                return true;
            isScaling = true;
            float x = e.getX();
            float y = e.getY();

            if (getScale() < mMidScale) {
                postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
            } else {
                postDelayed(new AutoScaleRunnable(mMinScale, x, y), 16);
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "onSingleTapConfirmed()");
            if (mZoomGifImgTabListener != null) {
                mZoomGifImgTabListener.onSingleTap();
            }
            if (onClickListener != null) {
                onClickListener.onClick(ZoomGifImageView.this);
                return true;
            }
            return false;
        }
    }

    private class AutoScaleRunnable implements Runnable {
        /**
         * 要缩放的目标值
         */
        private float mTargetScale;
        private float x; //缩放的中心点x
        private float y; //缩放的中心点y
        private float tmpScale;

        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;

        AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;

            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALL;
            }
        }

        @Override
        public void run() {
            mMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mMatrix);


            float currentScale = getScale();
            if ((tmpScale > 1.0f && currentScale < mTargetScale)
                    || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                postDelayed(this, 16);
            } else {
                float scale = mTargetScale / currentScale;
                mMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mMatrix);
                isScaling = false;
            }
        }
    }

    /**
     * 获取当前的缩放比率
     */
    private float getScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(TAG, "onAttachedToWindow()");
        getViewTreeObserver().addOnGlobalLayoutListener(mViewTreeObserverOnGlobalLayout);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow()");
        getViewTreeObserver().removeOnGlobalLayoutListener(mViewTreeObserverOnGlobalLayout);
    }

    private ViewTreeObserver.OnGlobalLayoutListener mViewTreeObserverOnGlobalLayout = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Log.i(TAG, "onGlobalLayout()");
            if (getDrawable() == null) {
                Log.i(TAG, "NOTE ::: There is no image");
                return;
            }

            // Check layout size.
            int layoutWidth = getWidth();
            int layoutHeight = getHeight();
            if (layoutWidth == 0 || layoutHeight == 0) {
                Log.i(TAG, "NOTE ::: Layout has not been initialized.");
                return;
            }

            // Not initialized.
            Log.i(TAG, "onGlobalLayout() -Execute initialize-");
            // 屏幕高宽比
            //int layoutWidth = getWidth();
            //int layoutHeight = getHeight();
            float layoutWeight = ((float) layoutHeight) / layoutWidth;

            Log.i(TAG, "onGlobalLayout: layoutWidth = "+layoutWidth+" ,layoutHeight = "+layoutHeight+" ,layoutWeight = "+layoutWeight);

            // 图片高宽比
            Drawable ivDrawable = getDrawable();
            int imageHeight = ivDrawable.getIntrinsicHeight(); // 图片高度
            int imageWidth = ivDrawable.getIntrinsicWidth(); // 图片宽度
            float imageWeight = ((float) imageHeight) / imageWidth;

            Log.i(TAG, "onGlobalLayout: imageHeight = "+imageHeight+" ,imageWidth = "+imageWidth+" ,imageWeight = "+imageWeight);

            // * * * * * * *
            // *           *        * * * * * * *
            // *           *        * Drawable  *
            // * Layout    *        * Enlarge   *
            // *           *        *           *
            // * * * * * * *        * * * * * * *
            // 如果当前屏幕高宽比 大于等于 图片高宽比,就缩放图片
            // 即当图片的宽度等于Layout的宽度时，Layout的高度要比图片的高度高。
            if (layoutWeight >= imageWeight) {
                float scale = 1.0f;
                //图片比当前View宽,但是比当前View矮
                if (imageWidth > layoutWidth && imageHeight < layoutHeight) {
                    scale = layoutWidth * 1.0f / imageWidth; //根据宽度缩放
                }
                //图片比当前View窄,但是比当前View高
                if (imageHeight > layoutHeight && imageWidth < layoutWidth) {
                    scale = layoutHeight * 1.0f / imageHeight; //根据高度缩放
                }
                //图片高宽都大于当前View,那么就根据最小的缩放值来缩放
                if (imageHeight > layoutHeight && imageWidth > layoutWidth) {
                    scale = Math.min(layoutWidth * 1.0f / imageWidth, layoutHeight * 1.0f / imageHeight);
                    log("max scale:" + scale);
                }
                //图片高宽都小于当前View,那么就根据最小的缩放值来缩放
                if (imageHeight < layoutHeight && imageWidth < layoutWidth) {
                    scale = Math.min(layoutWidth * 1.0f / imageWidth, layoutHeight * 1.0f / imageHeight);
                    log("min scale:" + scale);
                }

                // 设置缩放比率
                mMinScale = scale;
                mMidScale = mMinScale * 2;
                mMaxScale = mMinScale * 4;
                Log.i(TAG, "onGlobalLayout() -(screenWeight >= imageWeight)- {mMinScale:" + mMinScale + ", mMaxScale:" + mMaxScale + "}-");

                //                      * * * * * * *
                // * * * * * * *        *           *
                // *           *        * Drawable  *
                // * Layout    *        * Enlarge   *
                // *           *        *           *
                // * * * * * * *        * * * * * * *
                // 即当图片的宽度等于Layout的宽度时，Layout的高度要比图片的高度低。
            } else {
                //将宽度缩放至屏幕比例缩放(长图,全图预览)
                float scale = ((float) layoutWidth) / imageWidth; // 将图片的宽度放大至屏幕的宽度
                // 设置缩放比率
                //最大比例，将使图片左右可放大超出Layout
                mMaxScale = scale * 2;
                // 中间比例，高度放大至最大，即图片长或宽均不应该超过Layout的长或宽
                mMidScale = ((float) layoutHeight) / imageHeight;
                // 最小比例
                mMinScale = mMidScale / 2;
                Log.i(TAG, "onGlobalLayout() -(screenWeight < imageWeight)- {mMinScale:" + mMinScale + ", mMaxScale:" + mMaxScale + "}-");

                // 把图片移动到中心点去
                mMatrix.reset();
                mMatrix.preScale(mMidScale, mMidScale, 0, 0);
                checkBorderAndCenterWhenScale();
            }

            // Set matrix
            setImageMatrix(mMatrix);
        }
        //}
    };

    /**
     * 打印日志
     *
     * @param value 要打印的日志
     */
    public static void log(String value) {
        if (IS_DEBUG)
            Log.w(TAG, value);
    }

    /**
     * 在移动图片的时候进行边界检查
     */
    private void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top;
        }

        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }


        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }

        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }

        mMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(mMatrix);
    }

    /**
     * 判断是否足以触发移动事件
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }

    /**
     * 在缩放的时候进行边界,位置 检查
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.width() >= width) {
            if (rectF.left > 0)
                deltaX = -rectF.left;
            if (rectF.right < width)
                deltaX = width - rectF.right;
        }

        if (rectF.height() >= height) {
            if (rectF.top > 0)
                deltaY = 0;
            if (rectF.bottom < height)
                deltaY = height - rectF.bottom;
        }

        if (rectF.width() < width) {
            deltaX = width / 2f - rectF.right + rectF.width() / 2;
        }

        if (rectF.height() < height) {
            deltaY = height / 2f - rectF.bottom + rectF.height() / 2;
        }

        mMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(mMatrix);
    }

    /**
     * 获取图片放大缩小后的宽高/top/left/right/bottom
     *
     * @return
     */
    private RectF getMatrixRectF() {
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();

        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mMatrix.mapRect(rectF);
        }

        return rectF;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        resetState();
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageResource(int resId) {
        resetState();
        super.setImageResource(resId);
    }

    /**
     * 设置初始化状态为false
     */
    public void resetState() {
        setTag(null);
        mMatrix.reset();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
    }

    /**
     * 设置加载中的占位图
     *
     * @param resID
     */
    public void placeholder(int resID) {
        this.arae_img_id = resID;
    }
}
