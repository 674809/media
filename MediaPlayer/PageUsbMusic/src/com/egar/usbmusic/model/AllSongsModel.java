package com.egar.usbmusic.model;

import android.os.AsyncTask;

import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.present.MusicPresent;

import java.util.List;

import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.FilterType;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/24 15:17
 * @see {@link }
 */
public class AllSongsModel {
    private String TAG ="AllSongsModel";
    private FilterLoadingTask mFilterLoadingTask;
    private IAllSongsDataChange iAllSongsDataChange;

    public interface IAllSongsDataChange {
        void AllSongsDateChage(List<ProAudio> list);
    }
    public void setAllSongDataChangeListener(IAllSongsDataChange iAllSongs) {
        this.iAllSongsDataChange = iAllSongs;
    }
    public void loadFilters() {
        LogUtil.i(TAG,"loadFilters");
        if (mFilterLoadingTask != null) {
            mFilterLoadingTask.cancel(true);
            mFilterLoadingTask = null;
        }
        mFilterLoadingTask = new FilterLoadingTask();
        mFilterLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 获取所有歌曲
     */
    private class FilterLoadingTask extends AsyncTask<Void, Void, List<ProAudio>> {
        private MusicPresent musicPresent;
        FilterLoadingTask() {
            musicPresent = MusicPresent.getInstance();

        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<ProAudio> doInBackground(Void... voids) {
            try {
                String[] params = new String[6];
                params[0] = null;//folderName
                params[1] = null;//mediaName
                params[2] = null;//fileName
                params[3] = null;//artistName
                params[4] = null;//albumName
                params[5] = null;//collected
             //  List<ProAudio> list = musicPresent.getAllMedias(FilterType.MEDIA_NAME, null);
              List<ProAudio> list = musicPresent.getAllMedias(FilterType.MEDIA_NAME, params);
                // Update play list of MusicPlayService.
                musicPresent.applyPlayList(null);
                return list;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ProAudio> audios) {
            super.onPostExecute(audios);
            LogUtil.i(TAG,"audios size ="+audios.size());
            try {
                iAllSongsDataChange.AllSongsDateChage(audios);
                //   fmusicPresent.autoPlay();
            } catch (Exception e) {

            }
        }
    }
}
