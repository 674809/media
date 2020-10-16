package com.egar.usbimage.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;
import java.util.Set;

/**
 * Common PreferenceHelper
 * <p>First, u must call {@link #init(Context)}</p>
 *
 * @author Jun.Wang
 */
public class SpUtils {

    /**
     * {@link SharedPreferences} Object
     */
    private SharedPreferences mSharedPreferences;

    private SpUtils() {
    }

    private static class SingletonHolder {
        private static final SpUtils INSTANCE = new SpUtils();
    }

    public static SpUtils instance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Initialize On Application Start
     */
    public void init(Context context) {
        if (mSharedPreferences == null && context != null) {
            Context appCxt = context.getApplicationContext();
            if (mSharedPreferences == null) {
                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCxt);
            }
        }
    }

    /**
     * Save String
     */
    public void saveString(String key, String value) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putString(key, value).apply();
        }
    }

    /**
     * Save Set<String>
     */
    public void saveStringSet(String key, Set<String> value) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putStringSet(key, value).apply();
        }
    }

    /**
     * Save Integer
     */
    public void saveInt(String key, Integer value) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putInt(key, value).apply();
        }
    }

    /**
     * Save Long
     */
    public void saveLong(String key, Long value) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putLong(key, value).apply();
        }
    }

    /**
     * Save Float
     */
    public void saveFloat(String key, Float value) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putFloat(key, value).apply();
        }
    }

    /**
     * Save Boolean
     */
    public void saveBoolean(String key, Boolean value) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putBoolean(key, value).apply();
        }
    }

    /**
     * Delete By Key
     */
    public void delete(String key) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().remove(key).apply();
        }
    }

    /**
     * Clear all data
     */
    public void clearAll() {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().clear().apply();
        }
    }

    /**
     * Get String
     */
    public String getString(String key, String defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getString(key, defaultValue);
        }
        return null;
    }

    /**
     * Get Set<String>
     */
    @SuppressLint("NewApi")
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getStringSet(key, defaultValue);
        }
        return null;
    }

    /**
     * Get Integer
     */
    public Integer getInt(String key, Integer defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getInt(key, defaultValue);
        }
        return null;
    }

    /**
     * Get Long
     */
    public Long getLong(String key, Long defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getLong(key, defaultValue);
        }
        return null;
    }

    /**
     * Get Float
     */
    public Float getFloat(String key, Float defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getFloat(key, defaultValue);
        }
        return null;
    }

    /**
     * Get Boolean
     */
    public Boolean getBoolean(String key, Boolean defaultValue) {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getBoolean(key, defaultValue);
        }
        return null;
    }

    /**
     * Get all Data
     */
    public Map<String, ?> getAllData() {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getAll();
        }
        return null;
    }

    /**
     * Is LOG allowed to print?
     *
     * @param isSet true: val will be stored to {@link SharedPreferences}
     * @param val   Value to store.
     * @return boolean : Latest result.
     */
    public boolean isOpenLogs(boolean isSet, boolean val) {
        final String preferKey = "juns.lib.android.utils.IS_OPEN_LOGS";
        if (isSet) {
            saveBoolean(preferKey, val);
        }
        return getBoolean(preferKey, false);
    }
}
