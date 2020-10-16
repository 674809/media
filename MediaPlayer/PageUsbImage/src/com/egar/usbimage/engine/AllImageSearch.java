package com.egar.usbimage.engine;

import android.os.AsyncTask;
import android.util.Log;

import com.egar.usbvideo.present.VideoPresent;

import java.util.ArrayList;
import java.util.List;

import juns.lib.media.bean.ProImage;
import juns.lib.media.flags.FilterType;
import juns.lib.media.flags.MediaType;

/**
 * Created by:luli on 2020/7/17
 */
public class AllImageSearch {
    private final String TAG = "AllImageSearch";
    private VideoPresent mVideoPresent;
    private IImageFilesDataListener mImageFilesDataListener;
    private LoadLocalMediasTask mLoadLocalMediasTask;


    public AllImageSearch() {
        mVideoPresent = VideoPresent.getInstance();
        mVideoPresent.initScanService();
    }


    public void setImageFilsDataListener(IImageFilesDataListener imageFilesDataListener) {
        this.mImageFilesDataListener = imageFilesDataListener;
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

    public interface IImageFilesDataListener {
        void imageFilesDataChange(List<ProImage> aVoid);
    }

    class LoadLocalMediasTask extends AsyncTask<Object, Integer, List<ProImage>> {
        List<ProImage> list = new ArrayList<>();
        String[] params = new String[6];

        public LoadLocalMediasTask(String text) {
            params[1] = text;//mediaName
        }

        @Override
        protected List<ProImage> doInBackground(Object... objects) {

            try {
                if (mVideoPresent != null) {
                    list.clear();
                    List allImageMedias = mVideoPresent.getAllMedias(MediaType.IMAGE, FilterType.MEDIA_NAME, params);
                    list.addAll(allImageMedias);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //long count = attachedActivity.getCount(MediaType.VIDEO);
            return list;
        }


        @Override
        protected void onPostExecute(List<ProImage> images) {
            super.onPostExecute(images);
            Log.i(TAG, "image size =" + images.size());
            if (mImageFilesDataListener != null) {
                mImageFilesDataListener.imageFilesDataChange(images);
            }
        }
    }
}
