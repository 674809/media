package com.egar.mediaui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.egar.mediaui.engine.Configs;
import com.egar.mediaui.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/11/4 09:01
 * @see {@link }
 */
public class MediaBoardcast extends BroadcastReceiver {
    private String TAG = "MediaBoardcast";
    private IMediaReceiver iMediaReceiver;
    private Map<String,IMediaReceiver> mNotifyap = new HashMap<>();

    public interface IMediaReceiver {
        void onUdiskStateChange(boolean state);

    }

    public void registerNotify(String nameKey,IMediaReceiver iMediaReceiver) {
            if(!mNotifyap.containsKey(nameKey)){
                mNotifyap.remove(nameKey);
                mNotifyap.put(nameKey,iMediaReceiver);
            }
       // this.iMediaReceiver = iMediaReceiver;
    }

    public void removeNotify(String namekye) {
        if(mNotifyap.containsKey(namekye)){
            mNotifyap.remove(namekye);
        }
      //  iMediaReceiver = null;
    }

    public void cleanMap(){
        mNotifyap.clear();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.i(TAG, "action=" + action);
        if (Configs.UDISK_MOUNT.equals(action)) {
            UdiskStateChange(true);
        } else if (Configs.UDISK_UNMOUNT.equals(action) || "android.intent.action.MEDIA_EJECT".equals(action)) {
            UdiskStateChange(false);
        }
    }


    public void UdiskStateChange(boolean state) {
        try {
            for(IMediaReceiver notify :mNotifyap.values()){
                notify.onUdiskStateChange(state);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
