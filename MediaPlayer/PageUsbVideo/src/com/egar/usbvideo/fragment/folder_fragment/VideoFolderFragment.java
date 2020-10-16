package com.egar.usbvideo.fragment.folder_fragment;


import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.interfaces.CollectListener;
import com.egar.usbmusic.view.IndexTitleScrollView;
import com.egar.usbmusic.view.MyScrollView;
import com.egar.usbvideo.adapter.UsbVideoFoldersAdapter;
import com.egar.usbvideo.fragment.UsbVideoMainFragment;
import com.egar.usbvideo.fragment.VideoFoldersFragment;
import com.egar.usbvideo.model.UsbVideoFolderMode;
import com.egar.usbvideo.present.VideoPresent;

import java.util.List;

import juns.lib.media.bean.FilterFolder;
import juns.lib.media.bean.ProVideo;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/28 10:26
 * @see {@link }
 */
public class VideoFolderFragment extends BaseLazyLoadFragment implements CollectListener,
        UsbVideoFolderMode.IVideFolderDataChange, IndexTitleScrollView.OnIndexListener,
        AbsListView.OnScrollListener, AdapterView.OnItemClickListener, MyScrollView.IOnScrollXY {

    private String TAG = "VideoFolderFragment";
    private int mPage = 0;//一级界面
    private ListView listView;
    private UsbVideoMainFragment mFragment;
    private UsbVideoFoldersAdapter mAdapter;
    private IndexTitleScrollView mLetterSidebar;
    private TextView mTvCenterChar;
    private VideoPresent mVideoPresent;
    private UsbVideoFolderMode mUsbVideoFolderMode;
    private boolean mmIsTouchScrolling;
    private List<FilterFolder> mListFilters;
    private List<ProVideo> mAudios;
    private VideoFoldersFragment mVideofragment;
    private MyScrollView mScrollview;
    private static Handler mHandler = new Handler();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int getPageIdx() {
        return mPage;
    }

    @Override
    public void onWindowChangeFull() {

    }

    @Override
    public void onWindowChangeHalf() {

    }

    @Override
    public void initView() {
        mFragment = (UsbVideoMainFragment) getMainActivity().getMainPresent().getCurrentUsbFragmen(Configs.PAGE_IDX_USB_VIDEO);
        listView = findViewById(R.id.lv_folder);
        mAdapter = new UsbVideoFoldersAdapter(getMainActivity());
        mLetterSidebar = findViewById(R.id.video_index);
        mTvCenterChar = findViewById(R.id.tv_center_char);
        listView.setAdapter(mAdapter);
        mVideoPresent = VideoPresent.getInstance();
        mAdapter.setCollectListener(this);
        mUsbVideoFolderMode = new UsbVideoFolderMode();
        mUsbVideoFolderMode.loadFilters();
        mUsbVideoFolderMode.setVideoFolderDataChangeListener(this);
        mLetterSidebar.registIndexChanged(this);
        listView.setOnScrollListener(this);
        listView.setOnItemClickListener(this);

        mScrollview = findViewById(R.id.scroll);
        mScrollview.setOnScrollXYLinstener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.usb_video_folder_fragment;
    }

    @Override
    public void onPageResume() {

    }

    @Override
    public void onPageStop() {

    }

    @Override
    public void onPageLoadStart() {

    }

    @Override
    public void onPageLoadStop() {

    }


    @Override
    public void onClickCollectBtn(ImageView ivCollect, int pos) {

    }

    @Override
    public void VideoFolderDateChage(List<FilterFolder> filterFolders) {
        this.mListFilters = filterFolders;
        ProVideo currMedia = mVideoPresent.getCurrProVideo();
        mAdapter.refreshData(filterFolders,currMedia);

    }

    @Override
    public void VideoFileDataChange(List<ProVideo> video) {
        this.mAudios = video;
        mAdapter.refreshData(video,mVideoPresent.getCurrProVideo());
    }

    @Override
    public void onIndexChanged(int index, char c) {
        mTvCenterChar.setVisibility(View.VISIBLE);
        mTvCenterChar.setText("" + c);
        int position = mAdapter.getPositionForSection(c);
        listView.setSelection(position);

    }

    @Override
    public void onStopChanged(int index, char c) {
        mTvCenterChar.setVisibility(View.GONE);
    }

    @Override
    public void onClickChar(int index, char c) {
        mTvCenterChar.setVisibility(View.VISIBLE);
        mTvCenterChar.setText("" + c);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvCenterChar.setVisibility(View.INVISIBLE);
            }
        }, 800);
        int position = mAdapter.getPositionForSection(c);
        listView.setSelection(position);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_TOUCH_SCROLL:
                mmIsTouchScrolling = true;
                Log.i(TAG, "LvOnScroll -SCROLL_STATE_TOUCH_SCROLL-");
                break;
            case SCROLL_STATE_IDLE:
                mmIsTouchScrolling = false;
                Log.i(TAG, "LvOnScroll -SCROLL_STATE_IDLE-");
                break;
            case SCROLL_STATE_FLING:
                Log.i(TAG, "LvOnScroll -SCROLL_STATE_FLING-");
                break;

        }
    }

    @Override
    public void onScroll(AbsListView absListView, int posit, int i1, int i2) {
    	int position = mAdapter.getSectionForPosition(posit);	
        	Character firstVisibleChar = (char)position;
        		mLetterSidebar.setIndex(firstVisibleChar);
        		int indexChar = mLetterSidebar.findCharIndex(firstVisibleChar);
        		if(mScrollview !=null){
        			mScrollview.scrollTo((int)mLetterSidebar.getDownX(),(int)mLetterSidebar.getDownY(indexChar));
        		}
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(mAdapter.getPage() == 0){
            FilterFolder filterFolder = (FilterFolder) (mListFilters.get(position));
            mUsbVideoFolderMode.loadMedias(filterFolder.mediaFolder.getPath());
            mAdapter.setPage(1);
            mPage = 1;
            LogUtil.i(TAG,"forder");
        }else {
            LogUtil.i(TAG,"file");
            VideoPresent.getInstance().execPlay(position);
            //跳转到播放页面
            backPlayerPager();
        }
    }

    /**
     * 返回到player页面
     */
    public void backPlayerPager() {
        LogUtil.i(TAG, "backPlayerPager");
        mVideofragment = (VideoFoldersFragment) getParentFragment();
        mVideofragment.onBackPage();
    }

    @Override
    public void onTouchScrollXY(float downx, float downY) {
        if(mLetterSidebar !=null){
            mLetterSidebar.setChar(downY);
        }
    }
}
