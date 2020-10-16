package com.egar.usbvideo.fragment;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.SearchActivity;
import com.egar.mediaui.fragment.BaseLazyLoadFragment;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.utils.FragUtil;
import com.egar.usbvideo.fragment.folder_fragment.VideoFilesFragment;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/12/12 15:04
 * @see {@link }
 */
public class VideoFoldersFragment extends Fragment implements View.OnClickListener {

    private String TAG = "VideoFolderFragment";
    private View mView;
    private RelativeLayout mImgBack,mSearchVideo;
    private FragmentManager mFragmenManager;
    private BaseLazyLoadFragment mFragToLoad;
    private Button mtvMediaName, mtvMediaFolder;
    private MainActivity mainActivity;
    public static int VIDEO_CODE = 2;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.usb_video_frag_folder, container, false);
        initView();
        return mView;
    }

    private void initView() {
        mImgBack = findViewById(R.id.img_back);
        mImgBack.setOnClickListener(this);

        mtvMediaName = findViewById(R.id.tvMediaName);
        mtvMediaName.setOnClickListener(this);
        mtvMediaName.setSelected(true);
        mtvMediaFolder = findViewById(R.id.tvMediaFolder);
        mtvMediaFolder.setOnClickListener(this);
        mSearchVideo = findViewById(R.id.search_video);
        mSearchVideo.setOnClickListener(this);
        initFiles();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                onBackPage();
                break;
            case R.id.tvMediaName:
                initFiles();;
                mtvMediaName.setSelected(true);
                mtvMediaFolder.setSelected(false);
                break;
            case R.id.tvMediaFolder:
                initFolder();
                mtvMediaName.setSelected(false);
                mtvMediaFolder.setSelected(true);
                break;
            case R.id.search_video:
                startSearch();
                break;
        }
    }
    public void startSearch(){
        Log.i(TAG,"search");
        Intent intent = new Intent();
        intent.setClass(mainActivity, SearchActivity.class);
        intent.putExtra("TYPE","VIDEO");
        startActivityForResult(intent,VIDEO_CODE);
    }
    private void initFiles() {
        LogUtil.w("intFavorites　");
        if (mFragToLoad != null) {
            FragUtil.removeV4Fragment(mFragToLoad, getChildFragmentManager());
            mFragToLoad = null;
        }
        mFragToLoad = new VideoFilesFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_video_framelayout, mFragToLoad, getChildFragmentManager());
    }

    private void initFolder() {
        LogUtil.w("intFavorites　");
        if (mFragToLoad != null) {
            FragUtil.removeV4Fragment(mFragToLoad, getChildFragmentManager());
            mFragToLoad = null;
        }
        mFragToLoad = new com.egar.usbvideo.fragment.folder_fragment.VideoFolderFragment();
        FragUtil.loadV4ChildFragment(R.id.usb_video_framelayout, mFragToLoad, getChildFragmentManager());
    }

    /**
     * 返回到player页面
     */
    public void onBackPage() {
        mFragmenManager = getFragmentManager();
        ((BaseUsbFragment) getParentFragment()).setIndicatorVisib(true);
        VideoPlayFragment fragments = (VideoPlayFragment) mFragmenManager.findFragmentByTag("videoPlayer");
        Log.i(TAG, "videoPlayFragment  =" + fragments.toString());
        if (fragments != null ) {
           fragments.mLightModeController.resetLightMode();
        }
        mFragmenManager.popBackStack();
    }

    public <T extends View> T findViewById(int id) {
        return (T) mView.findViewById(id);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");

    }


}
