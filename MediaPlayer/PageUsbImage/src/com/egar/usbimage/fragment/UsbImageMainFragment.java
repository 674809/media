package com.egar.usbimage.fragment;

import android.content.Context;
import android.egar.MediaStatus;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.fragment.BaseUsbFragment;
import com.egar.mediaui.lib.BaseAppV4Fragment;
import com.egar.mediaui.lib.VPFragStateAdapter;
import com.egar.mediaui.util.LogUtil;
import com.egar.usbimage.engine.PlayMode;
import com.egar.usbimage.engine.PlayState;
import com.egar.usbimage.engine.UsbImagePlayService;
import com.egar.usbimage.utils.UsbImageSpUtils;
import com.egar.usbimage.view.AutoDismissLinearLayout;
import com.egar.usbimage.view.AutoDismissRelativeLayout;
import com.egar.usbimage.view.DefaultTxtImageView;
import com.egar.usbimage.view.ImgSwitcherPager;

import java.util.ArrayList;
import java.util.List;

import juns.lib.android.utils.FragUtil;
import juns.lib.android.utils.Logs;
import juns.lib.media.bean.ProImage;
import juns.lib.media.flags.MediaScanState;

/**
 * PAGE - Usb Image
 */
public class UsbImageMainFragment extends UsbImageMainFragmentBase
        implements UsbImagePlayService.UsbImagePlayCallback {
    // TAG
    private static final String TAG = "UsbImageMainFrag";

    //==========Widgets in this Fragment==========
    // Top bar
    private AutoDismissLinearLayout mLayoutMediaInfoBar;
    private TextView mTvTitle, mTvPos;

    // Bottom bar
    private AutoDismissRelativeLayout mLayoutOpBar;
    private DefaultTxtImageView mIvFolder, mIvPrev, mIvPlayOrPause, mIvNext, mIvPlayMode;
    // Image switcher
    private ImgSwitcherPager mIvSwitcher;

    // Page cover to play image.
    private RelativeLayout mLayoutCoverPage;

    //==========Variables in this Fragment==========
    // View pager fragment list set.
    private List<BaseAppV4Fragment> mListFrags;

    // Folder page.
    private UsbImageFolderFragment mFragFolder;

    /**
     * Touch 事件 - ACTION_DOWN - getX();
     */
    private float mTouchDownX;

    private MainActivity mMainActivity;
    private UsbImageContentFragment mUsbImageContentFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        UsbImageSpUtils.init(context);
        UsbImagePlayService.instance().init(context);
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public int getPageIdx() {
        return Configs.PAGE_IDX_USB_IMAGE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.usb_image_frag_main;
    }

    @Override
    public void initView() {
        super.initView();

        //
        mLayoutMediaInfoBar = findViewById(R.id.layout_media_info);
        mTvPos = findViewById(R.id.tv_pos_of_list);
        mTvPos.setText("");
        mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText("");

        //
        mLayoutOpBar = findViewById(R.id.layout_op_bar);
        mIvFolder = findViewById(R.id.iv_folder);
        mIvFolder.setOnClickListener(mViewOnClick);

        mIvPrev = findViewById(R.id.iv_prev);
        mIvPrev.setOnClickListener(mViewOnClick);

        mIvPlayOrPause = findViewById(R.id.iv_play_or_pause);
        mIvPlayOrPause.setOnClickListener(mViewOnClick);

        mIvNext = findViewById(R.id.iv_next);
        mIvNext.setOnClickListener(mViewOnClick);

        mIvPlayMode = findViewById(R.id.iv_play_mode_set);
        mIvPlayMode.setOnClickListener(mViewOnClick);

        //
        mIvSwitcher = findViewById(R.id.view_pager);
        mLayoutCoverPage = findViewById(R.id.layout_cover_page);

        mLayoutOpBar.setRelativeLayoutAutoDimissListener(new AutoDismissRelativeLayout.RelativeLayoutAutoDimissListener() {
            @Override
            public void onRelativeLayoutDimiss(boolean isDimiss) {
                if (isDimiss){
                    mMainActivity.hideNatvigtion();
                    ((BaseUsbFragment)getParentFragment()).setIndicatorVisib(false);
                }else {
                    mMainActivity.showNativigtion();
                    ((BaseUsbFragment)getParentFragment()).setIndicatorVisib(true);
                }
            }
        });

        mLayoutMediaInfoBar.setLinearLayoutAutoDimissListener(new AutoDismissLinearLayout.LinearLayoutAutoDimissListener() {
            @Override
            public void onLinearLayoutDimiss(boolean isDimiss) {
            }
        });



        // BIND Service
        bindScanService(true);
    }

    @Override
    public void onResume() {
        super.onResume();
//        UsbImagePlayService.instance().resumeByUser();
        mIvSwitcher.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected: "+position);
                if (UsbImagePlayService.instance().getTotalCount() != 0){
                    UsbImageSpUtils.getLastPos(true, position);
                    //UsbImagePlayService.instance().notifyPlay();
                    UsbImagePlayService.instance().setPos(position);

                    int totalCount = UsbImagePlayService.instance().getTotalCount();
                    mTvPos.setText(String.format("%s/%s",
                            String.valueOf(position+1),
                            String.valueOf(totalCount)));
                    // Set title
                    ProImage currMedia = UsbImagePlayService.instance().getCurrMedia();
                    mTvTitle.setText(currMedia.getFileName());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        UsbImagePlayService.instance().pauseByUser(false);
    }

    @Override
    public void onWindowChangeFull() {
        super.onWindowChangeFull();
        LogUtil.i("onWindowChangeFull");
    }

    @Override
    public void onWindowChangeHalf() {
        super.onWindowChangeHalf();
        LogUtil.i("onWindowChangeHalf");
    }

    @Override
    public void onMediaScanServiceConnected() {
        super.onMediaScanServiceConnected();
        Log.i(TAG, "onMediaScanServiceConnected()");
        loadData(null);
    }

    public void onRespScanState(int scanState) {
        super.onRespScanState(scanState);
        Log.i(TAG, "onRespScanState(" + MediaScanState.desc(scanState) + ")");
        if (scanState == MediaScanState.SCAN_IMAGE_END) {
            loadData(null);
        }
    }

    private void loadData(List<ProImage> targetData) {
        // load play service.
        List<ProImage> listData = (targetData == null) ? getAllImages() : targetData;
        UsbImagePlayService.instance().loadData(listData);
        UsbImagePlayService.instance().addPlayCallback(this);

        Log.i(TAG, "loadData "+(listData != null));
        // load fragments.
        if (listData != null && listData.size() != 0) {
            Log.i(TAG, "loadData "+listData.size());
            mListFrags = new ArrayList<>();
            for (ProImage media : listData) {
                mUsbImageContentFragment = new UsbImageContentFragment();
                mUsbImageContentFragment.setMedia(media);
                mListFrags.add(mUsbImageContentFragment);
            }
            VPFragStateAdapter adapter = new VPFragStateAdapter(getChildFragmentManager());
            //noinspection unchecked
            adapter.setListFrags(mListFrags);
            mIvSwitcher.setAdapter(adapter);

            // Automatically start.
            showCoverInfo(true);
            UsbImagePlayService.instance().setPlayMode(PlayMode.NONE);
            UsbImagePlayService.instance().autoPlay();
        }
    }

    private View.OnClickListener mViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mIvFolder) {
                showFolder(true);
            } else if (v == mIvPrev) {
                playPrevByUser();
            } else if (v == mIvPlayOrPause) {
                playOrPauseByUser();
            } else if (v == mIvNext) {
                playNextByUser();
            } else if (v == mIvPlayMode) {
                switchPlayMode();
            }
        }

        /**
         * Play prev media by user.
         */
        private void playPrevByUser() {
            Log.i(TAG, "playPrevByUser()");
            // Play media.
            UsbImagePlayService.instance().playPrevByUser();
            // Reset cover dismiss time.
            showCoverInfo(true);
        }

        /**
         * Play or pause by user.
         */
        private void playOrPauseByUser() {
            // Set pager.
            UsbImagePlayService.instance().playOrPauseByUser();
            // Reset cover dismiss time.
            showCoverInfo(true);
        }

        /**
         * Play next media by user.
         */
        private void playNextByUser() {
            Log.i(TAG, "playNextByUser()");
            // Set pager.
            UsbImagePlayService.instance().playNextByUser();
            // Reset cover dismiss time.
            showCoverInfo(true);
        }

        /**
         * Switch play mode.
         */
        private void switchPlayMode() {
            Log.i(TAG, "playPrevByUser()");
            // Play media.
            UsbImagePlayService.instance().switchPlayMode();
            // Reset cover dismiss time.
            showCoverInfo(true);
        }
    };

    /**
     * Show or hide folder page.
     */
    public void showFolder(boolean isShow) {
        Log.i(TAG,"showFolder( "+isShow+" )");
        if (isShow) {
            if (mFragFolder == null
                    && mLayoutCoverPage.getVisibility() == View.GONE) {
                mLayoutCoverPage.setVisibility(View.VISIBLE);
                FragUtil.loadV4ChildFragment(R.id.layout_cover_page,
                        (mFragFolder = new UsbImageFolderFragment()),
                        getChildFragmentManager());
                UsbImagePlayService.instance().pauseByUser(false);
            }
        } else {
            if (mFragFolder != null) {
                mLayoutCoverPage.setVisibility(View.GONE);
                FragUtil.removeV4Fragment(mFragFolder, getChildFragmentManager());
                mFragFolder = null;
                UsbImagePlayService.instance().resumeByUser();
            }
        }
    }

    /**
     * Update data.
     *
     * @param targetData    Data cache will be changed to "targetData".
     * @param startPosition Will play from this position.
     * @param startMediaUrl Will play from this mediaUrl.
     */
    public void updateData(List<ProImage> targetData, int startPosition, String startMediaUrl) {
        UsbImagePlayService.instance().pauseByUser(false);
        UsbImageSpUtils.getLastPos(true, startPosition);
        UsbImageSpUtils.getLastMediaUrl(true, startMediaUrl);
        loadData(targetData);
        showFolder(false);
    }

    @Override
    public void onPlayStateChanged(int state) {
        try {
            Logs.i(TAG, "onPlayStateChanged(" + PlayState.desc(state) + ")");
            switch (state) {
                case PlayState.PAUSE:
                    mIvPlayOrPause.setBackground(getResources().getDrawable(R.drawable.pause_bottom_ico));
                    break;
                case PlayState.PLAY:
                default:
                    // Set state.
                    mIvPlayOrPause.setBackground(getResources().getDrawable(R.drawable.play_bottom_ico));
                    // Set position
                    int currPos = UsbImagePlayService.instance().getCurrPos();
                    /*int totalCount = UsbImagePlayService.instance().getTotalCount();
                    mTvPos.setText(String.format("%s/%s",
                            String.valueOf(currPos+1),
                            String.valueOf(totalCount)));
                    // Set title
                    ProImage currMedia = UsbImagePlayService.instance().getCurrMedia();
                    mTvTitle.setText(currMedia.getFileName());*/
                    // Switch page.
                    mIvSwitcher.setCurrentItem(currPos,false);
                    break;
            }

            mMainActivity.getMainPresent().setMediaStatusInfo(MediaStatus.MEDIA_TYPE_IMAGE,state,UsbImagePlayService.instance().getCurrMedia().getFileName());
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onPlayModeChanged(int mode) {
        Logs.i(TAG, "onPlayModeChanged(" + PlayMode.desc(mode) + ")");
        mIvPlayMode.setText(PlayMode.desc(mode));
        if (mode == PlayMode.LOOP){
            mIvPlayMode.setImageDrawable(getContext().getDrawable(R.drawable.image_loop));
        }else {
            mIvPlayMode.setImageDrawable(getContext().getDrawable(R.drawable.image_random));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "-MotionEvent.ACTION_DOWN-");
                // Pause media play.
                UsbImagePlayService.instance().pauseByUser(false);
                mTouchDownX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "-MotionEvent.ACTION_MOVE-");
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "-MotionEvent.ACTION_UP-");
                //Resume media play.
                UsbImagePlayService.instance().resumeByUser();
                break;
        }

        // 允许ViewPager的Scroll事件执行的范围
        if (mTouchDownX > 100 && mTouchDownX < mLayoutMediaInfoBar.getWidth() - 100) {
            mIvSwitcher.setScroll(true);
            return false; // 在此范围内不允许父类事件执行
            // 距离左右边距 100px 范围内不允许触发ViewPager的Scroll事件
        } else {
            mIvSwitcher.setScroll(false);
        }
        // 上一级ViewPager滑动
        return super.onTouchEvent(event);
    }

    /**
     * Switch visible state of top bar and bottom bar.
     */
    public void switchCoverInfo() {
        if (mLayoutMediaInfoBar.getVisibility() == View.VISIBLE
                || mLayoutOpBar.getVisibility() == View.VISIBLE) {
            showCoverInfo(false);
        } else if (mLayoutMediaInfoBar.getVisibility() == View.GONE
                || mLayoutOpBar.getVisibility() == View.GONE) {
            showCoverInfo(true);
        }
    }

    /**
     * Set visible state of top bar and bottom bar.
     */
    private void showCoverInfo(boolean isShow) {
        if (isShow) {
            mLayoutMediaInfoBar.autoShow();
            mLayoutOpBar.autoShow();
            mMainActivity.showNativigtion();
        } else {
            mLayoutMediaInfoBar.autoDismiss();
            mLayoutOpBar.autoDismiss();
            mMainActivity.hideNatvigtion();
        }
    }

    @Override
    public void onDestroy() {
        // Destroy play service.
        //UsbImagePlayService.instance().destroy();
        // UNBIND Service
        //bindScanService(false);
        // Clear auto dismiss task.
//        mLayoutMediaInfoBar.clear();
//        mLayoutOpBar.clear();
        super.onDestroy();
    }

    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();

        if (mUsbImageContentFragment != null){
            mUsbImageContentFragment.onDestroyView();
        }

        UsbImagePlayService.instance().destroy();
        bindScanService(false);
        mLayoutMediaInfoBar.clear();
        mLayoutOpBar.clear();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPageLoadStop();
    }
}