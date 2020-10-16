package com.egar.usbimage.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.R;
import com.egar.mediaui.SearchActivity;
import com.egar.mediaui.lib.BaseAppV4Fragment;
import com.egar.usbimage.adapter.ImageGroupAdapter;
import com.egar.usbimage.engine.UsbImagePlayService;
import com.egar.usbimage.utils.UsbImageSpUtils;
import com.egar.usbimage.view.DefaultTxtImageView;
import com.egar.usbimage.view.LetterSideBar;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import juns.lib.android.utils.Logs;
import juns.lib.media.bean.FilterFolder;
import juns.lib.media.bean.ProImage;
import juns.lib.media.db.tables.AudioTables;
import juns.lib.recyclerview.FastRecyclerView;
import juns.lib.recyclerview.anim.RecyclerViewLayoutAnimHelper;

public class UsbImageFolderFragment extends BaseAppV4Fragment {
    //TAG
    private static final String TAG = "UsbImageFolderFrag";

    //
    private View mContentView;
    private DefaultTxtImageView mIvGrid;
    private TextView mIvBack;

    private FastRecyclerView mRecyclerView;
    private ImageGroupAdapter mImgGrpAdapter;

    //
    private UsbImageMainFragment mFragParent;

    //
    private boolean mShowAsGrid = true;

    // Async data loading task.
    // Task for loading filters.
    private FilterLoadingTask mFilterLoadingTask;
    private List<FilterFolder> mListFilters;
    // Task for loading medias.
    private DataLoadingTask mDataLoadingTask;
    private List<ProImage> mListData;
    private TextView mTvList;
    private TextView mTvGrid;
    private LetterSideBar mLsb;
    private ImageView mIvSearch;
    private MainActivity mMainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragParent = (UsbImageMainFragment) getParentFragment();
        mMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.usb_image_frag_folder, container, false);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        //
        mIvBack = (TextView) mContentView.findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(mViewOnClick);

        mIvGrid = (DefaultTxtImageView) mContentView.findViewById(R.id.iv_switch);
        mIvGrid.setOnClickListener(mViewOnClick);

        //
        mImgGrpAdapter = new ImageGroupAdapter(this);
        mRecyclerView = (FastRecyclerView) mContentView.findViewById(R.id.v_recycler);
        mRecyclerView.setAdapter(mImgGrpAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (recyclerView != null && recyclerView.getChildCount() > 0) {
                    try {
                        int currentPosition = ((RecyclerView.LayoutParams) recyclerView.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
                        Log.i(TAG, "" + currentPosition);
                        char c = mListData.get(currentPosition).getTitlePinYin().charAt(0);
                        mLsb.refreshHlLetter(c);
                    } catch (Exception e) {
                    }
                }

            }
        });

        mIvSearch = (ImageView) mContentView.findViewById(R.id.iv_search);
        mIvSearch.setOnClickListener(mViewOnClick);


        mTvList = (TextView) mContentView.findViewById(R.id.tv_list);
        mTvGrid = (TextView) mContentView.findViewById(R.id.tv_grid);

        mTvList.setOnClickListener(mViewOnClick);
        mTvGrid.setOnClickListener(mViewOnClick);

        mLsb = (LetterSideBar) mContentView.findViewById(R.id.lsb);
        mLsb.refreshLetters(null);
        mLsb.addCallback(new LetterSidBarCallback());
        //
        //loadFilters();
        loadMedias(null);
    }

    private View.OnClickListener mViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mIvBack) {
                mFragParent.showFolder(false);
            } else if (v == mTvList) {
                mShowAsGrid = false;
                setLayout();
            }else if (v == mTvGrid){
                mShowAsGrid = true;
                setLayout();
            }else if (v == mIvSearch){
                startImageSearch();
            }
        }
    };

    private void startImageSearch(){
        Log.i(TAG, "startImageSearch()");
        Intent intent = new Intent();
        intent.setClass(mMainActivity, SearchActivity.class);
        intent.putExtra("TYPE","IMAGE");
        startActivity(intent);
        startActivityForResult(intent,3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 333){
            if (requestCode == 3){
                ProImage proImage = data.getParcelableExtra("play_image");
                Log.i(TAG, "onActivityResult: "+proImage.getMediaUrl());

                UsbImageSpUtils.getLastMediaUrl(true, proImage.getMediaUrl());
                UsbImagePlayService.instance().loadData(mListData);
                int lastPos = UsbImageSpUtils.getLastPos(false, -1);
                mFragParent.updateData(mListData, lastPos, proImage.getMediaUrl());
            }
        }
    }

    /**
     * Method used to load filters.
     */
    private void loadFilters() {
        if (isAdded()) {
            Logs.i(TAG, "-- loadFilters() --");
            if (mFilterLoadingTask != null) {
                mFilterLoadingTask.cancel(true);
                mFilterLoadingTask = null;
            }
            mFilterLoadingTask = new FilterLoadingTask(this);
            mFilterLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * 查询一级目录 TASK
     */
    private static class FilterLoadingTask extends AsyncTask<Void, Void, List<FilterFolder>> {
        WeakReference<UsbImageFolderFragment> mmWeakReferenceContext;

        FilterLoadingTask(UsbImageFolderFragment frag) {
            frag.mListData = null;
            mmWeakReferenceContext = new WeakReference<>(frag);
        }

        @Override
        protected List<FilterFolder> doInBackground(Void... voids) {
            Log.i(TAG, "FilterLoadingTask - doInBackground()");
            List<FilterFolder> list = null;
            try {
                //
                UsbImageFolderFragment frag = mmWeakReferenceContext.get();
                if (frag != null) {
                    if (frag.mListFilters == null) {
                        list = frag.mFragParent.getAllFolders();
                    } else {
                        list = frag.mListFilters;
                    }
                }
            } catch (Exception e) {
                Log.i(TAG, "");
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<FilterFolder> filterMedias) {
            super.onPostExecute(filterMedias);
            Log.i(TAG, "FilterLoadingTask - onPostExecute()");
            UsbImageFolderFragment frag = mmWeakReferenceContext.get();
            if (frag != null) {
                frag.mListFilters = filterMedias;
                frag.setLayout();
            }
        }
    }

    /**
     * Method used to loading medias.
     *
     * @param mediaFolderPath Selected folder.
     */
    private void loadMedias(String mediaFolderPath) {
        Log.i(TAG, "-- loadMedias(" + mediaFolderPath + ") --");
        if (mDataLoadingTask != null) {
            mDataLoadingTask.cancel(true);
            mDataLoadingTask = null;
        }
        mDataLoadingTask = new DataLoadingTask(this, mediaFolderPath);
        mDataLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 查询二级目录 TASK - 某文件夹下面所有媒体文件信息
     */
    private static class DataLoadingTask extends AsyncTask<Void, Void, List<ProImage>> {
        WeakReference<UsbImageFolderFragment> mmReference;
       // String mmMediaFolderPath;

        DataLoadingTask(UsbImageFolderFragment frag, String mediaFolderPath) {
            mmReference = new WeakReference<>(frag);
           // mmMediaFolderPath = mediaFolderPath;
        }

        @Override
        protected List<ProImage> doInBackground(Void... voids) {
            Log.i(TAG, "DataLoadingTask - doInBackground()");
            List<ProImage> list = null;
            try {
                // 查询条件的[列 映射 值];
                /*Map<String, String> whereColumns = new HashMap<>();
                whereColumns.put(AudioTables.AudioInfoTable.MEDIA_FOLDER_PATH, mmMediaFolderPath);

                // Query list.
                list = mmReference.get().mFragParent.getMediasByColumns(whereColumns, null);*/

                list = mmReference.get().mFragParent.getAllImages();
            } catch (Exception e) {
                Log.i(TAG, "");
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<ProImage> medias) {
            super.onPostExecute(medias);
            Log.i(TAG, "DataLoadingTask - onPostExecute()");
            UsbImageFolderFragment frag = mmReference.get();
            if (frag != null) {
                frag.mListData = medias;
                frag.setLayout();
            }
        }
    }

    private void setLayout() {
        if (mShowAsGrid) {
            //mIvGrid.setImageDrawable(getContext().getDrawable(R.drawable.grid));
            setGridLayout();
            setListSelected(mTvGrid,true);
            setListSelected(mTvList,false);
        } else {
            //mIvGrid.setImageDrawable(getContext().getDrawable(R.drawable.line));
            setLinearLayout();
            setListSelected(mTvGrid,false);
            setListSelected(mTvList,true);
        }
    }

    private void setListSelected(View view,boolean isSelected){
        if (isSelected){
            view.setBackgroundResource(R.drawable.list_choose);
        }else {
            view.setBackgroundResource(R.drawable.selector_list_chose);
        }
    }

    private void setGridLayout() {
        //
        mImgGrpAdapter.setLayoutFlag(true);
        //noinspection unchecked
        mImgGrpAdapter.setListData(mListData == null ? mListFilters : mListData);
        mRecyclerView.setAdapter(mImgGrpAdapter);

        //
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //mRecyclerView.setLayoutAnimation(RecyclerViewLayoutAnimHelper.getAnimationSetScaleBig(300));
        mRecyclerView.setItemSpace(10, false);
        mRecyclerView.setOnItemClickListener(new RecyclerItemOnClick());
    }

    private void setLinearLayout() {
        //
        mImgGrpAdapter.setLayoutFlag(false);
        //noinspection unchecked
        mImgGrpAdapter.setListData(mListData == null ? mListFilters : mListData);
        mRecyclerView.setAdapter(mImgGrpAdapter);

        //
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //mRecyclerView.setLayoutAnimation(RecyclerViewLayoutAnimHelper.getAnimationSetScaleBig(300));//缩放动画
        mRecyclerView.setItemSpace(10, false);
        mRecyclerView.setOnItemClickListener(new RecyclerItemOnClick());

    }

    private class RecyclerItemOnClick implements FastRecyclerView.OnItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            Object objItem = mImgGrpAdapter.getItem(position);
            /*if (objItem instanceof FilterFolder) {
                try {
                    FilterFolder filter = (FilterFolder) objItem;
                    loadMedias(filter.mediaFolder.getPath());
                } catch (Exception ignored) {
                }

            } else if (objItem instanceof ProImage) {
                ProImage media = (ProImage) objItem;
                mFragParent.updateData(mListData, position, media.getMediaUrl());
            }*/
            if (objItem instanceof ProImage) {
                ProImage media = (ProImage) objItem;
                Log.i(TAG, "onItemClick: "+media.getMediaUrl());
                mFragParent.updateData(mListData, position, media.getMediaUrl());
            }
        }
    }

    private class LetterSidBarCallback implements LetterSideBar.LetterSideBarListener{
        private Character mmTouchedLetter;
        @Override
        public void callback(int pos, String letter) {
            try {
                mmTouchedLetter = letter.charAt(0);
//            mImgGrpAdapter.
                int sectionPos = mImgGrpAdapter.getPositionForSection(mmTouchedLetter);
                if (sectionPos != -1) {
                    Log.i(TAG, "LetterSideBarCallback -> callback(" + pos + "," + letter + "-" + sectionPos + ")");
                    mRecyclerView.getLayoutManager().scrollToPosition(sectionPos);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTouchDown() {

        }

        @Override
        public void onTouchMove() {

        }

        @Override
        public void onTouchUp() {
            Log.i(TAG, "LetterSideBarCallback - onTouchUp()");
            refreshHLLetterOfSideBar(mmTouchedLetter);
        }
    }

    private void refreshHLLetterOfSideBar(Character c) {
        if (isAdded() && mLsb != null) {
            try {
                if (c == null){
                    if (mListData.size() > 0){
                        int firstVisiblePos = ((RecyclerView.LayoutParams) mRecyclerView.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
                        ProImage firstVisibleMedia = mListData.get(firstVisiblePos);
                        c = firstVisibleMedia.getTitlePinYin().charAt(0);
                    }
                }
            } catch (Exception e) {
               // e.printStackTrace();
                Log.e(TAG, "refreshHLLetterOfSideBar: "+ e.getMessage());
            }

            Log.i(TAG, "c:" + c);
            mLsb.refreshHlLetter(c);
        }
    }
}
