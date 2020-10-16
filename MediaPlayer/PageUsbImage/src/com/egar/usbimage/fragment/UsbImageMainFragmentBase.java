package com.egar.usbimage.fragment;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;

import com.egar.mediaui.MainActivity;
import com.egar.mediaui.fragment.BaseUsbScrollLimitFragment;
import com.egar.scanner.api.EgarApiScanner;
import com.egar.scanner.api.MediaScanRespFactory;
import com.egar.usbimage.engine.UsbImagePlayService;

import java.util.List;
import java.util.Map;

import juns.lib.media.bean.FilterFolder;
import juns.lib.media.bean.ProImage;
import juns.lib.media.flags.FilterType;
import juns.lib.media.flags.MediaType;
import juns.lib.media.scanner.IMediaScanListener;

public class UsbImageMainFragmentBase extends BaseUsbScrollLimitFragment
        implements EgarApiScanner.IEgarApiScanListener,
        IMediaScanListener {

    // Attached activity of this fragment.
    protected MainActivity mAttachedActivity;

    private  String TAG = "UsbImageMainFragmentBase";

    /**
     * API object.
     */
    private EgarApiScanner mApiScanner;
    private IMediaScanListener mMediaScanListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAttachedActivity = (MainActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPageLoadStart() {
        super.onPageLoadStart();
        Log.d(TAG, "UsbImageMainFragmentBase_onPageLoadStart ");
        bindScanService(true);
        UsbImagePlayService.instance().reqAudioFocus();
    }

    @Override
    public void onPageLoadStop() {
        super.onPageLoadStop();
        Log.d(TAG, "UsbImageMainFragmentBase_onPageLoadStop ");
    }

    @Override
    public void initView() {
        super.initView();
        mApiScanner = new EgarApiScanner(mAttachedActivity, this);
    }

    /**
     * {@link EgarApiScanner#bindScanService()}
     * & {@link EgarApiScanner#unbindScanService()}
     *
     * @param isConnected true- Execute BIND; false- Execute UNBIND.
     */
    protected void bindScanService(boolean isConnected) {
        if (mApiScanner != null) {
            if (isConnected) {
                mApiScanner.bindScanService();
            } else {
                mApiScanner.unbindScanService();
            }
        }
    }

    @Override
    public void onMediaScanServiceConnected() {
        if (mApiScanner != null) {
            mMediaScanListener = new MediaScanRespFactory(this).getRespCallback();
            mApiScanner.addScanListener(MediaType.IMAGE, false, null, mMediaScanListener);
        }
    }

    @Override
    public void onMediaScanServiceDisconnected() {
        removeScanListener();
    }

    private void removeScanListener() {
        if (mApiScanner != null) {
            mApiScanner.removeScanListener(null, mMediaScanListener);
        }
    }

    @Override
    public void onRespScanState(int scanState) {
    }

    @Override
    public void onRespMountChange(List list) {
    }

    @Override
    public void onRespDeltaMedias(List list) {
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    public List<ProImage> getAllImages() {
        if (mApiScanner != null) {
            try {
                //noinspection unchecked
                return mApiScanner.getAllMedias(MediaType.IMAGE, FilterType.NOTHING, null);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public List<FilterFolder> getAllFolders() {
        if (mApiScanner != null) {
            try {
                //noinspection unchecked
                return mApiScanner.getFilterFolders(MediaType.IMAGE);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public List<ProImage> getMediasByColumns(Map<String, String> whereColumns, String sortOrder) {
        if (mApiScanner != null) {
            List listResp = mApiScanner.getMediasByColumns(MediaType.IMAGE, whereColumns, sortOrder);
            if (listResp != null) {
                //noinspection unchecked
                return (List<ProImage>) listResp;
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onBack() {
        super.onBack();
        mAttachedActivity.exitApp();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPageLoadStop();
        //LogUtil.i(TAG, "onDetach");
    }
}
