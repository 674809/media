package com.egar.usbvideo.fragment.folder_fragment;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.view.IndexTitleScrollView;
import com.egar.usbmusic.view.MyScrollView;
import com.egar.usbvideo.adapter.UsbVideoFileAdapter;
import com.egar.usbvideo.fragment.VideoFoldersFragment;
import com.egar.usbvideo.model.UsbVideoFilesModel;
import com.egar.usbvideo.present.VideoPresent;

import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.ProVideo;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/28 10:26
 * @see {@link }
 */
public class VideoFilesFragment extends BaseLazyLoadFragment implements UsbVideoFilesModel.IVideoFilesDataListener,
        ListView.OnItemClickListener, IndexTitleScrollView.OnIndexListener, MyScrollView.IOnScrollXY {

    private String TAG = "VideoFilesFragment";
    private int mPage = 0;
    private ListView mLvVideo;
    private UsbVideoFileAdapter mUsbVideoFileAdapter;
    private VideoFoldersFragment mVideofragment;
    private TextView mTvCenterChar;

    private VideoPresent mMvideoPresent;
    private UsbVideoFilesModel mUsbVideoFilesModel;
    private IndexTitleScrollView mLetterSidebar;
    private boolean mIsClicking = false;
    private MyScrollView mScrollview;
    private List<ProVideo> mListMedias = new ArrayList<>();
    private Handler mmHandler = new Handler();
    private Runnable mmDelayResetClickingFlagRunnable = new Runnable() {

        @Override
        public void run() {
            mIsClicking = false;
        }
    };

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
        mMvideoPresent = VideoPresent.getInstance();
        mLetterSidebar = findViewById(R.id.video_index);
        mLvVideo = findViewById(R.id.video_lv);
        mTvCenterChar = findViewById(R.id.tv_center_char);
        mUsbVideoFileAdapter = new UsbVideoFileAdapter(getMainActivity());
        mLvVideo.setAdapter(mUsbVideoFileAdapter);
        mUsbVideoFilesModel = new UsbVideoFilesModel();
        mUsbVideoFilesModel.setVideoFilsDataListener(this);
        mUsbVideoFilesModel.LoadData();
        mLvVideo.setOnItemClickListener(this);
        mLvVideo.setOnScrollListener(new LvOnScroll());
        mLetterSidebar.registIndexChanged(this);
        mScrollview = findViewById(R.id.scroll);
        mScrollview.setOnScrollXYLinstener(this);
        refreshItem();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.usb_video_files_fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        refreshItem();
    }

    /**
     * 刷新item条目高亮
     */
    private void refreshItem() {
        ProVideo video = VideoPresent.getInstance().getCurrProVideo();
        if (video != null && mUsbVideoFileAdapter != null) {
            mUsbVideoFileAdapter.refreshData(video.getMediaUrl());
        }
    }

    @Override
    public void onPageResume() {
        Log.i(TAG, "onPageResume");
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
    public void VideoFilesDataChange(List<ProVideo> video) {
        mListMedias = video;
        LogUtil.i(TAG, "mListMedias size =" + mListMedias.size());
        mUsbVideoFileAdapter.refreshData(video, mMvideoPresent.getLastTargetMediaPath());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        execItemClick(position);
        backPlayerPager();
    }

    /**
     * 返回到player页面
     */
    public void backPlayerPager() {
        LogUtil.i(TAG, "backPlayerPager");
        mVideofragment = (VideoFoldersFragment) getParentFragment();
        mVideofragment.onBackPage();
    }

    private void execItemClick(int position) {
        if (mListMedias.size() < 0) {
            return;
        }

        if (mIsClicking) {
            mIsClicking = false;
            LogUtil.d(TAG, "##### ---Forbidden click because of frequency !!!--- #####");
            return;
        } else {
            mIsClicking = true;
            mmHandler.removeCallbacksAndMessages(null);
            mmHandler.postDelayed(mmDelayResetClickingFlagRunnable, 1000);
        }

        //
        // ProVideo item = (ProVideo) objItem;

        VideoPresent.getInstance().execPlay(position);

        LogUtil.d(TAG, "LvItemClick -> onItemClick ----Just Play----");

    }

    private void destroy() {
        mmHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onIndexChanged(int index, char c) {
        mTvCenterChar.setVisibility(View.VISIBLE);
        mTvCenterChar.setText("" + c);
        int position = mUsbVideoFileAdapter.getPositionForSection(c);
        mLvVideo.setSelection(position);
    }

    @Override
    public void onStopChanged(int index, char c) {
        mTvCenterChar.setVisibility(View.GONE);
    }

    @Override
    public void onClickChar(int index, char c) {
        mTvCenterChar.setVisibility(View.VISIBLE);
        mTvCenterChar.setText("" + c);
        mmHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvCenterChar.setVisibility(View.INVISIBLE);
            }
        }, 800);
        int position = mUsbVideoFileAdapter.getPositionForSection(c);
        mLvVideo.setSelection(position);
    }

    @Override
    public void onTouchScrollXY(float downx, float downY) {
        if(mLetterSidebar !=null){
            mLetterSidebar.setChar(downY);
        }
    }

    /**
     * ListView scroll event.
     */
    private class LvOnScroll implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case SCROLL_STATE_TOUCH_SCROLL:
                    mUsbVideoFileAdapter.setScrollState(true);
                    //  Log.i(TAG, "LvOnScroll -SCROLL_STATE_TOUCH_SCROLL-");
                    break;
                case SCROLL_STATE_IDLE:
                    mUsbVideoFileAdapter.setScrollState(false);
                    //   Log.i(TAG, "LvOnScroll -SCROLL_STATE_IDLE-");
                    break;
                case SCROLL_STATE_FLING:
                    mUsbVideoFileAdapter.setScrollState(true);
                    //  Log.i(TAG, "LvOnScroll -SCROLL_STATE_FLING-");
                    break;

            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        		int position = mUsbVideoFileAdapter.getSectionForPosition(firstVisibleItem);	
        		Character firstVisibleChar = (char)position;
        		mLetterSidebar.setIndex(firstVisibleChar);
        		int indexChar = mLetterSidebar.findCharIndex(firstVisibleChar);
        		if(mScrollview !=null){
        			mScrollview.scrollTo((int)mLetterSidebar.getDownX(),(int)mLetterSidebar.getDownY(indexChar));
        		}	
        }
    }
}
