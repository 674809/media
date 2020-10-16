package com.egar.usbvideo.model;

import android.os.AsyncTask;

import com.egar.mediaui.util.LogUtil;
import com.egar.usbvideo.present.VideoPresent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import juns.lib.media.bean.FilterFolder;
import juns.lib.media.bean.ProVideo;
import juns.lib.media.db.tables.VideoTables;
import juns.lib.media.flags.MediaType;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/28 11:19
 * @see {@link }
 */
public class UsbVideoFolderMode {
    private String TAG = "UsbVideoFolderMode";
    private VideoPresent mVideoPresent;
    private FilterLoadingTask mFilterLoadingTask;
    private DataLoadingTask mDataLoadingTask;
    private IVideFolderDataChange iVideFolderDataChange;


    public interface IVideFolderDataChange {
        void VideoFolderDateChage(List<FilterFolder> filterFolders);

        void VideoFileDataChange(List<ProVideo> video);
    }
    public void setVideoFolderDataChangeListener( IVideFolderDataChange iVideFolderChange) {
        iVideFolderDataChange = iVideFolderChange;
    }
    //调用时判断isadd
    public void loadFilters() {
        LogUtil.d(TAG, "-- loadFilters() --");
        if (mFilterLoadingTask != null) {
            mFilterLoadingTask.cancel(true);
            mFilterLoadingTask = null;
        }
        mFilterLoadingTask = new FilterLoadingTask();
        mFilterLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void loadMedias(String mediaFolderPath) {
        if (mDataLoadingTask != null) {
            mDataLoadingTask.cancel(true);
            mDataLoadingTask = null;
        }
        mDataLoadingTask = new DataLoadingTask( mediaFolderPath);
        mDataLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
     /**
      *  查询文件夹目录
     */
    private  class FilterLoadingTask extends AsyncTask<Void, Void, List<FilterFolder>> {
        FilterLoadingTask() {
            mVideoPresent = VideoPresent.getInstance();
        }
        @SuppressWarnings("unchecked")
        @Override
        protected List<FilterFolder> doInBackground(Void... voids) {

            List<FilterFolder> list = new ArrayList<>();
            try {
                //
                list.clear();
                list.addAll(mVideoPresent.getFilterFolders(MediaType.VIDEO));

            } catch (Exception e) {

            }
            return list;
        }

        @Override
        protected void onPostExecute(List<FilterFolder> filterMedias) {
            super.onPostExecute(filterMedias);
                iVideFolderDataChange.VideoFolderDateChage(filterMedias);
        }
    }



    /**
     * 根据文件夹名称获取文件夹中的文件
     */
    private  class DataLoadingTask extends AsyncTask<Void, Void, List<ProVideo>> {

        String mmMediaFolderPath;

        DataLoadingTask( String mediaFolderPath) {
            mmMediaFolderPath = mediaFolderPath;
            mVideoPresent = VideoPresent.getInstance();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<ProVideo> doInBackground(Void... voids) {

            List<ProVideo> list = new ArrayList<>();
            try {
                Map<String, String> mapColumns = new HashMap<>();
                mapColumns.put(VideoTables.VideoInfoTable.MEDIA_FOLDER_PATH, mmMediaFolderPath);
                list.clear();
                list.addAll(mVideoPresent.getMediasByColumns(MediaType.VIDEO, mapColumns, null));

            } catch (Exception e) {

            }
            return list;
        }

        @Override
        protected void onPostExecute(List<ProVideo> videos) {
            super.onPostExecute(videos);
            iVideFolderDataChange.VideoFileDataChange(videos);
        }
    }
}
