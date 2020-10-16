package com.egar.mediaui.present;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.egar.mediaui.App;
import com.egar.mediaui.receiver.MediaBtnReceiver;
import com.egar.mediaui.util.LogUtil;

import java.lang.ref.WeakReference;


public class MediaBtnPresenter implements MediaBtnReceiver.MediaBtnListener {
    //TAG
    private static String TAG = "MediaBtnController";

    /**
     * {@link Context}
     */
//    private Context mContext;

    /**
     * Class ComponentName
     */
    private ComponentName mComponentName;

    /**
     * {@link AudioManager}
     */
    private AudioManager mAudioManager;

    private IMediaButton mIMediaButton;
    private static MyHandler mhandler;
    private MediaBtnReceiver mediaBtnReceiver;
    private IntentFilter intentFilter;

    public boolean isfist = true;

    public MediaBtnPresenter(Context context) {
        //
//        mContext = context;
        mComponentName = new ComponentName(context.getPackageName(), MediaBtnReceiver.class.getName());
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mhandler = new MyHandler(this);
    }

    /**
     * 注册MediaButton 广播
     *
     * @param
     */
    public void regiestMediaBtnBoardcast() {
        mediaBtnReceiver = new MediaBtnReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_BUTTON");
        App.getContext().registerReceiver(mediaBtnReceiver, intentFilter);
        regiestMediaBtnChange(this);
    }

    /**
     * 反注册 MediaButton 广播
     */
    public void unRegiestMediaBtnBoardcast() {
        App.getContext().unregisterReceiver(mediaBtnReceiver);
        unRegiestMediaBtnChange();
    }

    /**
     * Activity 注册接收事件
     * @param listener
     */
    public void regiestMediaBtnChange(MediaBtnReceiver.MediaBtnListener listener){
        mediaBtnReceiver.registerNotify(listener);
    }
    /**
     * Activity 反注册接收事件
     * @param
     */
    public void unRegiestMediaBtnChange(){
        mediaBtnReceiver.removeNotify();
    }

    /**
     * 注册声音焦点
     */
    public void register() {
        unregister();
        Log.i(TAG, "register()");
        if (mAudioManager != null) {
            mAudioManager.registerMediaButtonEventReceiver(mComponentName);
        }
    }

    /**
     * 取消声音焦点注册
     */
    public void unregister() {
        Log.i(TAG, "unregister()");
        if (mAudioManager != null) {
            mAudioManager.unregisterMediaButtonEventReceiver(mComponentName);
        }
    }

    /**
     * 软引用，防止内存溢出
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MediaBtnPresenter> mMediaBtnPresenter;
        ;
        private Context mContext;

        private MyHandler(MediaBtnPresenter mediaBtnPresenter) {
            mMediaBtnPresenter = new WeakReference<MediaBtnPresenter>(mediaBtnPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final MediaBtnPresenter mMediaBtn = mMediaBtnPresenter.get();
            if (mMediaBtn != null) {
                switch (msg.what) {
                    case 1:
                        LogUtil.i(TAG, "next long click");
                        mMediaBtn.onNextlongClick();
                        break;
                    case 2:
                        LogUtil.i(TAG, "next  click");
                        mMediaBtn.onNextClick();
                        break;
                    case 3:
                        LogUtil.i(TAG, "prev long click");
                        mMediaBtn.onPrevLongClick();
                        break;
                    case 4:
                        mMediaBtn.onPrevClick();
                        LogUtil.i(TAG, "prev  click");
                        break;
                }
            }
        }
    }

    public void onNextlongClick() {
        if (mIMediaButton != null) {
            mIMediaButton.onNextLongClick();
        }
    }

    public void onNextClick() {
        if (mIMediaButton != null) {
            mIMediaButton.onNextClick();
        }
    }

    public void onPrevLongClick() {
        if (mIMediaButton != null) {
            mIMediaButton.onPrevLongClick();
        }
    }

    public void onPrevClick() {
        if (mIMediaButton != null) {
            mIMediaButton.onPrevClick();
        }
    }

/*    Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    LogUtil.i(TAG, "next long click");
                    mImediaButton.onNextLongClick();
                    break;
                case 2:
                    LogUtil.i(TAG, "next  click");
                    mImediaButton.onNextClick();
                    break;
                case 3:
                    LogUtil.i(TAG, "prev long click");
                    mImediaButton.onPrevLongClick();
                    break;
                case 4:
                    LogUtil.i(TAG, "prev  click");
                    mImediaButton.onPrevClick();
                    break;
            }
        }
    };*/

    public interface IMediaButton {
        /**
         * 下一首长按事件
         */
        void onNextLongClick();

        /**
         * 下一首短按事件
         */
        void onNextClick();

        /**
         * 上一首长按事件
         */
        void onPrevLongClick();

        /**
         * 上一首短按事件
         */
        void onPrevClick();
    }

    public void setMediaButListener(IMediaButton imediaButton) {
        this.mIMediaButton = imediaButton;
    }

    @Override
    public void onMediaButton(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        if (isfist) {
                            mhandler.sendMessageDelayed(mhandler.obtainMessage(1), 1000);
                            isfist = false;
                        }
                        break;
                    case KeyEvent.ACTION_UP:
                        boolean hamessage = mhandler.hasMessages(1);
                        // LogUtil.i(TAG,"hamessage="+hamessage);
                        if (hamessage) {
                            mhandler.sendEmptyMessage(2);
                            mhandler.removeMessages(1);
                        }
                        isfist = true;
                        break;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                switch (event.getAction()) {
                    case KeyEvent.ACTION_DOWN:
                        if (isfist) {
                            mhandler.sendMessageDelayed(mhandler.obtainMessage(3), 1000);
                            isfist = false;
                        }
                        break;
                    case KeyEvent.ACTION_UP:
                        boolean hamessage = mhandler.hasMessages(3);
                        //    LogUtil.i(TAG,"hamessage="+hamessage);
                        if (hamessage) {
                            mhandler.sendEmptyMessage(4);
                            mhandler.removeMessages(3);
                        }
                        isfist = true;
                        break;
                }
                //   LogUtil.i(TAG, "prev: ");
                break;
        }
    }
}
