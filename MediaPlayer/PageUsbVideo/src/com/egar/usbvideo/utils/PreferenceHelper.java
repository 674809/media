package com.egar.usbvideo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by:luli on 2019/5/22
 * Describe:Preference封装类
 */
public class PreferenceHelper {
    /**
     * SharedPreferences.Editor Object
     */
    private static SharedPreferences.Editor editor;
    /**
     * SharedPreferences Object
     */
    private static SharedPreferences preferences;

    /**
     * Initialize On Application Start
     */
    public static void init(Context cxt) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(cxt);
        }
        if (editor == null) {
            editor = PreferenceManager.getDefaultSharedPreferences(cxt).edit();
        }
    }

    /**
     * Save String
     */
    public static void saveString(String key, String value) {
        editor.putString(key, value).apply();
    }

    /**
     * Get String
     */
    public static String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }


    /**
     * Save Integer
     */
    public static void saveInt(String key, Integer value) {
        editor.putInt(key, value).apply();
    }

    /**
     * Get Integer
     */
    public static Integer getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }


    /**
     * Delete By Key
     */
    public static void delete(String currentKey) {
        editor.remove(currentKey).apply();
    }


    /**
     * Save Boolean
     */
    public static void saveBoolean(String key, Boolean value) {
        editor.putBoolean(key, value).apply();
    }

    /**
     * Get Boolean
     */
    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }
}
