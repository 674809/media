package com.egar.mediaui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;


/**
 * Media button broadcast receiver.
 * <p>1. Register {@link MediaBtnReceiver} with action:"android.intent.action.MEDIA_BUTTON" in your 'AndroidManifest.xml'</p>
 * <p>2. Register in where you want use media button {@link android.media.AudioManager#registerMediaButtonEventReceiver}</p>
 *
 * @author Jun.Wang
 */
public class MediaBtnReceiver extends BroadcastReceiver {
    //TAG
    private final String TAG = "ybfBtnReceiver";
    private MediaBtnListener iMediaBtnListener;
   // protected static Map<String, MediaBtnListener> mMapNotifys = new HashMap<String, MediaBtnListener>();


    public interface MediaBtnListener {
        void onMediaButton(KeyEvent event);
    }

    public  void registerNotify(MediaBtnListener iMediaBtnListener) {
      this.iMediaBtnListener = iMediaBtnListener;
    }

    public  void removeNotify() {
        iMediaBtnListener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        mediaButton(keyEvent);
    }


    public void mediaButton(KeyEvent event) {
        iMediaBtnListener.onMediaButton(event);
    /*    try {
            for (MediaBtnListener notify : mMapNotifys.values()) {
                notify.onMediaButton(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


}
