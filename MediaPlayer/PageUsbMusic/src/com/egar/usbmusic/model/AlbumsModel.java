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

import juns.lib.android.utils.Logs;
import juns.lib.media.bean.FilterMedia;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.db.tables.AudioTables;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/18 18:37
 * @see {@link }
 */
public class AlbumsModel {
    private String TAG = "FoldersModel";
    private FilterLoadingTask mFilterLoadingTask;
    private DataLoadingTask mDataLoadingTask;
    private AlbumsModel mFoldersModel;
    private MusicPresent mMusicPresent;
    private IAlbumsDataChange iAlbumsDataChange;


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
    private class FilterLoadingTask extends AsyncTask<Void, Void, List<FilterMedia>> {
        private MusicPresent musicPresent;

        FilterLoadingTask() {
            musicPresent = MusicPresent.getInstance();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<FilterMedia> doInBackground(Void... voids) {
            List<FilterMedia> list = new ArrayList<>();
            try {
                list.clear();
                list.addAll(musicPresent.getFilterAlbums());

            } catch (Exception e) {
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<FilterMedia> filterMedias) {
            super.onPostExecute(filterMedias);
            if (iAlbumsDataChange != null) {
                iAlbumsDataChange.AlbumsFolderDateChange(filterMedias);
            }

        }
    }


    /**
     * 查询二级目录 TASK - 某文件夹下面所有媒体文件信息
     */
    private class DataLoadingTask extends AsyncTask<Void, Void, List<ProAudio>> {

        String mmMediaAlbum;
        private MusicPresent musicPresent;

        DataLoadingTask(String album) {
            mmMediaAlbum = album;
            musicPresent = MusicPresent.getInstance();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<ProAudio> doInBackground(Void... voids) {
            try {
                Logs.i(TAG, "DataLoadingTask - doInBackground()");
                Map<String, String> mapColumns = new HashMap<>();
                mapColumns.put(AudioTables.AudioInfoTable.ALBUM, mmMediaAlbum);
                List<ProAudio> list = musicPresent.getMediasByColumns(mapColumns, null);

                //Update play list.
                FilterParams fps = new FilterParams();
                fps.setAlbum(mmMediaAlbum);
                musicPresent.applyPlayList(fps.getParams());
                return list;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ProAudio> audios) {
            super.onPostExecute(audios);
            if (iAlbumsDataChange != null) {
                iAlbumsDataChange.AlbumsFileDataChange(audios);
            }

        }
    }

    public void setAlbumDataChangeListener(IAlbumsDataChange iAlbums) {
        this.iAlbumsDataChange = iAlbums;
    }

    public interface IAlbumsDataChange {
        void AlbumsFolderDateChange(List<FilterMedia> filterFolders);

        void AlbumsFileDataChange(List<ProAudio> audios);
    }

}
