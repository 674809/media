package com.egar.usbmusic.fragment.folder_fragment;

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
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.adapter.UsbAllsongsAdapter;
import com.egar.usbmusic.fragment.MusicFoldersFragment;
import com.egar.usbmusic.interfaces.CollectListener;
import com.egar.usbmusic.model.AllSongsModel;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.view.IndexTitleScrollView;
import com.egar.usbmusic.view.MyScrollView;

import java.util.List;

import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/15 20:21
 * @see {@link }
 * folder 二级页面
 */
public class AllSongsFragment extends BaseLazyLoadFragment implements IndexTitleScrollView.OnIndexListener,
        AllSongsModel.IAllSongsDataChange, AdapterView.OnItemClickListener, ListView.OnScrollListener, CollectListener,  MyScrollView.IOnScrollXY {
    private String TAG = "AllSongsFragment";
    private ListView mLvAllsongs;
    private UsbAllsongsAdapter mAllsongsAdapter;
    private List<ProAudio> mListData;

    private MusicPresent mMusicPresent;
    private int mPage = 0;
    private IndexTitleScrollView mLetterSidebar;
    private TextView mTvCenterChar;
    private AllSongsModel mAllSongsModel;
    private boolean mmIsTouchScrolling;
    private MusicFoldersFragment fragment;
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
            if(mLetterSidebar !=null){
                mLetterSidebar.refresh();
            }
    }

    @Override
    public void onWindowChangeHalf() {

    }


    @Override
    public void initView() {

        LogUtil.i("initview all songs");
        mMusicPresent = MusicPresent.getInstance();
        mLvAllsongs = findViewById(R.id.lv_allsongs);
        mTvCenterChar = findViewById(R.id.tv_center_char);
        mLetterSidebar = (IndexTitleScrollView) findViewById(R.id.lsb);

        mAllsongsAdapter = new UsbAllsongsAdapter(getMainActivity());
        mLvAllsongs.setAdapter(mAllsongsAdapter);
        mLvAllsongs.setOnScrollListener(this);
        mLvAllsongs.setOnItemClickListener(this);
        mAllsongsAdapter.setCollectListener(this);
        mLetterSidebar.registIndexChanged(this);

        mAllSongsModel = new AllSongsModel();
        mAllSongsModel.setAllSongDataChangeListener(this);
        mAllSongsModel.loadFilters();
        mLvAllsongs.setOnScrollListener(this);
        mScrollview = findViewById(R.id.scroll);
        mScrollview.setOnScrollXYLinstener(this);
        refreshItem();
    }

    /**
     * 刷新当前播放条目高亮
     */
    private void refreshItem() {
        ProAudio audio = MusicPresent.getInstance().getCurrMedia();
        if (audio != null && mAllsongsAdapter != null) {
                mAllsongsAdapter.refreshData(audio.getMediaUrl());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.usb_music_all_songs_fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshItem();
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
    public void onIndexChanged(int index, char c) {
        mTvCenterChar.setVisibility(View.VISIBLE);
        mTvCenterChar.setText("" + c);
        int position = mAllsongsAdapter.getPositionForSection(c);
        mLvAllsongs.setSelection(position);

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
        int position = mAllsongsAdapter.getPositionForSection(c);
        mLvAllsongs.setSelection(position);
    }


    @Override
    public void AllSongsDateChage(List<ProAudio> list) {
        LogUtil.i(TAG, "list size=" + list.size());
        if (list != null) {
            mListData = list;
            mAllsongsAdapter.refreshData(list, mMusicPresent.getCurrMedia());
            scrollToPlayingPos(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        execItemClick(position);
        //跳转到播放页面
        backPlayerPager();
    }
    /**
     * 跳到播放页面
     */
    public void backPlayerPager() {
        LogUtil.i(TAG, "backPlayerPager");
        fragment = (MusicFoldersFragment) getParentFragment();
        fragment.onBackPage();
    }
    private void execItemClick(int position) {
        ProAudio itemMedia = (ProAudio) mAllsongsAdapter.getItemInfo(position);
        if (itemMedia == null) {
            return;
        }
        //
        mMusicPresent.playMusic(itemMedia.getMediaUrl(), position);


    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
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
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int section = mAllsongsAdapter.getSectionForPosition(firstVisibleItem);
        Character firstVisibleChar = (char) section;
        mLetterSidebar.setIndex(firstVisibleChar);
        int getindex = mLetterSidebar.findCharIndex(firstVisibleChar);
        if(mScrollview !=null){
        	mScrollview.scrollTo((int)mLetterSidebar.getDownX(),(int)mLetterSidebar.getDownY(getindex));
        }
    }

    @Override
    public void onClickCollectBtn(ImageView ivCollect, int pos) {
        if (mListData.size() != 0) {
            ProAudio media = mListData.get(pos);
            Log.i(TAG,"collect media="+media.toString());
            switch (media.getCollected()) {
                case MediaCollectState.UN_COLLECTED:
                    media.setCollected(MediaCollectState.COLLECTED);
                    media.setUpdateTime(System.currentTimeMillis());
                    mMusicPresent.updateMediaCollect(pos, media);
                    ivCollect.setImageResource(R.drawable.favor_c);
                    //Clear history collect
                    break;
                case MediaCollectState.COLLECTED:
                    media.setCollected(MediaCollectState.UN_COLLECTED);
                    media.setUpdateTime(System.currentTimeMillis());
                    mMusicPresent.updateMediaCollect(pos, media);
                    ivCollect.setImageResource(R.drawable.favor_c_n);
                    break;
            }
        }
    }


    /**
     * 显示播放的歌曲
     *
     * @param isWaitLoading
     */
    public void scrollToPlayingPos(final boolean isWaitLoading) {
        mHandler.removeCallbacksAndMessages(null);
        if (isWaitLoading) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollToPlayingPos(false);
                }
            }, 300);
        } else {
            try {
                //
                int currPos = mMusicPresent.getCurrPos();
                int firstPosOfPage = getPageFirstPos(currPos);
                mLvAllsongs.setSelection(firstPosOfPage);
                //
                ProAudio firstMediaOfCurrPage = mListData.get(firstPosOfPage);
                char c = firstMediaOfCurrPage.getTitlePinYin().charAt(0);
                mLetterSidebar.setIndex(c);
                mAllsongsAdapter.refreshData(firstMediaOfCurrPage.getMediaUrl());
            } catch (Exception e) {
                Log.i(TAG, "refreshHLLetterOfSideBar() >> e:" + e.getMessage());
            }
        }
    }

    @Override
    public void onTouchScrollXY(float downx, float downY) {
        if(mLetterSidebar !=null){
            mLetterSidebar.setChar(downY);
        }
    }
}
