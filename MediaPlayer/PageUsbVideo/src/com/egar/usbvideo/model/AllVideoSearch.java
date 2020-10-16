package com.egar.usbvideo.model;

import android.os.AsyncTask;
import android.util.Log;

import com.egar.usbvideo.present.VideoPresent;

import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.ProVideo;
import juns.lib.media.flags.FilterType;
import juns.lib.media.flags.MediaType;


/**
 * Created by:luli on 2019/6/19
 */
public class AllVideoSearch {

    private final String TAG = "AllVideoSearch";
    private boolean mIsScanWhenLocalMediaIsEmpty = true;
    private VideoPresent mVideoPresent;
    private IVideoFilesDataListener iVideoFilesDataListener;
    private LoadLocalMediasTask mLoadLocalMediasTask;


    public AllVideoSearch() {
        mVideoPresent = VideoPresent.getInstance();
    }


    public void setVideoFilsDataListener(IVideoFilesDataListener iVideoFilesDataListener) {
        this.iVideoFilesDataListener = iVideoFilesDataListener;
    }

    public interface IVideoFilesDataListener {
        void VideoFilesDataChange(List<ProVideo> aVoid);
    }

    public void LoadData(String text) {
            Log.i(TAG, "refreshData()");
            if (mLoadLocalMediasTask != null) {
                mLoadLocalMediasTask.cancel(true);
                mLoadLocalMediasTask = null;
            }
            mLoadLocalMediasTask = new LoadLocalMediasTask(text);
            mLoadLocalMediasTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    class LoadLocalMediasTask extends AsyncTask<Object, Integer, List<ProVideo>> {
        List<ProVideo> list = new ArrayList<>();
        String[] params = new String[6];
        public LoadLocalMediasTask(String text) {
            params[1] = text;//mediaName
        }
        @Override
        protected  List<ProVideo> doInBackground(Object... objects) {

            try {
                if (mVideoPresent != null) {
                    list.clear();
                    list.addAll(mVideoPresent.getAllMedias(MediaType.VIDEO, FilterType.MEDIA_NAME, params));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //long count = attachedActivity.getCount(MediaType.VIDEO);
            return list;
        }


        @Override
        protected void onPostExecute( List<ProVideo> aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG,"aVoid size ="+aVoid.size());
            if(iVideoFilesDataListener !=null){
                iVideoFilesDataListener.VideoFilesDataChange(aVoid);
            }
        }
    }
}























