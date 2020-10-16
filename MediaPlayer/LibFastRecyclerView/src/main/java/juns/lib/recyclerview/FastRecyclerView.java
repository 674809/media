package juns.lib.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;

import juns.lib.recyclerview.decoration.GridSpacingItemDecoration;
import juns.lib.recyclerview.decoration.SpacesItemDecoration;

/**
 * This class implement RecyclerView and extend other functions.
 * <p>Add item click event.</p>
 * <p>Add item long click event.</p>
 */
public class FastRecyclerView extends RecyclerView {
    //TAG
    private static final String TAG = "FastRecyclerView";

    /**
     * {@link Context}
     */
    private Context mContext;

    /**
     * {@link OnItemTouchListener} object.
     */
    private OnItemTouchListener mOnItemTouchListener;

    /**
     * {@link ItemDecoration} object.
     */
    private ItemDecoration mItemDecoration;

    /**
     * RecyclerView item long click listener
     */
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    /**
     * RecyclerView item click listener
     */
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public FastRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public FastRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FastRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    /**
     * 支持GridLayoutManager以及StaggeredGridLayoutManager
     */
    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params,
                                                   int index, int count) {
        LayoutManager layoutManager = this.getLayoutManager();
        if (getAdapter() != null && (layoutManager instanceof GridLayoutManager
                || layoutManager instanceof StaggeredGridLayoutManager)) {

            GridLayoutAnimationController.AnimationParameters animationParams =
                    (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

            if (animationParams == null) {
                animationParams = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParams;
            }

            int columns = 0;
            if (layoutManager instanceof GridLayoutManager) {
                columns = ((GridLayoutManager) layoutManager).getSpanCount();
            } else {
                columns = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            }

            animationParams.count = count;
            animationParams.index = index;
            animationParams.columnsCount = columns;
            animationParams.rowsCount = count / columns;

            final int invertedIndex = count - 1 - index;
            animationParams.column = columns - 1 - (invertedIndex % columns);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns;

        } else {
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }

    /**
     * Set animation of items.
     * <p>Must execute after {#link {@link #setLayoutManager(LayoutManager)}}</p>
     * 缩放动画
     * @param animation {@link Animation}
     */
    public void setLayoutAnimation(Animation animation) {
        LayoutManager layoutManager = getLayoutManager();
        // GridLayout
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutAnimationController controller = new GridLayoutAnimationController(animation);
            controller.setColumnDelay(0.2f);
            controller.setRowDelay(0.3f);
            controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
            setLayoutAnimation(controller);

            // LinearLayout
        } else if (layoutManager instanceof LinearLayoutManager) {
            LayoutAnimationController controller = new LayoutAnimationController(animation);
            controller.setDelay(0.1f);
            controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
            setLayoutAnimation(controller);
        }
    }

    /**
     * Set item space
     * <p>Must execute after {#link {@link #setLayoutManager(LayoutManager)}}</p>
     *
     * @param space Space value.
     */
    public void setItemSpace(int space, boolean isSetEdge) {
        LayoutManager layoutManager = getLayoutManager();
        // GridLayout
        if (layoutManager instanceof GridLayoutManager) {
            GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(space);
            itemDecoration.setEdgeFlag(isSetEdge);
            addItemDecoration(itemDecoration);
            // LinearLayout
        } else if (layoutManager instanceof LinearLayoutManager) {
            SpacesItemDecoration itemDecoration = new SpacesItemDecoration(space);
            itemDecoration.setEdgeFlag(isSetEdge);
            addItemDecoration(itemDecoration);
        }
    }

    @Override
    public void addItemDecoration(ItemDecoration decor) {
        // Remove duplicate.
        if (mItemDecoration != null) {
            removeItemDecoration(mItemDecoration);
            mItemDecoration = null;
        }
        // Record.
        mItemDecoration = decor;
        super.addItemDecoration(decor);
    }

    @Override
    public void addOnItemTouchListener(OnItemTouchListener listener) {
        super.addOnItemTouchListener(listener);
        mOnItemTouchListener = listener;
    }

    /**
     * Set item click listener.
     *
     * @param l {@link OnItemClickListener}
     */
    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
        if (mOnItemTouchListener == null) {
            addOnItemTouchListener(new RecyclerOnItemClick());
        }
    }

    /**
     * Set item long click listener.
     *
     * @param l {@link OnItemLongClickListener}
     */
    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        mOnItemLongClickListener = l;
        if (mOnItemTouchListener == null) {
            addOnItemTouchListener(new RecyclerOnItemClick());
        }
    }

    /**
     * RecyclerView item click event implements.
     */
    private class RecyclerOnItemClick implements OnItemTouchListener {
        private GestureDetector mmGestureDetector;
        private View mmChildView;
        private RecyclerView mmTouchView;

        RecyclerOnItemClick() {
            mmGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if (mmChildView != null && mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mmChildView,
                                mmTouchView.getChildLayoutPosition(mmChildView));
                    }
                    return true;
                    //return super.onSingleTapUp(e);
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    //super.onLongPress(e);
                    if (mmChildView != null && mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onItemLongClick(mmChildView,
                                mmTouchView.getChildLayoutPosition(mmChildView));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            mmGestureDetector.onTouchEvent(e);
            mmChildView = rv.findChildViewUnder(e.getX(), e.getY());
            mmTouchView = rv;
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}
