package com.egar.usbmusic.fragment.folder_fragment;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.adapter.UsbFavoriteAdapter;
import com.egar.usbmusic.fragment.MusicFoldersFragment;
import com.egar.usbmusic.fragment.UsbMusicMainFragment;
import com.egar.usbmusic.interfaces.CollectListener;
import com.egar.usbmusic.model.FavoritesModel;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.view.IndexTitleScrollView;
import com.egar.usbmusic.view.MyScrollView;

import java.util.List;

import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/15 19:59
 * @see {@link }
 */
public class FavoritesFragment extends BaseLazyLoadFragment implements AdapterView.OnItemClickListener,
        FavoritesModel.IFavoritesDataChange, IndexTitleScrollView.OnIndexListener,ListView.OnScrollListener, CollectListener,
        MyScrollView.IOnScrollXY {
    private String TAG = "FavoritesFragment";
    private ListView mListView;
    private UsbFavoriteAdapter mAdapter;
    private MusicPresent mMusicPresent;
    private UsbMusicMainFragment mFragment;
    private List<ProAudio> mListData;
    //Task for loading medias.
    private IndexTitleScrollView mLetterSidebar;
    private TextView mTvCenterToast, mTvCcenterChar;
    private FavoritesModel mFavoritesModel;
    private static Handler mHandler = new Handler();
    private MusicFoldersFragment fragment;
    private MyScrollView mScrollview;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int getPageIdx() {
        return 0;
    }

    @Override
    public void onWindowChangeFull() {
        Log.i(TAG,"onWindowChangeFull");

    }

    @Override
    public void onWindowChangeHalf() {
        Log.i(TAG,"onWindowChangeHalf");

    }

    @Override
    public void initView() {
        LogUtil.i("init favorite");
        mFragment = (UsbMusicMainFragment) getMainActivity().getMainPresent().getCurrentUsbFragmen(Configs.PAGE_USB_MUSIC_PLAY);
        mMusicPresent = MusicPresent.getInstance();
        mListView = (ListView) findViewById(R.id.listView);
        mTvCenterToast = findViewById(R.id.tv_center_toast);
        mTvCcenterChar = findViewById(R.id.tv_center_char);
        mLetterSidebar = (IndexTitleScrollView) findViewById(R.id.lsb);
        mLetterSidebar.registIndexChanged(this);


        mAdapter = new UsbFavoriteAdapter(getMainActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

        mFavoritesModel = new FavoritesModel();
        mFavoritesModel.setFavoriteDataChangeListener(this);
        mFavoritesModel.loadFilters();
        mAdapter.setCollectListener(this);

        mScrollview = findViewById(R.id.scroll);
        mScrollview.setOnScrollXYLinstener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.usb_music_favorite_fragment;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.i(TAG, "position=" + position);
       execItemClick(position);
        //跳转到播放页面
        backPlayerPager();
    }

    public void backPlayerPager() {
        LogUtil.i(TAG, "backPlayerPager");
        fragment = (MusicFoldersFragment) getParentFragment();
        fragment.onBackPage();
    }
    private void execItemClick(int position) {
        ProAudio itemMedia = (ProAudio) mAdapter.getItemInfo(position);
        if (itemMedia == null) {
            return;
        }
        mMusicPresent.playMusic(itemMedia.getMediaUrl(), position);

    }

    @Override
    public void FavoritesDateChage(List<ProAudio> list) {
        LogUtil.i(TAG, "list =" + list.size());
        if (list.size() == 0) {
            mTvCenterToast.setVisibility(View.VISIBLE);
        } else {
            mTvCenterToast.setVisibility(View.GONE);
        }
        mListData = list;
        mAdapter.refreshData(mListData, mMusicPresent.getCurrMedia());
    }

    @Override
    public void onIndexChanged(int index, char c) {
        LogUtil.i(TAG,"onIndexChanged");
        if(mListData.size() == 0){
            mTvCcenterChar.setVisibility(View.GONE);
        }else {
            mTvCcenterChar.setVisibility(View.VISIBLE);
        }
        mTvCcenterChar.setText("" + c);
        int position = mAdapter.getPositionForSection(c);
        mListView.setSelection(position);
    }

    @Override
    public void onStopChanged(int index, char c) {
        mTvCcenterChar.setVisibility(View.GONE);
    }

    @Override
    public void onClickChar(int index, char c) {
        LogUtil.i(TAG,"onClickChar ="+c);
        if(mListData.size() == 0){
            mTvCcenterChar.setVisibility(View.GONE);
        }else {
            mTvCcenterChar.setVisibility(View.VISIBLE);
        }
        mTvCcenterChar.setText("" + c);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvCcenterChar.setVisibility(View.INVISIBLE);
            }
        }, 800);
        int position = mAdapter.getPositionForSection(c);
        mListView.setSelection(position);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.i(TAG,"onScroll");
            int section = mAdapter.getSectionForPosition(firstVisibleItem);
            Character firstVisibleChar = (char) section;
            mLetterSidebar.setIndex(firstVisibleChar);
            int getindex = mLetterSidebar.findCharIndex(firstVisibleChar);
        if(mScrollview !=null){
        	mScrollview.scrollTo((int)mLetterSidebar.getDownX(),(int)mLetterSidebar.getDownY(getindex));
        }
    }

    @Override
    public void onClickCollectBtn(ImageView ivCollect, int pos) {
        LogUtil.d(TAG,"onClickCollectBtn "+pos);
        ProAudio item = (ProAudio) mAdapter.getItemInfo(pos);
        if (item == null) {
            return;
        }
        if (item.getCollected() == MediaCollectState.COLLECTED) {
            item.setCollected(MediaCollectState.UN_COLLECTED);
            item.setUpdateTime(System.currentTimeMillis());
            mMusicPresent.updateMediaCollect(pos, item);
            mFavoritesModel.loadFilters();
        }
    }

    @Override
    public void onTouchScrollXY(float downx, float downY) {
        Log.i(TAG,"downx ="+downx +"   downY = "+downY);
        if(mLetterSidebar !=null){
            mLetterSidebar.setChar(downY);
        }
    }
}
