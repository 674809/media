package com.egar.usbimage.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.egar.mediaui.R;
import com.egar.mediaui.lib.BaseAppV4Fragment;
import com.egar.usbimage.view.ZoomGifImageView;

import juns.lib.media.bean.ProImage;

public class UsbImageContentFragment extends BaseAppV4Fragment {
    //TAG
    private static final String TAG = "UsbImgContentFrag";

    //
    private View mContentView;
    private ZoomGifImageView mIvContent;
    private ProImage mMedia;

    public void setMedia(ProImage media) {
        mMedia = media;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.usb_image_frag_content, container, false);
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        Log.i(TAG, "init: ");
        mIvContent = (ZoomGifImageView) mContentView.findViewById(R.id.iv_zoom_gif);
        mIvContent.setTapListener(new ZoomGifImageView.ZoomGifImgTabListener() {
            @Override
            public void onSingleTap() {
                Fragment fragment = getParentFragment();
                if (fragment instanceof UsbImageMainFragment) {
                    UsbImageMainFragment parentFragment = (UsbImageMainFragment) fragment;
                    parentFragment.switchCoverInfo();
                }
            }

            @Override
            public void onDoubleTap() {
            }
        });
        loadData();
    }

//    Bitmap bm = null;
    private void loadData() {
        String mediaUrl = "";
        try {
            mediaUrl = mMedia.getMediaUrl();
            Log.i(TAG, "loadData() >> mediaUrl:[" + mediaUrl + "]");
        } catch (Exception ignored) {
        }
        /*try {
            GifDrawable gifDrawable = new GifDrawable(mediaUrl);
            mIvContent.setImageDrawable(gifDrawable);
            Log.i(TAG, "loadData() >> 'Load as GifDrawable'");
            return;
        } catch (Exception ignored) {
            Log.d(TAG,"---------------Exception1------------------");
        }
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(mediaUrl);
            Matrix matrix = new Matrix();
            matrix.setScale(0.5f, 0.5f);
            bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
//            mIvContent.setImageURI(Uri.parse(mMedia.getMediaUrl()));
            mIvContent.setImageBitmap(bm);
            if (bitmap != null){
                bitmap.recycle();
                System.gc();
            }
            Log.i(TAG, "loadData() >> 'Load as NORMAL drawable'");
        } catch (Exception ignored) {
            Log.d(TAG,"---------------Exception2------------------");
        }*/

        Glide.with(UsbImageContentFragment.this).load(mediaUrl).into(mIvContent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"onDestroyView");
//        Log.d(TAG,"onDestroyView _bm != null --> "+(bm != null));
//        if (bm != null){
//            bm.recycle();
//            System.gc();
//        }
    }
}
