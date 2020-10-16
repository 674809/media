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
import juns.lib.media.bean.FilterMedia;
import juns.lib.media.bean.ProAudio;
import juns.lib.media.db.tables.AudioTables;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/22 15:54
 * @see {@link }
 */
public class ArtistsMode {

    private String TAG = "ArtistsMode";

    private IArtistDataChange iArtistDataChange;
    private ArtistLoadingTask mArtistLoadingTask;
    private DataLoadingTask mDataLoadingTask;

    public void setArtistDataChangeListener(IArtistDataChange iArtistData) {
        iArtistDataChange = iArtistData;
    }

    public interface IArtistDataChange {
        void ArtistsDateChage(List<FilterMedia> filterFolders);

        void ArtistsFileDataChange(List<ProAudio> audios);
    }

    //调用此方法，请判断fragment isAdded()
    public void loadFilters() {

        LogUtil.i(TAG, "-- loadFilters() --");
        if (mArtistLoadingTask != null) {
            mArtistLoadingTask.cancel(true);
            mArtistLoadingTask = null;
        }
        mArtistLoadingTask = new ArtistLoadingTask();
        mArtistLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void loadMedias(String mediaFolderPath) {
        Log.i(TAG, "-- loadMedias(" + mediaFolderPath + ") --");
        if (mDataLoadingTask != null) {
            mDataLoadingTask.cancel(true);
            mDataLoadingTask = null;
        }
        mDataLoadingTask = new DataLoadingTask(mediaFolderPath);
        mDataLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private class ArtistLoadingTask extends AsyncTask<Void, Void, List<FilterMedia>> {
        private MusicPresent musicPresent;
        public List<FilterMedia> mListFilters = new ArrayList<>();

        public ArtistLoadingTask() {
            musicPresent = MusicPresent.getInstance();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<FilterMedia> doInBackground(Void... voids) {
            try {
                mListFilters.clear();
                mListFilters.addAll(musicPresent.getFilterArtists());
            } catch (Exception e) {
                Log.i(TAG, e.toString());
            }
            return mListFilters;
        }

        @Override
        protected void onPostExecute(List<FilterMedia> filterMedias) {
            super.onPostExecute(filterMedias);
            LogUtil.i(TAG, "DataLoadingTask - onPostExecute() ==" + filterMedias.size());

            if (!EmptyUtil.isEmpty(filterMedias)) {
                // frag.showLoading(false);
                //   LogUtil.i(TAG,"mListFilters 不为空");
            }
            if(iArtistDataChange !=null){
                iArtistDataChange.ArtistsDateChage(filterMedias);

            }
        }
    }


    /**
     * 查询二级目录 TASK - 某文件夹下面所有媒体文件信息
     */
    private class DataLoadingTask extends AsyncTask<Void, Void, List<ProAudio>> {
        String mmMediaArtist;
        private MusicPresent musicPresent;

        DataLoadingTask(String artist) {
            mmMediaArtist = artist;
            musicPresent = MusicPresent.getInstance();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<ProAudio> doInBackground(Void... voids) {

            List<ProAudio> list = null;
            try {
                Map<String, String> mapColumns = new HashMap<>();
                mapColumns.put(AudioTables.AudioInfoTable.ARTIST, mmMediaArtist);
                list = musicPresent.getMediasByColumns(mapColumns, null);

                //Update play list.
                FilterParams fps = new FilterParams();
                fps.setArtist(mmMediaArtist);
                musicPresent.applyPlayList(fps.getParams());
            } catch (Exception e) {

            }
            return list;
        }

        @Override
        protected void onPostExecute(List<ProAudio> audios) {
            super.onPostExecute(audios);
            Log.i(TAG, "-- FilterLoadingTask > onPostExecute() --");
            if(iArtistDataChange !=null){
                iArtistDataChange.ArtistsFileDataChange(audios);
            }

        }
    }
}
