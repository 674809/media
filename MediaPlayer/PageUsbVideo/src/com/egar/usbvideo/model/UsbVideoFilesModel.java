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
public class UsbVideoFilesModel {

    private final String TAG = "VideoListModel";
    private boolean mIsScanWhenLocalMediaIsEmpty = true;
    private VideoPresent mVideoPresent;
    private IVideoFilesDataListener iVideoFilesDataListener;
    private LoadLocalMediasTask mLoadLocalMediasTask;


    public UsbVideoFilesModel() {
        mVideoPresent = VideoPresent.getInstance();
    }


    public void setVideoFilsDataListener(IVideoFilesDataListener iVideoFilesDataListener) {
        this.iVideoFilesDataListener = iVideoFilesDataListener;
    }

    public interface IVideoFilesDataListener {
        void VideoFilesDataChange(List<ProVideo> aVoid);
    }

    public void LoadData() {

            Log.i(TAG, "refreshData()");
            if (mLoadLocalMediasTask != null) {
                mLoadLocalMediasTask.cancel(true);
                mLoadLocalMediasTask = null;
            }
            mLoadLocalMediasTask = new LoadLocalMediasTask();
            mLoadLocalMediasTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    class LoadLocalMediasTask extends AsyncTask<Object, Integer, List<ProVideo>> {
        List<ProVideo> list = new ArrayList<>();
        public LoadLocalMediasTask() {
        }
        @Override
        protected  List<ProVideo> doInBackground(Object... objects) {

            try {
                if (mVideoPresent != null) {
                    list.clear();
                    list.addAll(mVideoPresent.getAllMedias(MediaType.VIDEO, FilterType.MEDIA_NAME, null));
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
            if(iVideoFilesDataListener !=null){
                iVideoFilesDataListener.VideoFilesDataChange(aVoid);
            }
        }
    }


}























