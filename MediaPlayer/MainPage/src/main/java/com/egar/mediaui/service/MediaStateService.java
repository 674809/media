package com.egar.mediaui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/24 11:05
 * @see {@link }
 */
public class MediaStateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
