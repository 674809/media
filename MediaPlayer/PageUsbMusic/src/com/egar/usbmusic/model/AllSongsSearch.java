package com.egar.usbmusic.model;

import android.os.AsyncTask;
import android.util.Log;

import com.egar.mediaui.util.LogUtil;
import com.egar.usbmusic.present.MusicPresent;

import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.ProAudio;
import juns.lib.media.flags.FilterType;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/10/24 15:17
 * @see {@link }
 */
public class AllSongsSearch {
    private String TAG ="AllSongsModel";
    private FilterLoadingTask mFilterLoadingTask;
    private IAllSongsDataChange iAllSongsDataChange;

    public interface IAllSongsDataChange {
        void AllSongsDateChage(List<ProAudio> list);
    }
    public void setAllSongDataChangeListener(IAllSongsDataChange iAllSongs) {
        this.iAllSongsDataChange = iAllSongs;
    }
    public void loadFilters(String text) {
        LogUtil.i(TAG,"loadFilters ="+text);
        if (mFilterLoadingTask != null) {
            mFilterLoadingTask.cancel(true);
            mFilterLoadingTask = null;
        }
        mFilterLoadingTask = new FilterLoadingTask(text);
        mFilterLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 获取所有歌曲
     */
    private class FilterLoadingTask extends AsyncTask<Void, Void, List<ProAudio>> {
        private MusicPresent musicPresent;
        String[] params = new String[6];
        List<ProAudio> list = new ArrayList<>();
        FilterLoadingTask(String par) {
            musicPresent = MusicPresent.getInstance();
            params[1] = par;//mediaName

        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<ProAudio> doInBackground(Void... voids) {
            try {

             //  List<ProAudio> list = musicPresent.getAllMedias(FilterType.MEDIA_NAME, null);
                Log.i(TAG,"params ="+ params[1]);
                    list.clear();
                    list.addAll(musicPresent.getAllMedias(FilterType.MEDIA_NAME, params));
                    // Update play list of MusicPlayService.
                    musicPresent.applyPlayList(null);

                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<ProAudio> audios) {
            super.onPostExecute(audios);
            LogUtil.i(TAG,"audios size ="+audios.size());
            try {
                if(iAllSongsDataChange !=null){
                    iAllSongsDataChange.AllSongsDateChage(audios);
                }
                //   fmusicPresent.autoPlay();
            } catch (Exception e) {

            }
        }
    }
}
