package juns.lib.recyclerview.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * LinearLayoutManager - 设置item间距
 *
 * @author Jun.Wang
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    //
    private int mItemOffSetFlag = 0;
    private boolean mSetEdge;

    //
    private int mSpace;
    private int mLeft, mTop, mRight, mBottom;

    public SpacesItemDecoration(int space) {
        this.mSpace = space;
    }

    public SpacesItemDecoration(int left, int top, int right, int bottom) {
        this.mLeft = left;
        this.mTop = top;
        this.mRight = right;
        this.mBottom = bottom;
        this.mItemOffSetFlag = 1;
    }

    public void setEdgeFlag(boolean setEdge) {
        mSetEdge = setEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        try {
            //
            if (mItemOffSetFlag == 1) {
                outRect.set(mLeft, mTop, mRight, mBottom);
                return;
            }

            //
            RecyclerView.ViewHolder childViewHolder = parent.getChildViewHolder(view);
            int itemViewType = childViewHolder.getItemViewType();
            switch (itemViewType) {
                case 0:
                    // Which position is current item in ?
                    int rowCurrent = parent.getChildAdapterPosition(view);// 索引？

                    // Get total count of item.
                    int rowCount = parent.getAdapter().getItemCount();
                    // Set edge
                    if (mSetEdge) {
                        if (rowCurrent == rowCount - 1) { // Final row.
                            outRect.set(mSpace, mSpace, mSpace, mSpace);
                        } else { // Other rows.
                            outRect.set(mSpace, mSpace, mSpace, 0);
                        }

                        // No edge
                    } else {
                        if (rowCurrent == 0) { // 1`st row.
                            outRect.set(0, 0, 0, 0);
                        } else { // Other rows.
                            outRect.set(0, mSpace, 0, 0);
                        }
                    }
                    break;
                default:
                    outRect.set(0, 0, 0, 0);
                    break;
            }
        } catch (Exception e) {
        }
    }
}
