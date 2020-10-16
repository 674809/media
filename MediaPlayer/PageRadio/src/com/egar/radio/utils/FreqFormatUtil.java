package com.egar.radio.utils;



import com.egar.manager.radio.RadioManager;

import java.util.Locale;

/**
 * Radio frequency format methods.
 *
 * @author Jun.Wang
 */
public class FreqFormatUtil {
    public static String getFreqStr(int band, int freq) {
        return (band == RadioManager.RADIO_BAND_FM) ? getFmFreqStr(freq) : getAmFreqStr(freq);
    }

    public static String getFmFreqStr(int freq) {
        return String.format(Locale.getDefault(), "%1$.1f", (freq / 1000d));
    }

    public static String getAmFreqStr(int freq) {
        return String.valueOf(freq);
    }
}
