package juns.lib.recyclerview.decoration;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * GridLayoutManager - 设置item间距
 *
 * @author Jun.Wang
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    //
    private int mItemOffSetFlag = 0;
    private boolean mSetEdge;

    //
    private int mSpace;
    private int mLeft, mTop, mRight, mBottom;

    public GridSpacingItemDecoration(int space) {
        this.mSpace = space;
        this.mItemOffSetFlag = 0;
    }

    public GridSpacingItemDecoration(int left, int top, int right, int bottom) {
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
    public void getItemOffsets(Rect outRect,
                               View view,
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
                    int whichPos = parent.getChildAdapterPosition(view);// 索引？
                    int whichPosOrder = whichPos + 1;// 第几个？

                    // Get column count
                    int columnCount = 1;
                    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {
                        columnCount = ((GridLayoutManager) layoutManager).getSpanCount();
                    }
                    // Which column is current item in ?
                    int whichColumn = whichPosOrder % columnCount;// 第几列？
                    if (whichColumn == 0) {
                        whichColumn = columnCount;
                    }

                    // Get total count of item.
                    int itemCount = parent.getAdapter().getItemCount();
                    // Get row count
                    int rowCount = itemCount / columnCount;
                    if (rowCount % columnCount > 0) {
                        rowCount = rowCount + 1;
                    }
                    // Which row is current item in ?
                    int whichRow = whichPosOrder / columnCount;// 第几行？
                    if (whichRow * columnCount < whichPosOrder) {
                        whichRow = whichRow + 1;
                    }

                    // Set edge
                    if (mSetEdge) {
                        if (whichRow == rowCount) {
                            if (whichColumn % columnCount == 0) { // Final column
                                outRect.set(mSpace, mSpace, mSpace, mSpace);
                            } else {// Other columns
                                outRect.set(mSpace, mSpace, 0, mSpace);
                            }
                        } else {// Other columns
                            if (whichColumn % columnCount == 0) { // Final column
                                outRect.set(mSpace, mSpace, mSpace, 0);
                            } else {
                                outRect.set(mSpace, mSpace, 0, 0);
                            }
                        }

                        // No edge
                    } else {
                        // 1`st row.
                        if (whichRow == 1) {
                            if (whichColumn % columnCount == 1) { // 1`st column
                                outRect.set(0, 0, 0, 0);
                            } else {// Other columns
                                outRect.set(mSpace, 0, 0, 0);
                            }
                            // Other rows
                        } else {
                            if (whichColumn % columnCount == 1) { // 1`st column
                                outRect.set(0, mSpace, 0, 0);
                            } else { // Other columns
                                outRect.set(mSpace, mSpace, 0, 0);
                            }
                        }
                    }
                    break;
                default:
                    outRect.set(0, 0, 0, 0);
                    break;
            }
        } catch (Exception ignored) {
        }
    }
}
