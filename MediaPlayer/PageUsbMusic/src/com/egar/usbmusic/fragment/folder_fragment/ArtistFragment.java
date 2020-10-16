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
import com.egar.usbmusic.adapter.UsbArtistAdapter;
import com.egar.usbmusic.fragment.MusicFoldersFragment;
import com.egar.usbmusic.interfaces.CollectListener;
import com.egar.usbmusic.model.ArtistsMode;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.view.IndexTitleScrollView;
import com.egar.usbmusic.view.MyScrollView;

import java.util.List;

import juns.lib.media.bean.FilterMedia;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/15 20:21
 * @see {@link }
 * folder 二级页面
 */
public class ArtistFragment extends BaseLazyLoadFragment implements ArtistsMode.IArtistDataChange,
        AdapterView.OnItemClickListener, IndexTitleScrollView.OnIndexListener, ListView.OnScrollListener, CollectListener,MyScrollView.IOnScrollXY  {
    private String TAG = "ArtistFragment";
    private int mPage = 0;
    private ListView mLvArtist;
    private IndexTitleScrollView mIndexTitleScrollView;
    private TextView mTvCenterChar;
    private UsbArtistAdapter mUsbArtistAdapter;
    private MusicPresent mMusicPresent;
    private List<ProAudio> mAudios;
    private List<FilterMedia> mFilterMedia;
    private ArtistsMode mArtmodel;
    private boolean mmIsTouchScrolling;
    private MusicFoldersFragment fragment;
    private MyScrollView mScrollview;
    private static Handler mHandler = new Handler();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void initView() {
        mLvArtist = findViewById(R.id.lv_artist);
        mIndexTitleScrollView = findViewById(R.id.lsb);
        mTvCenterChar = findViewById(R.id.tv_center_char);
        mUsbArtistAdapter = new UsbArtistAdapter(getMainActivity());
        mUsbArtistAdapter.setCollectListener(this);
        mLvArtist.setAdapter(mUsbArtistAdapter);
        mMusicPresent = MusicPresent.getInstance();
        mArtmodel = new ArtistsMode();
        mArtmodel.setArtistDataChangeListener(this);
        mArtmodel.loadFilters();
        mLvArtist.setOnItemClickListener(this);
        mLvArtist.setOnScrollListener(this);
        mScrollview = findViewById(R.id.scroll);
        mScrollview.setOnScrollXYLinstener(this);
        mIndexTitleScrollView.registIndexChanged(this);
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
    protected int getLayoutId() {
        return R.layout.usb_music_artist_fragment;
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
    public void ArtistsDateChage(List<FilterMedia> filterFolders) {
        this.mFilterMedia = filterFolders;
        mUsbArtistAdapter.refreshData(filterFolders, mMusicPresent.getCurrMedia());
    }

    @Override
    public void ArtistsFileDataChange(List<ProAudio> audios) {
        this.mAudios = audios;
        mUsbArtistAdapter.refreshData(audios, mMusicPresent.getCurrMedia());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mUsbArtistAdapter.getmPage() == 0) {
            FilterMedia filter = (FilterMedia) (mFilterMedia.get(position));
            mArtmodel.loadMedias(filter.sortStr);
            mUsbArtistAdapter.setmPage(1);
            mPage = 1;
            LogUtil.i(TAG, "forder");
        } else {
            LogUtil.i(TAG, "file");
           ProAudio media = mAudios.get(position);
            mMusicPresent.playMusic(media.getMediaUrl(), position);
            backPlayerPager();
        }
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
    public void onIndexChanged(int index, char c) {
        LogUtil.i(TAG, "onIndexChanged =" + c);
        mTvCenterChar.setVisibility(View.VISIBLE);
        mTvCenterChar.setText("" + c);
        int position = mUsbArtistAdapter.getPositionForSection(c);
        mLvArtist.setSelection(position);
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
        int position = mUsbArtistAdapter.getPositionForSection(c);
        mLvArtist.setSelection(position);
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
        int section = mUsbArtistAdapter.getSectionForPosition(firstVisibleItem);
        Character firstVisibleChar = (char) section;
        mIndexTitleScrollView.setIndex(firstVisibleChar);
         int getindex = mIndexTitleScrollView.findCharIndex(firstVisibleChar);
        if(mScrollview !=null){
        	mScrollview.scrollTo((int)mIndexTitleScrollView.getDownX(),(int)mIndexTitleScrollView.getDownY(getindex));
        }
    }


    @Override
    public void onClickCollectBtn(ImageView ivCollect, int pos) {
        int item = mUsbArtistAdapter.getmPage();
        if (item != 0) {
            ProAudio media = mAudios.get(pos);
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

    @Override
    public void onTouchScrollXY(float downx, float downY) {
        if(mIndexTitleScrollView !=null){
            mIndexTitleScrollView.setChar(downY);
        }
    }
}
