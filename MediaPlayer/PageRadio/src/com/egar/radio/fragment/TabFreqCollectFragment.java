package com.egar.radio.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egar.manager.radio.Freq;
import com.egar.manager.radio.util.LogUtils;
import com.egar.mediaui.R;
import com.egar.mediaui.util.LogUtil;
import com.egar.radio.utils.CollectionFreq;
import com.egar.radio.utils.FreqFormatUtil;
import com.egar.radio.utils.Values;

import java.util.ArrayList;


/**
 * 收藏夹页封装类
 * 每一页收藏夹封装为一个TabFreqCollectFragment，
 * 每页收藏夹中有五个收藏夹按钮
 *
 */
public class TabFreqCollectFragment extends BaseAppV4Fragment {
    //TAG
    private final String TAG = "FreqCollectFrag";

    protected static final float FLIP_DISTANCE = 50;
    private RadioMainFragment mRadioMainFragment;

    /**
     * ==========Widgets in this Activity==========
     */
    private View contentV;
    private TextView tvItems[] = new TextView[Values.COLLECT_ITEM_NUMBER];
    private int mPageIdx;
    private CollectionFreq[] mCollectionFreqList;
    private Freq mCurrentFreq;
    private ArrayList<ViewSize> mViewSizeList = new ArrayList<>();
    private CustomDialog mDialog;
    private Window mWindow;
    private RelativeLayout mRadioui_collect_dialog_repalce_rl;
    private RelativeLayout mRadioui_collect_dialog_delete_rl;
    private View mLlRadioUIFragCollect;
    private int mFragViewWidth;


    public TabFreqCollectFragment() {
    }

    @SuppressLint("ValidFragment")
    public TabFreqCollectFragment(RadioMainFragment radioMainFragment) {
        this.mRadioMainFragment = radioMainFragment;
    }

    public void setCollectionFreqsList(CollectionFreq[] freqsList){
        mCollectionFreqList = freqsList;
    }

    public void setCurrentFreq(Freq currentFreq){
        LogUtil.d(TAG,"setCurrentFreq:  " + currentFreq  +  "  mPageIdx: " + mPageIdx);
        mCurrentFreq = currentFreq;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
    }


    public void setPageIdx(int pageIdx) {
        mPageIdx = pageIdx;
        LogUtil.d(TAG,"setPageIdx:  " + mPageIdx);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtil.d(TAG,"onAttach() mPageIdx:  " + mPageIdx);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentV = inflater.inflate(R.layout.radioui_frag_collect, null);
        LogUtil.d(TAG,"onCreateView()  mPageIndex: " + mPageIdx);

        return contentV;
    }



    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume()" + this.toString());
        loadCollected(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG,"onActivityCreated()");
        init();
    }

    private void init() {
        // ---- Widgets ----
        initViewSizeList();
        mLlRadioUIFragCollect = contentV.findViewById(R.id.ll_radioui_frag_collect);
        mLlRadioUIFragCollect.post(new Runnable() {
            @Override
            public void run() {
                inflateViewSize(mLlRadioUIFragCollect);
            }
        });

        tvItems[0] = (TextView) contentV.findViewById(R.id.tv_collect1);
        tvItems[0].setOnClickListener(mFilterViewOnClick);
        tvItems[0].setOnLongClickListener(mOnLongClick);
        tvSetTag(tvItems[0],0);
        tvItems[0] .post(new Runnable() {
            @Override
            public void run() {
                inflateViewSize(tvItems[0] );
            }
        });

        tvItems[1] = (TextView) contentV.findViewById(R.id.tv_collect2);
        tvItems[1].setOnClickListener(mFilterViewOnClick);
        tvItems[1].setOnLongClickListener(mOnLongClick);
        tvSetTag(tvItems[1],1);
        tvItems[1] .post(new Runnable() {
            @Override
            public void run() {
                inflateViewSize(tvItems[1] );
            }
        });

        tvItems[2] = (TextView) contentV.findViewById(R.id.tv_collect3);
        tvItems[2].setOnClickListener(mFilterViewOnClick);
        tvItems[2].setOnLongClickListener(mOnLongClick);
        tvSetTag(tvItems[2],2);
        tvItems[2] .post(new Runnable() {
            @Override
            public void run() {
                inflateViewSize(tvItems[2] );
            }
        });

        tvItems[3] = (TextView) contentV.findViewById(R.id.tv_collect4);
        tvItems[3].setOnClickListener(mFilterViewOnClick);
        tvItems[3].setOnLongClickListener(mOnLongClick);
        tvSetTag(tvItems[3],3);
        tvItems[3] .post(new Runnable() {
            @Override
            public void run() {
                inflateViewSize(tvItems[3] );
            }
        });

        tvItems[4] = (TextView) contentV.findViewById(R.id.tv_collect5);
        tvItems[4].setOnClickListener(mFilterViewOnClick);
        tvItems[4].setOnLongClickListener(mOnLongClick);
        tvSetTag(tvItems[4],4);
        tvItems[4] .post(new Runnable() {
            @Override
            public void run() {
                inflateViewSize(tvItems[4] );
            }
        });

        loadCollected(true);
    }

    /**
     * 无法实现动态回去控件坐标，因为有时Fragment会销毁或预加载
     *当页面在第一页时第一页的控件的坐标是在坐标原点右侧，当
     * 页面在第二页好第三页时如果第一页销毁重建过，第一页的控件
     * 坐标在坐标原点的左侧，而且第一页控件坐标随当前页是第二页
     * 或第三页不同，因此指定收藏夹元素控件初始坐标值，然后按照收藏夹元素控件
     * 所属页进行计算
     *
     */
    private void initViewSizeList() {
        mViewSizeList.clear();
        for(int i = 0 ; i < Values.COLLECT_ITEM_NUMBER; i ++){
            ViewSize viewSize = new ViewSize();
            switch (i){
                case 0:
                    viewSize.x = 165;
                    viewSize.y = 882;
                    break;
                case 1:
                    viewSize.x = 289;
                    viewSize.y = 882;
                    break;
                case 2:
                    viewSize.x = 413;
                    viewSize.y = 882;
                    break;
                case 3:
                    viewSize.x = 537;
                    viewSize.y = 882;
                    break;
                case 4:
                    viewSize.x = 661;
                    viewSize.y = 882;
                    break;
            }
            mViewSizeList.add(viewSize);
        }
    }

    private void tvSetTag(TextView tvItem, int collectionPositon) {
        tvItem.setTag(Values.COLLECT_ITEM_NUMBER *mPageIdx+collectionPositon);
    }


    public void loadCollected(boolean refreshBg) {
        LogUtil.d(TAG,"loadCollected()  isAdded(): " + isAdded());
        for(int collectionPosition = 0; collectionPosition < mCollectionFreqList.length;collectionPosition++){
            CollectionFreq collectionFreq = mCollectionFreqList[collectionPosition];
            TextView tv = tvItems[collectionPosition];
            LogUtil.d(TAG,"loadCollected()  collectionFreq:  "  +  collectionFreq  + "   /tv:  " + tv);
            int freq = collectionFreq.getFreq();
            String nickName = collectionFreq.getNickName();
            int band = collectionFreq.getBand();
            LogUtil.d(TAG,"loadCollected()   band:  " + band);
            if((null!= nickName)&&(!nickName.equals(""))){
                tv.setText(nickName);
            }else {
                if(freq != Values.DEFAULT_FREQ_VALUE){
                    tv.setText(FreqFormatUtil.getFreqStr(band, freq));
                }
            }
            if(null!= tv){
                tv.setTag(collectionFreq.getListPosition());
            }
        }
        if(refreshBg){
            refreshItemsBgByCurrFreq(mCurrentFreq.getFreq());
        }
    }


    /**
     * 高亮显示收藏夹中收藏有当前频率的按钮的字体
     *
     * @param currFreq
     */
    public void refreshItemsBgByCurrFreq(int currFreq) {
        LogUtil.d(TAG,"refreshItemsBgByCurrFreq()   currFreq: " + currFreq +"     / isAdded(): " + isAdded()  +  "   mPagerIndex: " + mPageIdx);
        if (isAdded()) {
            for(int collectionPosition = 0 ;collectionPosition < Values.COLLECT_ITEM_NUMBER ; collectionPosition++){
                CollectionFreq collectionFreq  = mCollectionFreqList[collectionPosition];
                if(collectionFreq.getFreq() == currFreq){
                    setBg(tvItems[collectionFreq.getCollectionPosition()],true);
                } else {
                    setBg(tvItems[collectionFreq.getCollectionPosition()],false);
                }
            }
        }
    }



    View.OnLongClickListener mOnLongClick = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            LogUtil.i(TAG, "mOnLongClick> onLongClick");
            int loop = tvItems.length;
            for (int idx = 0; idx < loop; idx++) {
                TextView tv = tvItems[idx];
                if (tv == v) {
                    showCollectFreqDialog(tv, idx);
                    break;
                }
            }
            return true;
        }


    };

    /**
     * 弹出Repalce 和 Delete  的Dialog
     *
     * @param tv
     * @param position
     */
    private void showCollectFreqDialog(final TextView tv, int position) {
        LogUtil.d(TAG,"collectFreq()  position: " + position  +  "    /tv:  " + tv);
        int listPosition = (int)tv.getTag();
        int collectionFreq = mCollectionFreqList[position].getFreq();
        ViewSize viewSize = mViewSizeList.get(position);
        showDialog(viewSize,collectionFreq,listPosition);
    }

    /**
     * 修改收藏夹Dialog
     *
     * @param viewSize
     * @param collectionFreq
     * @param listPosition
     */
    private void showDialog(ViewSize viewSize, int collectionFreq, final int listPosition) {
        LogUtil.d(TAG,"showDialog()   viewSize:   "  +  viewSize);
        mDialog = new CustomDialog(getActivity());

        mWindow = mDialog.getWindow();
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                R.dimen.radioui_collection_dialog_width, R.dimen.radioui_collection_dialog_height,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                        | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER,
                PixelFormat.TRANSLUCENT);

        mDialog.setCanceledOnTouchOutside(true);
        lp.format = PixelFormat.TRANSLUCENT;
        lp.setTitle(this.getClass().getSimpleName());
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        // lp.y = (int) viewSize.y-158-80;
        if(mRadioMainFragment.getScreenFullState()){
            lp.y = (int) viewSize.y-((int)mRadioMainFragment.getContext().getResources().getDimension(R.dimen.status_bar_height))-((int)mRadioMainFragment.getContext().getResources().getDimension(R.dimen.radioui_collection_dialog_height)) - ((int)mRadioMainFragment.getContext().getResources().getDimension(R.dimen.radioui_collection_dialog_marginbottom)) ;
        }else {
            lp.y = (int) viewSize.y-((int)mRadioMainFragment.getContext().getResources().getDimension(R.dimen.status_bar_height))-((int)mRadioMainFragment.getContext().getResources().getDimension(R.dimen.radioui_collection_half_screen_size));
        }

        LogUtil.d(TAG,"  showDialog()    viewSize.x:  " + viewSize.x   +   "   /viewSize.width/2:   " +  viewSize.width/2   +  "   /mFragViewWidth+40:    "  + mFragViewWidth  +  "   /mPageIdx:   "  +  mPageIdx);
        lp.x = (int)viewSize.x;
        LogUtil.d(TAG,"showDialog()    / lp.y:  " + lp.y  + "   /lp.x:   " + lp.x);
        lp.windowAnimations = -1;
        mWindow.setAttributes(lp);
        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);

        mDialog.setContentView(R.layout.radioui_collect_dialog_layout);
        mRadioui_collect_dialog_repalce_rl = (RelativeLayout) mDialog.findViewById(R.id.radioui_collect_dialog_repalce_rl);
        mRadioui_collect_dialog_delete_rl = (RelativeLayout) mDialog.findViewById(R.id.radioui_collect_dialog_delete_rl);

        if((collectionFreq == mCurrentFreq.getFreq())||(mRadioMainFragment.checkIsCollected(mCurrentFreq.getFreq()))){
            //mRadioui_collect_dialog_repalce_rl.setTextColor(getActivity().getResources().getColor(R.color.radioui_collection_dialog_text_gray));
            mRadioui_collect_dialog_repalce_rl.setEnabled(false);
        }
        if(collectionFreq == Values.DEFAULT_FREQ_VALUE){
            //mRadioui_collect_dialog_delete_rl.setTextColor(getActivity().getResources().getColor(R.color.radioui_collection_dialog_text_gray));
            mRadioui_collect_dialog_delete_rl.setEnabled(false);
        }


        mRadioui_collect_dialog_repalce_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d(TAG,"onClick()    Click replace button");
                if(null!= mRadioMainFragment){
                    mRadioMainFragment.modifyCollectionFreqs(mRadioMainFragment.getCurrentBand(),listPosition,true);
                }
                dissMissDialog();
            }
        });

        mRadioui_collect_dialog_delete_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.d(TAG,"onClick()    Click delete button");
                dissMissDialog();
                if(null!= mRadioMainFragment){
                    mRadioMainFragment.modifyCollectionFreqs(mRadioMainFragment.getCurrentBand(),listPosition,false);
                }
            }
        });

        mDialog.show();

    }

    public  void dissMissDialog(){
        if((null!=mDialog)&&(mDialog.isShowing())){
            mDialog.dismiss();
        }
    }



    /**
     * 收藏夹Dailog
     */
    private final class CustomDialog extends Dialog {
        public CustomDialog(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            return super.dispatchTouchEvent(ev);
        }

        @Override
        protected void onStop() {
            super.onStop();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            LogUtils.d(TAG, "dispatchTouchEvent() isOut=" + (event.getAction() == MotionEvent.ACTION_OUTSIDE));
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                //TODO dismiss dialog
                dismiss();
                return false;
            }
            return false;
        }
    }

    public  ViewSize  inflateViewSize(View view){
        LogUtils.d(TAG,"inflateViewSize()");
        ViewSize viewSize = new ViewSize();

        final int[] location = new int[2];
        view.getLocationOnScreen(location);
        viewSize.width = view.getWidth();
        viewSize.height = view.getHeight();
        viewSize.top = view.getTop();
        viewSize.left = view.getLeft();
        viewSize.x = location[0];
        viewSize.y = location[1];

      //  LogUtil.d(TAG,"inflateViewSize()   viewSize:  "  + viewSize);
        if(view == mLlRadioUIFragCollect){
            mFragViewWidth = view.getWidth();
         //   LogUtil.d(TAG,"inflateViewSize()  mFragViewWidth:   " +   mFragViewWidth);
        }else {
            // mViewSizeList.add(viewSize);
        }
        return  viewSize;
    }






    /**
     * 功能按钮尺寸封装内部类，便于计算动画控件的路径坐标
     *
     */
    public class  ViewSize{
        int viewId;
        int width;
        int height;
        int top;
        int left;
        float x;
        float y;

        @Override
        public String toString() {
            return "ViewSize{" +
                    "viewId=" + viewId +
                    ", width=" + width +
                    ", height=" + height +
                    ", top=" + top +
                    ", left=" + left +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }


    private View.OnClickListener mFilterViewOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            LogUtil.i(TAG, "mFilterViewOnClick> onClick");
            int loop = tvItems.length;
            for(int i = 0;i <loop;i++){
                TextView tv = tvItems[i];
                if (tv == v) {
                    LogUtil.d(TAG,"listPositon):  " + (int)v.getTag());
                    CollectionFreq collectionFreq = mCollectionFreqList[i];
                    int freq = collectionFreq.getFreq();
                    if(freq == Values.DEFAULT_FREQ_VALUE){
                        // TODO  ToastView.show(this.getActivity(), R.string.click_collect_toast);
                    }else{
                        mRadioMainFragment.setFreq(freq);
                        refreshItemsBgByCurrFreq(freq);
                    }
                }
            }
        }
    };


    /**
     *  设置收藏夹按钮字体颜色，选中的按钮字体为红色
     *  其余为白色
     *
     * @param view
     * @param selected
     */
    public void setBg(TextView view, boolean selected) {
        LogUtil.d(TAG,"setBg() ");

        if(selected){
            view.setTextColor(getActivity().getApplicationContext().getColor(R.color.radioui_collected_text_selected));
        }else {
            view.setTextColor(getActivity().getApplicationContext().getColor(R.color.radioui_collected_text_unselected));
        }
    }


    // animation按压图片
    public void setBgs(int idx) {
        LogUtil.d(TAG,"setBgs()  idx: " + idx  );

    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(TAG,"onPause()       pageIndex: " + mPageIdx);
        if(null != mDialog){
            mDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG,"onDestroy()    被销毁了  pageIndex: " + mPageIdx);
        if(null != mDialog){
            mDialog.dismiss();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView()  ");

    }


}
