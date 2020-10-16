package com.egar.radio.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egar.manager.radio.Freq;
import com.egar.manager.radio.RadioManager;
import com.egar.mediaui.App;
import com.egar.mediaui.Icallback.IFinishActivity;
import com.egar.mediaui.Icallback.ITouchListener;
import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.adapter.MainFragAdapter;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.lib.NoScrollViewPager;
import com.egar.mediaui.util.LogUtil;
import com.egar.radio.contract.RadioUIContract;
import com.egar.radio.customizeview.CustomTextView;
import com.egar.radio.customizeview.FavoriteListFullDialog;
import com.egar.radio.customizeview.ModifyNickNameDialog;
import com.egar.radio.customizeview.SearchDialog;
import com.egar.radio.presenter.RadioUIPresenter;
import com.egar.radio.utils.CollectionFreq;
import com.egar.radio.utils.Values;

import java.util.ArrayList;


/**
 * PAGE - Radio
 */
public class RadioMainFragment extends BaseLazyLoadFragment implements IFinishActivity,
        RadioUIContract.RadioUIViewInterface,View.OnClickListener,AdapterView.OnItemClickListener,
        ModifyNickNameDialog.ModifyNickNameDelegate {
    // TAG
    private static final String TAG = "RadioMainFragment";

    //==========Widgets in this Fragment==========
    private View contentV;

    //==========Variables in this Fragment==========
    // Attached activity of this fragment.
    private MainActivity mAttachedActivity;
    private Context mContext;
    private RadioUIPresenter mRadioUIPresenter;
    private TextView mTvFreqFull;
    private TextView mTvHZFull;
    private ImageView mIvRadiouiSearchPreFull;
    private ImageView mIvRadiouiSearchNextFull;
    private ImageView mIvRadiouiStepPreFull;
    private ImageView mIvRadiouiStepNextFull;

    private TextView mTvFreqHalf;
    private TextView mTvHZHalf;
    private ImageView mIvRadiouiSearchPreHalf;
    private ImageView mIvRadiouiSearchNextHalf;
    private ImageView mIvRadiouiStepPreHalf;
    private ImageView mIvRadiouiStepNextHalf;

    private TextView mTvRadiouiStationlist;
    private RelativeLayout mRlRadiouiPlayMain;
    private RelativeLayout mRlRadiouiStationList;
    private ListView mLvRadiouiStationList;
    private RelativeLayout mRlRadiouiStationListBack;
    private TextView mTvRadiouiFm;
    private TextView mTvRadiouiAm;
    private NoScrollViewPager mVpRadiouiCollection;
    private MainFragAdapter mFragAdapter;
    private ViewPagerOnChange mViewPageOnChange;
    private ImageButton mBtnRadiouiCollectionPagePre;
    private ImageButton mBtnRadiouiCollectionPageNext;
    private ArrayList<BaseAppV4Fragment> mListFrags = new ArrayList<>();

    private EditText mETRadiouiModityNickname;
    private RelativeLayout mRLRadiouiMocifyNicknameBack;
    private ModifyNickNameDialog mModifyNickNameDialog;
    private ImageView mIVRadiouiRenameDeleteAll;
    private ImageView mIVRadiouiRenameRemove;


    //回调touch事件
    private ArrayList<ITouchListener> touchListeners = new ArrayList<>();
    private CustomTextView mTvRadiouiStationListRefresh;
    private RelativeLayout mRlRadiouiStationListTypeFM;
    private RelativeLayout mRlRadiouiStationListTypeAM;
    private RelativeLayout mRlRadiouiFull;
    private RelativeLayout mRlRadiouiHalf;
    private SearchDialog mSearchDialog;
    private boolean isFullScreen;
    private int mVpLeft = Values.VIEWSIZE_VP_LEFT;
    private RelativeLayout mRLRadiouiModifyNickname;
    private static Handler mHandler = new Handler();
    private FavoriteListFullDialog mFavoriteListFullDialog;

    private static  final  int MESSAGE_DISSMISS_FAVORITELISTFULL_DIALOG = 1001;



    private  Handler mRadioUIViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_DISSMISS_FAVORITELISTFULL_DIALOG:
                    if((null!= mFavoriteListFullDialog)&&(mFavoriteListFullDialog.isShowing())){
                        mFavoriteListFullDialog.dismiss();
                    }
                    break;
            }
        }
    };


    @Override
    public int getPageIdx() {
        return Configs.PAGE_IDX_RADIO;
    }

    public boolean getScreenFullState(){
        LogUtil.d(TAG,"getScreenFullState()   isFullScreen:  " + isFullScreen);
        return isFullScreen;
    }


    /**
     * 全屏回调
     *
     */
    @Override
    public void onWindowChangeFull() {
        LogUtil.i(TAG + "  onWindowChangeFull");
        isFullScreen = true;
        visibleFullWindowView(true);

    }

    /**
     * 半屏回调
     *
     */
    @Override
    public void onWindowChangeHalf() {
        LogUtil.i(TAG + "  onWindowChangeHalf");
        isFullScreen = false;
        visibleFullWindowView(false);
    }

    /**
     * 全屏和半屏布局切换
     *
     */
    private void visibleFullWindowView(boolean visibleFullView) {
        LogUtil.d(TAG,"visibleFullWindowView()  visible: " + visibleFullView);
        if(visibleFullView){
            mRlRadiouiFull.setVisibility(View.VISIBLE);
            mRlRadiouiHalf.setVisibility(View.INVISIBLE);
            if(null!=mModifyNickNameDialog){
                mModifyNickNameDialog.setScreenState(true);
            }
        }else {
            mRlRadiouiFull.setVisibility(View.INVISIBLE);
            mRlRadiouiHalf.setVisibility(View.VISIBLE);
            if(null!=mModifyNickNameDialog){
                mModifyNickNameDialog.setScreenState(false);
            }
        }
    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.d(TAG,"onAttach()");
        mContext = App.getContext();
        mAttachedActivity = (MainActivity) getActivity();
    }


    public void dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d(TAG,"dispatchTouchEvent()  ev:  " + ev);
    }


    /**
     * 初始化控件
     *
     * 初始化半屏和全屏控件，根据设备半屏和全屏状态显示
     * 相应的布局和控件
     *
     */
    @Override
    public void initView() {
        LogUtil.d(TAG,"initView()");

        mRlRadiouiFull = (RelativeLayout)findViewById(R.id.rl_radioui_full);
        mRlRadiouiHalf = (RelativeLayout)findViewById(R.id.rl_radioui_half);


        mTvFreqFull = (TextView) findViewById(R.id.tv_radioui_freqs_full);
        mTvHZFull = (TextView) findViewById(R.id.tv_radioui_hz_full);

        mTvFreqHalf = (TextView) findViewById(R.id.tv_radioui_freqs_half);
        mTvHZHalf = (TextView) findViewById(R.id.tv_radioui_hz_half);



        //向上 向下搜 ，步进按钮
        mIvRadiouiSearchPreFull = (ImageView)findViewById(R.id.iv_radioui_search_pre_full);
        mIvRadiouiSearchPreFull.setOnClickListener(this);

        mIvRadiouiSearchNextFull = (ImageView)findViewById(R.id.iv_radioui_search_next_full);
        mIvRadiouiSearchNextFull.setOnClickListener(this);
        mIvRadiouiStepPreFull = (ImageView)findViewById(R.id.iv_radioui_step_pre_full);
        mIvRadiouiStepPreFull.setOnClickListener(this);
        mIvRadiouiStepNextFull = (ImageView)findViewById(R.id.iv_radioui_step_next_full);
        mIvRadiouiStepNextFull.setOnClickListener(this);


        mIvRadiouiSearchPreHalf = (ImageView)findViewById(R.id.iv_radioui_search_pre_half);
        mIvRadiouiSearchPreHalf.setOnClickListener(this);

        mIvRadiouiSearchNextHalf = (ImageView)findViewById(R.id.iv_radioui_search_next_half);
        mIvRadiouiSearchNextHalf.setOnClickListener(this);
        mIvRadiouiStepPreHalf = (ImageView)findViewById(R.id.iv_radioui_step_pre_half);
        mIvRadiouiStepPreHalf.setOnClickListener(this);
        mIvRadiouiStepNextHalf = (ImageView)findViewById(R.id.iv_radioui_step_next_half);
        mIvRadiouiStepNextHalf.setOnClickListener(this);


        mRlRadiouiPlayMain = (RelativeLayout)findViewById(R.id.rl_radioui_play_main);

        //可用电台列表布局和ListView
        mRlRadiouiStationList = (RelativeLayout)findViewById(R.id.rl_radioui_station_list);
        mLvRadiouiStationList= (ListView)findViewById(R.id.lv_radioui_station_list);
        mLvRadiouiStationList.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mLvRadiouiStationList.setVerticalScrollBarEnabled(true);
        mLvRadiouiStationList.setScrollbarFadingEnabled(true);
        mLvRadiouiStationList.setOnItemClickListener(this);

        //可用电台列表页面返回按键和Title
        mRlRadiouiStationListBack = (RelativeLayout)findViewById(R.id.rl_radioui_station_list_back);
        mRlRadiouiStationListBack.setOnClickListener(this);
        mTvRadiouiStationlist = (TextView)findViewById(R.id.tv_radioui_stationlist);
        mTvRadiouiStationlist.setOnClickListener(this);
        mTvRadiouiStationlist.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG,"initView()   getListViewRight:  " + mTvRadiouiStationlist.getRight());
                //mTvRadiouiStationlist.scrollTo((int)(mTvRadiouiStationlist.getRight()+(mContext.getResources().getDimension(R.dimen.radioui_stationlist_scollbar_marginleft))),0);
                mTvRadiouiStationlist.scrollTo((int)(mContext.getResources().getDimension(R.dimen.radioui_stationlist_scollbar_coordinate_x)),0);
            }
        });
        mTvRadiouiStationListRefresh = (CustomTextView)findViewById(R.id.tv_radioui_station_list_refresh);
        mTvRadiouiStationListRefresh.setOnClickListener(this);
        mRlRadiouiStationListTypeFM = (RelativeLayout)findViewById(R.id.rl_radioui_stationlist_band_type_fm);
        mRlRadiouiStationListTypeFM.setOnClickListener(this);
        mRlRadiouiStationListTypeAM = (RelativeLayout)findViewById(R.id.rl_radioui_stationlist_band_type_am);
        mRlRadiouiStationListTypeAM.setOnClickListener(this);


        //底部的波段显示控件和收藏夹列表控件 适配器  切换收藏夹页面按键
        mTvRadiouiFm = (TextView) findViewById(R.id.tv_radioui_fm);
        mTvRadiouiFm.setOnClickListener(this);
        mTvRadiouiAm = (TextView) findViewById(R.id.tv_radioui_am);
        mTvRadiouiAm.setOnClickListener(this);
        mVpRadiouiCollection = (NoScrollViewPager) findViewById(R.id.vp_radioui_collection);
        mVpRadiouiCollection.setNoScroll(true);
        mVpRadiouiCollection.post(new Runnable() {
            @Override
            public void run() {
                inflateViewSize( mVpRadiouiCollection);
            }
        });
        mFragAdapter = new MainFragAdapter(mAttachedActivity.getSupportFragmentManager()) ;
        mVpRadiouiCollection.setAdapter(mFragAdapter);

        mVpRadiouiCollection.setOnPageChangeListener((mViewPageOnChange = new ViewPagerOnChange()));
        mBtnRadiouiCollectionPagePre = (ImageButton)findViewById(R.id.btn_radioui_collection_page_pre);
        mBtnRadiouiCollectionPageNext = (ImageButton)findViewById(R.id.btn_radioui_collection_page_next);
        mBtnRadiouiCollectionPagePre.setOnClickListener(this);
        mBtnRadiouiCollectionPageNext.setOnClickListener(this);


        //修改电台名称
        mRLRadiouiModifyNickname = (RelativeLayout)findViewById(R.id.radioui_modify_nickname_rl);
        mETRadiouiModityNickname = (EditText)findViewById(R.id.radioui_modity_nickname_et);
        mIVRadiouiRenameDeleteAll = (ImageView)findViewById(R.id.radioui_modify_nickname_rename_deleteall_iv);
        mIVRadiouiRenameRemove = (ImageView)findViewById(R.id.radioui_modify_nickname_rename_remove_iv);
        mRLRadiouiMocifyNicknameBack = (RelativeLayout)findViewById(R.id.radioui_modity_nickname_back_rl);
        mIVRadiouiRenameDeleteAll.setOnClickListener(this);
        mIVRadiouiRenameRemove.setOnClickListener(this);
        mRLRadiouiMocifyNicknameBack.setOnClickListener(this);





        isFullScreen = mAttachedActivity.getScreenState();

        if(isFullScreen){
            //全屏
            visibleFullWindowView(true);
        }else {
            //半屏
            visibleFullWindowView(false);
        }
        initModifyNickNameDialog();
        initSearchDialog();
        initFavoriteListFullDialog();

    }

    private void initFavoriteListFullDialog() {
        mFavoriteListFullDialog = new FavoriteListFullDialog(getActivity());
    }


    private void initModifyNickNameDialog() {
        mModifyNickNameDialog = new ModifyNickNameDialog(this.getActivity());
        mModifyNickNameDialog.register(this);
        mModifyNickNameDialog.setScreenState(getScreenFullState());
    }

    private void inflateViewSize(NoScrollViewPager vpRadiouiCollection) {
        mVpLeft = vpRadiouiCollection.getLeft();
     //   LogUtil.d(TAG,"inflateViewSize()   mVpLeft:   "  + mVpLeft);
    }

    public int getVpLeft(){
        return  mVpLeft;
    }

    private void initSearchDialog() {
        mSearchDialog = new SearchDialog(getActivity());
    }


    /**
     * View Pager adapter
     * viewpage 监听
     */
    private class ViewPagerOnChange implements ViewPager.OnPageChangeListener {

        /**
         * 记录上一次页面索引
         */
        private int mmLastPageIdx = 0;
        private int selectposition = 0;

        /**
         * @param state 1 开始滑动 2 滑动完毕 0 保持不变
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            LogUtil.i(TAG, "TabFragOnPageChange> onPageScrollStateChanged(" + state + ")");
            if(state == 0){
                //onPageSelected(selectposition);
            }
        }

        /**
         * @param pos             表示的当前屏幕显示的左边页面的position
         * @param posOffset       表示的当前屏幕显示的左边页面偏移的百分比
         * @param posOffsetPixels 向右滑动到头再向左滑动到头变化规律
         */
        @Override
        public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) {
            LogUtil.i(TAG, "TabFragOnPageChange> onPageScrolled(" + pos + "," + posOffset + "," + posOffsetPixels + ")");
            selectposition = pos;
        }



        /**
         * @param pos 滑动停止后调用，表示当前选中页面的索引
         */
        @Override
        public void onPageSelected(int pos) {
            LogUtil.i(TAG, "TabFragOnPageChange> onPageSelected(" + pos + ")");


            // 记录上一次页面索引
            mmLastPageIdx = pos;
            LogUtil.i(TAG,"mmLastPageIdx ="+mmLastPageIdx);
            // 刷新title
            try {
                TabFreqCollectFragment frag = (TabFreqCollectFragment)mFragAdapter.getItem(mmLastPageIdx);
                if (frag != null) {
                    frag.loadCollected(true);
                }
            }catch (Exception e){
                LogUtil.e(TAG,"Exception "+e.toString());
            }
            if(mRadioUIPresenter !=null){
                mRadioUIPresenter.onCollectionPageChanged();
            }

        }


        int getPageIdx() {
            return mmLastPageIdx;
        }

        void reset() {
            mmLastPageIdx = 0;
        }


    }

    /**
     * 收藏夹下一页
     */
    public void showCollectsNextPage() {
        LogUtil.i(TAG,"showCollectsNextPage()");
        if(mViewPageOnChange !=null){
            int pageIdx = mViewPageOnChange.getPageIdx();
            int pageMaxNum = getCollectionPageNum();
            pageIdx++;
            if (pageIdx >= pageMaxNum) {
                pageIdx = pageMaxNum;
            }
            mVpRadiouiCollection.setCurrentItem(pageIdx);
        }

    }

    /**
     * 收藏夹上一页
     */
    public void showCollectsPrePage() {
        LogUtil.i(TAG,"showCollectsPrePage()");
        if(mViewPageOnChange!=null){
            int pageIdx = mViewPageOnChange.getPageIdx();
            pageIdx--;
            if (pageIdx <= 0) {
                pageIdx = 0;
            }
            mVpRadiouiCollection.setCurrentItem(pageIdx);
        }
    }

    private int getCollectionPageNum(){
        int pageNumber = mRadioUIPresenter.getCollectionPageNum();
        mVpRadiouiCollection.setOffscreenPageLimit(pageNumber);
        return  pageNumber;
    }

    /**
     * 初始化Presenter
     *
     */
    private void initPresenter() {
        mRadioUIPresenter = new RadioUIPresenter(mLvRadiouiStationList);
        mRadioUIPresenter.setRadioUIViewInterface(this);
    }

    public void onBackPressed() {
        if(mAttachedActivity !=null){
            mAttachedActivity.exitApp();
        }

    }


    @Override
    protected int getLayoutId() {
        return R.layout.radio_frag_main;
    }

    @Override
    public void onPageResume() {
        LogUtil.i(TAG ,"  onPageResume");
    }

    /**
     * 应用销毁或退到后台
     *
     */
    @Override
    public void onPageStop() {
        LogUtil.i(TAG ,"  onPageStop");

    }

    @Override
    public void onPageLoadStart() {
        LogUtil.i(TAG , "  onPageLoadStart()");
        if(mAttachedActivity !=null){
            mAttachedActivity.getMainPresent().setOnWindowChange(this);
            mAttachedActivity.setFinishActivitListener(this);
        }
        if(null==mRadioUIPresenter){
            initPresenter();
        }
        if(mRadioUIPresenter != null){
            mRadioUIPresenter.openRadio();
        }

    }

    /**
     * 切换到其他页面时关闭收音机
     *
     */
    @Override
    public void onPageLoadStop() {
        LogUtil.i(TAG ,"  onPageLoadStop");
        if(mAttachedActivity !=null){
            mAttachedActivity.getMainPresent().removerWindowChange(this);
        }

        
        if(null != mRadioUIViewHandler){
            mRadioUIViewHandler.removeCallbacksAndMessages(null);
        }
        
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mRadioUIPresenter != null){
                    mRadioUIPresenter.onViewDestroy();
                    mRadioUIPresenter.removeRadioUIViewInterface();
                    mListFrags.clear();
                    if(null!=mFragAdapter){
                      mFragAdapter.refresh(mListFrags,true);
                    }
                    mRadioUIPresenter = null;
                }
            }
        },0);

    }


    /**
     * 关闭Media应用时关闭收音机
     *
     */
    @Override
    public void onFinishActivity() {
        LogUtil.d(TAG ,"onFinishActivity()   ");
        onPageLoadStop();
        if(mModifyNickNameDialog !=null){
            mModifyNickNameDialog.unregister(this);
        }

        mModifyNickNameDialog= null;
        mSearchDialog = null;
        mViewPageOnChange = null;
        if(mRadioUIPresenter != null){
            mRadioUIPresenter.onViewDestroy();
        }
        mRadioUIPresenter = null;
        if(mAttachedActivity !=null){
            mAttachedActivity.exitApp();
            mAttachedActivity= null;
        }

    }



    public void onActivityResults(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG,"onActivityResult()");
        if (requestCode == 1 && resultCode == 3) {
            String newName = data.getStringExtra("result");
            LogUtil.d(TAG,"   onActivityResult()    newName:  " + newName);
            if(null!=mRadioUIPresenter){
                mRadioUIPresenter.onGetModifyNickName(newName);
            }
        }
    }

    @Override
    public void clickStationEditView() {
        LogUtil.d(TAG,"clickStationEditView()");
        mRLRadiouiModifyNickname.setVisibility(View.VISIBLE);
        mRlRadiouiStationList.setVisibility(View.INVISIBLE);
        if(null!=mETRadiouiModityNickname){
            mETRadiouiModityNickname.setText("");
        }
    }

    @Override
    public void onFavoritesListIsFull() {
        //TODO  显示收藏夹已满的提示
        if(mFavoriteListFullDialog!=null){
            mFavoriteListFullDialog.show();
            if(null!= mRadioUIViewHandler){
                Message message = new Message();
                message.what = MESSAGE_DISSMISS_FAVORITELISTFULL_DIALOG;
                mRadioUIViewHandler.sendMessageDelayed(message,2500);
            }
        }
    }

    /**
     * 按照当前波段类型刷新UI显示
     *
     * @param band
     * @param freq
     */
    @Override
    public void setFreqInfo(int band, String freq) {
        LogUtil.d(TAG,"setFreqInfo()   band:  " + band  + "   freq:  "  + freq);
        switch (band){
            case RadioManager.RADIO_BAND_FM:
                mTvHZFull.setText(mContext.getString(R.string.fm_freq_unit_tv));
                mTvHZHalf.setText(mContext.getString(R.string.fm_freq_unit_tv));
                break;
            case  RadioManager.RADIO_BAND_AM:
                mTvHZFull.setText(mContext.getString(R.string.am_freq_unit_tv));
                mTvHZHalf.setText(mContext.getString(R.string.am_freq_unit_tv));
                break;
        }
        mTvFreqFull.setText(freq,TextView.BufferType.SPANNABLE);
        mTvFreqHalf.setText(freq,TextView.BufferType.SPANNABLE);

        //实现波段控件FM 和 AM 的选中效果
        refreshBandViewBg(band);
    }

    private void refreshBandViewBg(int band) {
        if(band == RadioManager.RADIO_BAND_FM){
            mTvRadiouiFm.setBackground(mContext.getDrawable(R.drawable.radioui_band_fm_selected));
            mTvRadiouiAm.setBackground(mContext.getDrawable(R.color.radioui_bottombar_bt_bg));
        }else if(band == RadioManager.RADIO_BAND_AM){
            mTvRadiouiAm.setBackground(mContext.getDrawable(R.drawable.radioui_band_am_selected));
            mTvRadiouiFm.setBackground(mContext.getDrawable(R.color.radioui_bottombar_bt_bg));
        }
    }

    @Override
    public void showFirstOpenToast() {
        LogUtil.d(TAG,"showFirstOpenToast()   ");
    }

    /**
     * 展示可用电台列表，刷新相应FM或AM按钮选中状态
     * @param show
     * @param band
     */
    @Override
    public void showStationList(boolean show, int band) {
        if(show){
            mRlRadiouiPlayMain.setVisibility(View.INVISIBLE);
            mRlRadiouiStationList.setVisibility(View.VISIBLE);
            if(band == RadioManager.RADIO_BAND_FM){
                mRlRadiouiStationListTypeAM.setBackground(mContext.getDrawable(R.drawable.radioui_stationlist_bandtype_bt_unselected_bg));
                mRlRadiouiStationListTypeFM.setBackground(mContext.getDrawable(R.drawable.radioui_stationlist_bandtype_bt_selected_bg));
            }else {
                mRlRadiouiStationListTypeAM.setBackground(mContext.getDrawable(R.drawable.radioui_stationlist_bandtype_bt_selected_bg));
                mRlRadiouiStationListTypeFM.setBackground(mContext.getDrawable(R.drawable.radioui_stationlist_bandtype_bt_unselected_bg));
            }
        }else {
            mRlRadiouiPlayMain.setVisibility(View.VISIBLE);
            mRlRadiouiStationList.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 刷新收藏夹UI显示
     *
     * @param collectionPageNumber  收藏夹页数
     * @param freqsList  收藏夹列表
     * @param currentfreq  当前频率，用来高亮显示收藏夹中的当前频率值
     * @param resetView 波段变化需要重建收藏夹页面，频率变化不需要重建收藏夹页面
     */
    @Override
    public void refreshCollect(int collectionPageNumber, ArrayList<CollectionFreq[]> freqsList, Freq currentfreq, boolean resetView) {
        LogUtil.d(TAG,"refreshCollect()  currentfreq： " + currentfreq  + "  addView: " + resetView);
        if(resetView){
            mListFrags.clear();
        }

        int loop = collectionPageNumber;
        LogUtil.d(TAG,"refreshCollect()  loop: " + loop  +  "  freqsList.size:  " + freqsList.size());
        mVpRadiouiCollection.setOffscreenPageLimit(Values.VP_OFF_SCREEN_PAGE_LIMIT);

        if(resetView){
            for (int idx = 0; idx < loop; idx++) {
                TabFreqCollectFragment frag = new TabFreqCollectFragment(this);
                frag.setCollectionFreqsList(freqsList.get(idx));
                frag.setPageIdx(idx);
                frag.setCurrentFreq(currentfreq);
                LogUtil.d(TAG,"[refreshCollect]idx = "+idx);
                mListFrags.add(frag);
            }
        }else {
            for (int idx = 0; idx < loop; idx++) {
                TabFreqCollectFragment frag = (TabFreqCollectFragment)mListFrags.get(idx);
                frag.setCollectionFreqsList(freqsList.get(idx));
                frag.setPageIdx(idx);
                frag.setCurrentFreq(currentfreq);
                LogUtil.d(TAG,"[refreshCollect]idx = "+idx);
                frag.loadCollected(true);
            }
        }

        if(null!=mFragAdapter){
            mFragAdapter.refresh(mListFrags,true);
        }else{
            LogUtil.d(TAG,"refreshCollect()  FragAdapter  is null!" );
        }
    }

    @Override
    public Activity getAttachedActivity() {
        return this.getActivity();
    }

    public int getCurrentBand(){
        if(null != mRadioUIPresenter){
            return   mRadioUIPresenter.getCurrentBand();
        }
        return RadioManager.RADIO_BAND_FM;
    }



    /**
     * 修改收藏夹列表
     *
     * @param position
     * @param isReplace
     */
    public  void modifyCollectionFreqs(int band,int position,boolean isReplace){
        LogUtil.d(TAG,"modifyCollectionFreqs()  position: " + position   + "   /isReplace:   " + isReplace);
        if(null!= mRadioUIPresenter){
            mRadioUIPresenter.modifyCollectFreq(band,position,isReplace,false);
        }
    }

    public boolean checkIsCollected(int freq) {
        if(null!= mRadioUIPresenter){
            mRadioUIPresenter.checkIsCollected(freq);
        }
        return false;
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void setFreqSeekBarProcess(int process) {

    }

    @Override
    public void setFreqSeekBarMax(int maxValue) {

    }

    /**
     * 搜台时使能或禁灰一些功能按钮
     *
     * @param band
     * @param isScanning
     * @param preOrNext
     * @param needResetCollectionPage
     */
    @Override
    public void refreshPageOnScanning(int band, boolean isScanning, boolean preOrNext, boolean needResetCollectionPage) {
        LogUtil.d(TAG,"refreshPageOnScanning()");
        if(isScanning&&(!preOrNext)){
            mTvRadiouiFm.setEnabled(false);
            mTvRadiouiAm.setEnabled(false);
            mTvRadiouiStationlist.setEnabled(false);
            mTvRadiouiStationListRefresh.setEnabled(false);
            mVpRadiouiCollection.setNoScroll(true);
            mRlRadiouiStationListBack.setEnabled(false);

            mIvRadiouiSearchPreFull.setEnabled(false);
            mIvRadiouiSearchNextFull.setEnabled(false);
            mIvRadiouiSearchPreHalf.setEnabled(false);
            mIvRadiouiSearchNextHalf.setEnabled(false);
            mIvRadiouiStepPreFull.setEnabled(false);
            mIvRadiouiStepNextFull.setEnabled(false);
            mIvRadiouiStepPreHalf.setEnabled(false);
            mIvRadiouiStepNextHalf.setEnabled(false);

            //TODO  show dialog
            showSearchDialog(true);
        }

        if(preOrNext&&(!isScanning)){
            mTvRadiouiStationListRefresh.setEnabled(false);
            mTvRadiouiFm.setEnabled(false);
            mTvRadiouiAm.setEnabled(false);
        }

        if((!isScanning)&&(!preOrNext)){
            mTvRadiouiFm.setEnabled(true);
            mTvRadiouiAm.setEnabled(true);
            mTvRadiouiStationlist.setEnabled(true);
            mTvRadiouiStationListRefresh.setEnabled(true);
            mVpRadiouiCollection.setNoScroll(false);
            mRlRadiouiStationListBack.setEnabled(true);

            mIvRadiouiSearchPreFull.setEnabled(true);
            mIvRadiouiSearchNextFull.setEnabled(true);
            mIvRadiouiSearchPreHalf.setEnabled(true);
            mIvRadiouiSearchNextHalf.setEnabled(true);
            mIvRadiouiStepPreFull.setEnabled(true);
            mIvRadiouiStepNextFull.setEnabled(true);
            mIvRadiouiStepPreHalf.setEnabled(true);
            mIvRadiouiStepNextHalf.setEnabled(true);

            //TODO dismiss dialog
            showSearchDialog(false);
        }

    }

    private void showSearchDialog(boolean show) {
        if(mSearchDialog !=null){
            if(show){
                mSearchDialog.show();
            }else {
                mSearchDialog.dismiss();
            }
        }


    }

    @Override
    public void setAllProcess(int process) {

    }

    @Override
    public void destroyView() {

    }

    @Override
    public void refreshFragmentPage(int band, int currentFreq, boolean isSearch, boolean isPreOrNext) {

    }

    @Override
    public boolean isCollectAnimRunning() {
        return false;
    }

    @Override
    public void startSwitchBandAnimation() {

    }


    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     */
    public void registerMyTouchListener(ITouchListener listener) {
        // LogUtil.i("listener =>"+touchListeners.size());
        touchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     */
    public void unRegisterMyTouchListener(ITouchListener listener) {
        touchListeners.remove(listener);
    }


    /**
     * 修改电台名称的确认Dialog点击ok按钮
     *
     */
    @Override
    public void onOkClick() {
        LogUtil.d(TAG,"onOkClick()");
        String name=mETRadiouiModityNickname.getText().toString();

        String newName = "";
        if((null != name )&&(!name.isEmpty())&&(name.length() != 0)&&(!name.trim().isEmpty())){
            newName = mETRadiouiModityNickname.getText().toString();
            if(null!=mRadioUIPresenter){
                mRadioUIPresenter.onGetModifyNickName(newName);
            }
        }
        showStationList(true,mRadioUIPresenter.getCurrentBand());
        mRLRadiouiModifyNickname.setVisibility(View.GONE);
        mModifyNickNameDialog.dismiss();
    }

    /**
     * 修改电台名称的确认Dialog点击no按钮
     *
     */
    @Override
    public void onCancelClick() {
        LogUtil.d(TAG,"onCancelClick()");
        showStationList(true,mRadioUIPresenter.getCurrentBand());
        mRLRadiouiModifyNickname.setVisibility(View.GONE);
        mModifyNickNameDialog.dismiss();
    }



    @Override
    public void onClick(View v) {
        LogUtil.d(TAG,"onClick()");
        if(mRadioUIPresenter == null){
            return;
        }
        switch (v.getId()){
            case R.id.iv_radioui_search_pre_full:
            case R.id.iv_radioui_search_pre_half:
                mRadioUIPresenter.processClickUIView(Values.CLICK_SEARCH_PRE_VIEW);
                break;

            case R.id.iv_radioui_search_next_full:
            case R.id.iv_radioui_search_next_half:
                mRadioUIPresenter.processClickUIView(Values.CLICK_SEARCH_NEXT_VIEW);
                break;

            case R.id.iv_radioui_step_pre_full:
            case R.id.iv_radioui_step_pre_half:
                mRadioUIPresenter.processClickUIView(Values.CLICK_STEP_PRE_VIEW);
                break;

            case R.id.iv_radioui_step_next_full:
            case R.id.iv_radioui_step_next_half:
                mRadioUIPresenter.processClickUIView(Values.CLICK_STEP_NEXT_VIEW);
                break;

            case R.id.tv_radioui_stationlist:
                mRadioUIPresenter.processClickUIView(Values.CLICK_STATION_LIST_VIEW);
                break;

            case R.id.rl_radioui_station_list_back:
                mRadioUIPresenter.processClickUIView(Values.CLICK_STATION_LIST_BACK_VIEW);
                break;

            case R.id.tv_radioui_fm:
                mRadioUIPresenter.processClickUIView(Values.CLICK_BAND_FM_VIEW);
                break;

            case R.id.tv_radioui_am:
                mRadioUIPresenter.processClickUIView(Values.CLICK_BAND_AM_VIEW);
                break;

            case  R.id.tv_radioui_station_list_refresh:
                mRadioUIPresenter.processClickUIView(Values.CLICK_STATION_LIST_REFRESH_VIEW);
                break;

            case R.id.btn_radioui_collection_page_pre:
                showCollectsPrePage();
                break;

            case R.id.btn_radioui_collection_page_next:
                showCollectsNextPage();
                break;

            case R.id.rl_radioui_stationlist_band_type_fm:
                mRadioUIPresenter.processClickUIView(Values.CLICK_STATION_LIST_BAND_TYEP_FM);
                break;

            case R.id.rl_radioui_stationlist_band_type_am:
                mRadioUIPresenter.processClickUIView(Values.CLICK_STATION_LIST_BAND_TYEP_AM);
                break;

            case R.id.radioui_modity_nickname_back_rl:
                showConfirmDialog();
                break;

            case R.id.radioui_modify_nickname_rename_remove_iv:
                int index=mETRadiouiModityNickname.getSelectionStart();   //获取Edittext光标所在位置
                String deleteCharNewName=mETRadiouiModityNickname.getText().toString();
                if (!deleteCharNewName.equals("")) {//判断输入框不为空，执行删除
                    mETRadiouiModityNickname.getText().delete(index-1,index);
                }
                break;

            case R.id.radioui_modify_nickname_rename_deleteall_iv:
                String removeName=mETRadiouiModityNickname.getText().toString();
                if (!removeName.equals("")) {//判断输入框不为空，执行清空
                    mETRadiouiModityNickname.setText("");
                }
                break;
        }
    }

    private void showConfirmDialog() {
        if(null!=mModifyNickNameDialog){
            mModifyNickNameDialog.show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.d(TAG,"onItemClick()  position:  " + position);
        if(null != mRadioUIPresenter){
            mRadioUIPresenter.onListViewItemClick(position);
        }
    }

    /**
     * 调用Presenter 设置频率
     *
     * @param freq
     */
    public void setFreq(int freq){
        if(null != mRadioUIPresenter){
            mRadioUIPresenter.setFreq(freq);
        }
    }

     @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}
