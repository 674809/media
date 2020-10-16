package com.egar.usbmusic.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.SearchActivity;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.fragment.folder_fragment.AlbumsFragment;
import com.egar.usbmusic.fragment.folder_fragment.AllSongsFragment;
import com.egar.usbmusic.fragment.folder_fragment.ArtistFragment;
import com.egar.usbmusic.fragment.folder_fragment.FavoritesFragment;
import com.egar.usbmusic.fragment.folder_fragment.FolderFragment;
import com.egar.usbmusic.present.MusicPresent;
import com.egar.usbmusic.utils.FragUtil;

import juns.lib.media.bean.ProAudio;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/12/11 10:32
 * @see {@link }
 */
public class MusicFoldersFragment extends Fragment implements View.OnClickListener {

    private String TAG = "MusicFoldersFragment";
    private MainActivity mMainActivity;
    private View mView;
    private RelativeLayout mImgBack,mImgSearch;
    private FragmentManager mFragmenManager;
    private MusicPlayerFragment fragment;
    private TextView mTvTitle;
    private BaseLazyLoadFragment mFragToLoad;
    private RadioButton mBt_favorites,mBt_folder,mBt_songs,mBt_art,mBt_album;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
        LogUtil.i(TAG, "onAttach");
    }

    public void onWindowChangeFull() {
        LogUtil.i(TAG, "onWindowChangeFull");

    }


    public void onWindowChangeHalf() {
        LogUtil.i(TAG, "onWindowChangeHalf");

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
    }

    public void onPageLoadStart() {
        LogUtil.i(TAG, "onPageLoadStart");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.usb_music_frag_folder, container, false);
        findView();
        return mView;

    }

    private void findView() {
        mImgBack = (RelativeLayout) mView.findViewById(R.id.img_back);
        mImgBack.setOnClickListener(this);

        mTvTitle = (TextView) mView.findViewById(R.id.tv_title);
        mBt_favorites= (RadioButton) mView.findViewById(R.id.bt_favorites);
        mBt_favorites .setOnClickListener(this);
        mBt_folder = (RadioButton) mView.findViewById(R.id.bt_folder);
        mBt_folder.setOnClickListener(this);
        mBt_songs = (RadioButton) mView.findViewById(R.id.bt_songs);
        mBt_songs .setOnClickListener(this);
        mBt_art = (RadioButton) mView.findViewById(R.id.bt_art);
        mBt_art.setOnClickListener(this);
        mBt_album = (RadioButton) mView.findViewById(R.id.bt_album);
        mBt_album .setOnClickListener(this);
        mImgSearch = (RelativeLayout) mView.findViewById(R.id.img_search);
        mImgSearch.setOnClickListener(this);
        initFavorites();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackKey();
                mBt_favorites.setChecked(true);
                break;
            case R.id.bt_favorites:
                initFavorites();
                break;
            case R.id.bt_folder:
                initFoler();
                break;
            case R.id.bt_songs:
                initAllSongs();
                break;
            case R.id.bt_art:
                initArtist();
                break;
            case R.id.bt_album:
                initAlbums();
                break;
            case R.id.img_search:
                startSearch();
                break;
        }
    }

    public void startSearch(){
        Log.i(TAG,"search");
        Intent intent = new Intent();
        intent.setClass(mMainActivity, SearchActivity.class);
        intent.putExtra("TYPE","MUSIC");
        startActivity(intent);
    }

    /**
     * 返回到player页面
     */
    public void onBackPage() {
        mFragmenManager = getFragmentManager();
        if(mFragmenManager !=null){
            Log.i(TAG,"mFragmenManager ="+mFragmenManager);
            mFragmenManager.popBackStack();
        }
        if(mFragmenManager !=null){
            fragment = (MusicPlayerFragment) mFragmenManager.findFragmentByTag("musicPlayer");
        }
        if(fragment !=null){
            fragment.mLightModeController.resetLightMode();
        }

    }


    /**
     * 返回键
     */
    public void onBackKey() {
        int pageLayer = mFragToLoad.getPageIdx();
        LogUtil.i(TAG, "pageLayer =" + pageLayer);
        if (mFragToLoad instanceof FolderFragment) {
            if (pageLayer == 1) { //如果是列表页面，返回时，重新创建第一文件夹页面
                initFoler();
            } else {
                onBackPage();
            }
        } else if (mFragToLoad instanceof ArtistFragment) {
            if (pageLayer == 1) { //如果是列表页面，返回时，重新创建第一文件夹页面
                initArtist();
            } else {
                onBackPage();
            }
        } else if (mFragToLoad instanceof AlbumsFragment) {
            if (pageLayer == 1) { //如果是列表页面，返回时，重新创建第一文件夹页面
                initAlbums();
            } else {
                onBackPage();
            }
        } else {
            onBackPage();
        }
    }

    /**
     * 初始化收藏歌曲页面
     */
    private void initFavorites() {
        LogUtil.w("intFavorites　");
        if (mFragToLoad != null) {
            FragUtil.removeV4Fragment(mFragToLoad, getChildFragmentManager());
            mFragToLoad = null;
        }
        mFragToLoad = new FavoritesFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_folder, mFragToLoad, getChildFragmentManager());
        mTvTitle.setText(R.string.favorite);
    }

    /**
     * 初始化文件列表页面
     */
    private void initFoler() {
        if(!isAdded()){
            return;
        }
        if (mFragToLoad != null) {
            FragUtil.removeV4Fragment(mFragToLoad, getChildFragmentManager());
            mFragToLoad = null;
        }
        mFragToLoad = new FolderFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_folder, mFragToLoad, getChildFragmentManager());
        mTvTitle.setText(R.string.folder);
    }

    /**
     * 初始化所有歌曲页面
     */
    private void initAllSongs() {
        if (mFragToLoad != null) {
            FragUtil.removeV4Fragment(mFragToLoad, getChildFragmentManager());
            mFragToLoad = null;
        }
        mFragToLoad = new AllSongsFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_folder, mFragToLoad, getChildFragmentManager());
        mTvTitle.setText(R.string.all_song);
    }

    /**
     * 初始化专业页面
     */
    private void initArtist() {
        if(!isAdded()){
            return;
        }
        if (mFragToLoad != null) {
            FragUtil.removeV4Fragment(mFragToLoad, getChildFragmentManager());
            mFragToLoad = null;
        }
        mFragToLoad = new ArtistFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_folder, mFragToLoad, getChildFragmentManager());
        mTvTitle.setText(R.string.artists);
    }

    /**
     * 初始化歌曲集
     */
    private void initAlbums() {
        if (mFragToLoad != null) {
            FragUtil.removeV4Fragment(mFragToLoad, getChildFragmentManager());
            mFragToLoad = null;
        }
        mFragToLoad = new AlbumsFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_folder, mFragToLoad, getChildFragmentManager());
        mTvTitle.setText(R.string.albums);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.i(TAG, "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.i(TAG, "onDestroyView");

    }

}
