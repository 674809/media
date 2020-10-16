package com.egar.radio.utils;

/**
 * Preference storage methods
 *
 * @author Jun.Wang
 */
public class EgarRadioPreferUtils extends RadioPreferUtils {
    //TAG
    private static final String TAG = "PreferUtils";

    public static void SetFirstOpenRadioUI(boolean isFirstOpen){
        //TODO implement
        final String PREFER_KEY = "com.egar.radio.FIRST_OPEN_RADIOUI_FLAG";
        saveBoolean(PREFER_KEY, (!isFirstOpen));
    }


    public static boolean isFirstOpenRadioUI() {
        final String PREFER_KEY = "com.egar.radio.FIRST_OPEN_RADIOUI_FLAG";
        boolean isOpened = getBoolean(PREFER_KEY, false);
        LogUtil.d(TAG,"isFirstOpenRadioUI()   isOpened:  " + isOpened);
        if (!isOpened) {
            saveBoolean(PREFER_KEY, true);
        }
        return !isOpened;
    }
}
