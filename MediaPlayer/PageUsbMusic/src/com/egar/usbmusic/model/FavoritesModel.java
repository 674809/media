package com.egar.usbmusic.model;

import android.os.AsyncTask;
import android.util.Log;

import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.bean.FilterParams;
import com.egar.usbmusic.present.MusicPresent;

import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.FilterType;
import juns.lib.media.flags.MediaCollectState;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/24 15:17
 * @see {@link }
 */
public class FavoritesModel {
    private String TAG = "FavoritesModel";
    private DataLoadingTask mFilterLoadingTask;
    private IFavoritesDataChange iFavoritesDataChange;

    public interface IFavoritesDataChange {
        void FavoritesDateChage(List<ProAudio> list);
    }

    public void setFavoriteDataChangeListener(IFavoritesDataChange ifavorite) {
        this.iFavoritesDataChange = ifavorite;
    }

    public void loadFilters() {
        LogUtil.i(TAG, "loadFilters");
        if (mFilterLoadingTask != null) {
            mFilterLoadingTask.cancel(true);
            mFilterLoadingTask = null;
        }
        mFilterLoadingTask = new DataLoadingTask();
        mFilterLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // 获取数据
    private class DataLoadingTask extends AsyncTask<Void, Void, List<ProAudio>> {

        private MusicPresent musicPresent;

        DataLoadingTask() {
            musicPresent = MusicPresent.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<ProAudio> doInBackground(Void... voids) {

            List<ProAudio> listToReturn = new ArrayList<>();
            try {
                //Construct parameters.
                FilterParams fps = new FilterParams();
                fps.setCollect(MediaCollectState.COLLECTED);
                // Get all collected medias.
               // listToReturn.clear();

                listToReturn.addAll(musicPresent.getAllMedias(FilterType.MEDIA_NAME, fps.getParams()));
                //Update play list.
                Log.i(TAG,"listToReturn isze ="+listToReturn.size());
                musicPresent.applyPlayList(fps.getParams());
            } catch (Exception e) {
                listToReturn = null;
            }
            return listToReturn;
        }

        @Override
        protected void onPostExecute(List<ProAudio> audios) {
            super.onPostExecute(audios);
            try {
                LogUtil.i(TAG, "加载完成 size =" + audios.size());
                iFavoritesDataChange.FavoritesDateChage(audios);
            } catch (Exception e) {
                Log.i(TAG, "");
            }
        }
    }
}
