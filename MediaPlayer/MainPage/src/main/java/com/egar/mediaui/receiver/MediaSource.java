package com.egar.mediaui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.egar.mediaui.engine.Configs;

import java.util.HashMap;
import java.util.Map;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/11/4 09:01
 * @see {@link }
 */
public class MediaSource extends BroadcastReceiver {

    protected static Map<String, IMediaSorce> mNotifys = new HashMap<String, IMediaSorce>();

    public interface IMediaSorce {
        /**
         * source 事件，切换UI
         */
        void onSouceChange();

    }

    public static void registerNotify(String notifyKey, IMediaSorce mediaBtnListener) {
        if (!mNotifys.containsKey(notifyKey)) {
            mNotifys.remove(notifyKey);
            mNotifys.put(notifyKey, mediaBtnListener);
        }
    }

    public static void removeNotify(String notifyKey) {
        if (mNotifys.containsKey(notifyKey)) {
            mNotifys.remove(notifyKey);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Configs.SOURCE_EVENT.equals(action)) {
            souceChange();
        }
    }


    public void souceChange() {
        try {
            for (IMediaSorce notify : mNotifys.values()) {
                notify.onSouceChange();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
