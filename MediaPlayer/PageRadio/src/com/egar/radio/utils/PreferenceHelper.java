package com.egar.radio.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;
import java.util.Set;

/**
 * Common PreferenceHelper
 * <p>
 * 必须初始化 {@link PreferenceHelper#init(Context)}
 * </p>
 *
 * @author Jun.Wang
 */
public class PreferenceHelper {
    private static final String TAG = "PreferenceHelper";

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
        LogUtil.d(TAG,"preferences:  " +  preferences  + "   /editor: " + editor);
    }

    /**
     * Save String
     */
    public static void saveString(String key, String value) {
        editor.putString(key, value).commit();
    }

    /**
     * Save Set<String>
     */
    @SuppressLint("NewApi")
    public static void saveStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value).commit();
    }

    /**
     * Save Integer
     */
    public static void saveInt(String key, Integer value) {
        editor.putInt(key, value).commit();
    }

    /**
     * Save Long
     */
    public static void saveLong(String key, Long value) {
        editor.putLong(key, value).commit();
    }

    /**
     * Save Float
     */
    public static void saveFloat(String key, Float value) {
        editor.putFloat(key, value).commit();
    }

    /**
     * Delete By Key
     */
    public static void delete(String currentKey) {
        editor.remove(currentKey).commit();
    }

    /**
     * Clear all data
     */
    public static void clearAll() {
        editor.clear().commit();
    }

    /**
     * Save Boolean
     */
    public static void saveBoolean(String key, Boolean value) {
        editor.putBoolean(key, value).commit();
    }

    /**
     * Get String
     */
    public static String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    /**
     * Get Set<String>
     */
    @SuppressLint("NewApi")
    public static Set<String> getStringSet(String key, Set<String> defaultValue) {
        return preferences.getStringSet(key, defaultValue);
    }

    /**
     * Get Integer
     */
    public static Integer getInt(String key, Integer defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * Get Long
     */
    public static Long getLong(String key, Long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    /**
     * Get Float
     */
    public static Float getFloat(String key, Float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    /**
     * Get Boolean
     */
    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * Get all Data
     */
    public static Map<String, ?> getAllData() {
        Map<String, ?> map = preferences.getAll();
        return map;
    }

    /**
     * Is Open LOGS
     */
    public static boolean isOpenLogs(boolean isSet, boolean val) {
        final String preferKey = "com.lib.utils.IS_OPEN_LOGS";
        if (isSet) {
            saveBoolean(preferKey, val);
        }
        return getBoolean(preferKey, false);
    }
}
