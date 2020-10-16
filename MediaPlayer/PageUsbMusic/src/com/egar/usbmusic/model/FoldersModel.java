package com.egar.usbmusic.model;

import android.os.AsyncTask;
import android.util.Log;

import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.bean.FilterParams;
import com.egar.usbmusic.present.MusicPresent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import juns.lib.java.utils.EmptyUtil;
import juns.lib.media.bean.FilterFolder;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.db.tables.AudioTables;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/18 18:37
 * @see {@link }
 */
public class FoldersModel {
    private String TAG = "FoldersModel";
    private FilterLoadingTask mFilterLoadingTask;
    private DataLoadingTask mDataLoadingTask;
    private static FoldersModel foldersModel;
    private MusicPresent mMusicPresent;
    private IFolderDataChange iFolderDataChange;

    public static FoldersModel getInstatnce() {
        if (foldersModel == null) {
            foldersModel = new FoldersModel();

        }
        return foldersModel;
    }

    //调用此方法，请判断fragment isAdded()
    public void loadFilters() {
        mMusicPresent = MusicPresent.getInstance();
        LogUtil.i(TAG, "-- loadFilters() --");
        if (mFilterLoadingTask != null) {
            mFilterLoadingTask.cancel(true);
            mFilterLoadingTask = null;
        }
        mFilterLoadingTask = new FilterLoadingTask();
        mFilterLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public FilterLoadingTask getFilterLoadingTask() {
        return mFilterLoadingTask;
    }

    public void loadMedias(String mediaFolderPath) {
        Log.i(TAG, "-- loadMedias(" + mediaFolderPath + ") --");
        mMusicPresent = MusicPresent.getInstance();
        if (mDataLoadingTask != null) {
            mDataLoadingTask.cancel(true);
            mDataLoadingTask = null;
        }
        mDataLoadingTask = new DataLoadingTask(mediaFolderPath);
        mDataLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    /**
     * 查询一级目录 TASK
     */
    private class FilterLoadingTask extends AsyncTask<Void, Void, List<FilterFolder>> {

        public List<FilterFolder> mListFilters  = new ArrayList<>();

        public FilterLoadingTask() {

        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<FilterFolder> doInBackground(Void... voids) {
            try {
                mListFilters.clear();
                mListFilters.addAll(mMusicPresent.getFilterFolders());
            } catch (Exception e) {
                Log.i(TAG, e.toString());
            }
            return mListFilters;
        }

        @Override
        protected void onPostExecute(List<FilterFolder> filterMedias) {
            super.onPostExecute(filterMedias);
            LogUtil.i(TAG, "DataLoadingTask - onPostExecute() ==" + filterMedias.size());

            if (!EmptyUtil.isEmpty(filterMedias)) {
                // frag.showLoading(false);
                //   LogUtil.i(TAG,"mListFilters 不为空");
            }
            if (iFolderDataChange != null) {
                iFolderDataChange.FolderDateChage(mListFilters);
            }

        }

    }

    public void setFolderDataChangeListener(IFolderDataChange iFolder) {
        iFolderDataChange = iFolder;
    }

    public interface IFolderDataChange {
        void FolderDateChage(List<FilterFolder> filterFolders);

        void FileDataChange(List<ProAudio> audios);
    }


    /**
     * 查询二级目录 TASK - 某文件夹下面所有媒体文件信息
     */
    private class DataLoadingTask extends AsyncTask<Void, Void, List<ProAudio>> {
        private String TAG = "FoldersModel";
        private String filePath;

        public DataLoadingTask(String Path) {
            filePath = Path;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<ProAudio> doInBackground(Void... voids) {
            LogUtil.i(TAG, "DataLoadingTask - doInBackground()");
            List<ProAudio> list = null;
            try {
                Map<String, String> mapColumns = new HashMap<>();
                mapColumns.put(AudioTables.AudioInfoTable.MEDIA_FOLDER_PATH, filePath);
                list = mMusicPresent.getMediasByColumns(mapColumns, null);
                //Update play list.
                FilterParams fps = new FilterParams();
                fps.setFolderPath(filePath);
                mMusicPresent.applyPlayList(fps.getParams());
            } catch (Exception e) {
                LogUtil.i(TAG, "");
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<ProAudio> audios) {
            super.onPostExecute(audios);
            Log.i(TAG, "DataLoadingTask - onPostExecute() audios =" + audios.size());
            if (iFolderDataChange != null) {
                iFolderDataChange.FileDataChange(audios);
            }


        }
    }

}
