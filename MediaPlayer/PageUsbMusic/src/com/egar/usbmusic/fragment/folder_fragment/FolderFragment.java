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
import com.egar.usbmusic.adapter.UsbFoldersAdapter;
import com.egar.usbmusic.fragment.MusicFoldersFragment;
import com.egar.usbmusic.interfaces.CollectListener;
import com.egar.usbmusic.model.FoldersModel;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.view.IndexTitleScrollView;
import com.egar.usbmusic.view.MyScrollView;

import java.util.List;

import juns.lib.media.bean.FilterFolder;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/15 20:21
 * @see {@link }
 * folder 二级页面
 */
public class FolderFragment extends BaseLazyLoadFragment implements FoldersModel.IFolderDataChange,
        CollectListener, IndexTitleScrollView.OnIndexListener, ListView.OnScrollListener,  MyScrollView.IOnScrollXY {
    private String TAG = "FolderFragment";
    private ListView mListFolder;
    private UsbFoldersAdapter mAdapter;
    private MusicPresent mMusicPresent;
    private int mPage = 0;//一级界面
    private List<ProAudio> mAudios;
    private List<FilterFolder> mFilterFolders;
    private IndexTitleScrollView mLetterSidebar;
    private TextView mTvCenterChar;
    private boolean mmIsTouchScrolling;
    private static Handler mHandler = new Handler();
    private MusicFoldersFragment fragment;
    private MyScrollView mScrollview;

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
        LogUtil.w("init floder");
        mListFolder = findViewById(R.id.lv_folders);
        mAdapter = new UsbFoldersAdapter(getMainActivity());
        mLetterSidebar = (IndexTitleScrollView) findViewById(R.id.lsb);

        mTvCenterChar = findViewById(R.id.tv_center_char);
        mListFolder.setAdapter(mAdapter);
        mMusicPresent = MusicPresent.getInstance();
        mAdapter.setCollectListener(this);
        FoldersModel.getInstatnce().loadFilters();
        FoldersModel.getInstatnce().setFolderDataChangeListener(this);
        mLetterSidebar.registIndexChanged(this);
        mListFolder.setOnScrollListener(this);

        mListFolder.setOnItemClickListener(new ListOnItemClick());
        mScrollview = findViewById(R.id.scroll);
        mScrollview.setOnScrollXYLinstener(this);
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
        int section = mAdapter.getSectionForPosition(firstVisibleItem);
        Character firstVisibleChar = (char) section;
        mLetterSidebar.setIndex(firstVisibleChar);
     	int getindex = mLetterSidebar.findCharIndex(firstVisibleChar);
        if(mScrollview !=null){
        	mScrollview.scrollTo((int)mLetterSidebar.getDownX(),(int)mLetterSidebar.getDownY(getindex));
        }
    }

    @Override
    public void onTouchScrollXY(float downx, float downY) {
        if(mLetterSidebar !=null){
            mLetterSidebar.setChar(downY);
        }
    }


    public class ListOnItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LogUtil.i(TAG, "onItemClick");
            if (mAdapter.getPage() == 0) {
                FilterFolder filterFolder = (FilterFolder) (mFilterFolders.get(position));
                FoldersModel.getInstatnce().loadMedias(filterFolder.mediaFolder.getPath());
                mAdapter.setPage(1);
                mPage = 1;
                LogUtil.i(TAG, "forder");
            } else {
                LogUtil.i(TAG, "file");
                ProAudio media = mAudios.get(position);
                mMusicPresent.playMusic(media.getMediaUrl(), position);
                //跳转到播放页面
                backPlayerPager();
            }
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.usb_music_folder_fragment;
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
    public void FolderDateChage(List<FilterFolder> filterFolders) {
        this.mFilterFolders = filterFolders;
        mAdapter.refreshData(filterFolders, mMusicPresent.getCurrMedia());
    }

    @Override
    public void FileDataChange(List<ProAudio> audios) {
        this.mAudios = audios;
        LogUtil.i(TAG, "refreshData file");
        mAdapter.refreshData(audios, mMusicPresent.getCurrMedia());
    }

    /**
     * 跳到播放页面
     */
    public void backPlayerPager() {
        LogUtil.i(TAG, "backPlayerPager");
        fragment = (MusicFoldersFragment) getParentFragment();
        fragment.onBackPage();
    }

    @Override
    public void onClickCollectBtn(ImageView ivCollect, int pos) {
        int item = mAdapter.getPage();
        if (item != 0) {
            ProAudio media = mAudios.get(pos);
            switch (media.getCollected()) {
                case MediaCollectState.UN_COLLECTED:
                    media.setCollected(MediaCollectState.COLLECTED);
                    media.setUpdateTime(System.currentTimeMillis());
                    mMusicPresent.updateMediaCollect(pos, media);
                    ivCollect.setImageResource(R.drawable.favor_c);
                    //Clear history collect

                  //  mMusicPresent.clearHistoryCollect();如果添加此方法，会清空收藏列表
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

    @Override
    public void onIndexChanged(int index, char c) {
        mTvCenterChar.setText("" + c);
        mTvCenterChar.setVisibility(View.VISIBLE);
        int position = mAdapter.getPositionForSection(c);
        mListFolder.setSelection(position);
    }

    @Override
    public void onStopChanged(int index, char c) {
        mTvCenterChar.setVisibility(View.GONE);
    }

    @Override
    public void onClickChar(int index, char c) {
        mTvCenterChar.setText("" + c);
        mTvCenterChar.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvCenterChar.setVisibility(View.GONE);
            }
        }, 800);
        int position = mAdapter.getPositionForSection(c);
        mListFolder.setSelection(position);
    }
}
