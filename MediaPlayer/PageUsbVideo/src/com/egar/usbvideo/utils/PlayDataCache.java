package com.egar.usbvideo.utils;

import java.lang.ref.WeakReference;
import java.util.List;

import juns.lib.media.bean.ProVideo;

public class PlayDataCache {
    private static WeakReference<String> mWeakReferenceMediaUrl;
    private static WeakReference<List<ProVideo>> mWeakReferenceMedias;

    public static void cache(String mediaUrlToPlay, List<ProVideo> mediasToPlay) {
        mWeakReferenceMediaUrl = new WeakReference<>(mediaUrlToPlay);
        mWeakReferenceMedias = new WeakReference<>(mediasToPlay);
    }

    public static String getMediaUrlToPlay() {
        try {
            String resMediaUrl = mWeakReferenceMediaUrl.get();
            mWeakReferenceMediaUrl.clear();
            return resMediaUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static List<ProVideo> getMediasToPlay() {
        try {
            List<ProVideo> resMedias = mWeakReferenceMedias.get();
            mWeakReferenceMedias.clear();
            return resMedias;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
