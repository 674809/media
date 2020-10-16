package com.egar.radio.utils;

import android.util.Log;

public class LogUtil {
    private static final String TAG = "RadioUI_";
    public static boolean  debug = false;


    public static void d(String tag, String message){
        if(debug){
            Log.d(TAG+tag,message);
        }
    }

    public static void i(String tag, String message){
           Log.i(TAG+tag,message);
    }

    public static void v(String tag, String message){
        if(debug){
            Log.v(TAG+tag,message);
        }
    }

    public static void e(String tag, String message){
            Log.e(TAG+tag,message);
    }
}
