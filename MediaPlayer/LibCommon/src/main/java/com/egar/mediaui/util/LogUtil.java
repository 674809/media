package com.egar.mediaui.util;

import android.util.Log;

/**
 * @des: Created by ybf
 * @version: 3.3.2
 * @date: 2019/9/23 09:06
 * @see {@link }
 */
public class LogUtil {
    public static final String TAG = "MediaPlayerUI";
    public static final int LOG_LEVEL_NONE = 0;     //不输出任和log
    public static final int LOG_LEVEL_DEBUG = 1;    //调试 蓝色
    public static final int LOG_LEVEL_INFO = 2;     //提现 绿色
    public static final int LOG_LEVEL_WARN = 3;     //警告 橙色
    public static final int LOG_LEVEL_ERROR = 4;    //错误 红色
    public static final int LOG_LEVEL_ALL = 5;      //输出所有等级


    /**
     * 允许输出的log日志等级
     * 当出正式版时,把mLogLevel的值改为 LOG_LEVEL_NONE,
     * 就不会输出任何的Log日志了.
     */
    private static int mLogLevel = LOG_LEVEL_ALL;

    /**
     * 获取Log等级
     *
     * @return
     */
    public static int getLogLevel() {
        return mLogLevel;
    }

    /**
     * 给输出的Log等级赋值
     *
     * @param level
     */
    public static void setLogLevel(int level) {
        LogUtil.mLogLevel = level;
    }

    /**
     * 以级别为 d 的形式输出LOG,输出debug调试信息
     */


    public static void d(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_DEBUG) {
            Log.d(tag, getMsgFormat(msg));
        }
    }
    public static void d(String msg) {
        if (getLogLevel() >= LOG_LEVEL_DEBUG) {
            Log.d(TAG, getMsgFormat(msg));
        }
    }

    /**
     * 以级别为 i 的形式输出LOG,一般提示性的消息information
     */
    public static void i(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_INFO) {
            Log.i(tag, getMsgFormat(msg));
        }
    }
    public static void i( String msg) {
        if (getLogLevel() >= LOG_LEVEL_INFO) {
            Log.d(TAG, getMsgFormat(msg));
        }
    }
    /**
     * 以级别为 w 的形式输出LOG,显示warning警告，一般是需要我们注意优化Android代码
     */
    public static void w(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_WARN) {
            Log.w(tag, getMsgFormat(msg));
        }
    }
    public static void w( String msg) {
        if (getLogLevel() >= LOG_LEVEL_WARN) {
            Log.d(TAG, getMsgFormat(msg));
        }
    }
    /**
     * 以级别为 e 的形式输出LOG ，红色的错误信息，查看错误源的关键
     */
    public static void e(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_ERROR) {
            Log.e(tag, getMsgFormat(msg));
        }
    }
    public static void e( String msg) {
        if (getLogLevel() >= LOG_LEVEL_ERROR) {
            Log.d(TAG, getMsgFormat(msg));
        }
    }
    /**
     * 以级别为 v 的形式输出LOG ，verbose啰嗦的意思
     */
    public static void v(String tag, String msg) {
        if (getLogLevel() >= LOG_LEVEL_ALL) {
            Log.v(tag, getMsgFormat(msg));
        }
    }

    public static void v( String msg) {
        if (getLogLevel() >= LOG_LEVEL_ALL) {
            Log.d(TAG, getMsgFormat(msg));
        }
    }


    public static String getStackTrace() { //limit =10
        final int limit = 10;
        final StringBuilder sb = new StringBuilder();
        try {
            throw new RuntimeException();
        } catch (final RuntimeException e) {
            final StackTraceElement[] frames = e.getStackTrace();
            // Start at 1 because the first frame is here and we don't care about it
            for (int j = 1; j < frames.length && j < limit + 1; ++j) {
                sb.append(frames[j].toString() + "\n");
            }
        }
        return sb.toString();
    }

    /**
     * 获取相关数据:类名,方法名,行号等.用来定位行<br>
     * at cn.utils.MainActivity.onCreate(MainActivity.java:17) 就是用來定位行的代碼<br>
     *
     * @return [ Thread:main, at
     * cn.utils.MainActivity.onCreate(MainActivity.java:17)]
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts != null) {
            for (StackTraceElement st : sts) {
                if (st.isNativeMethod()) {
                    continue;
                }
                if (st.getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(LogUtil.class.getName())) {
                    continue;
                }
              //  return "[ Thread:" + Thread.currentThread().getName() + ", at " + st.getClassName() + "." + st.getMethodName()
               //        + "(" + st.getFileName() + ":" + st.getLineNumber() + ")" + " ]";
             return "[" + st.getFileName() + ":" + st.getLineNumber() + "]";
            }
        }
        return null;
    }

    /**
     * 输出格式定义
     */
    private static String getMsgFormat(String msg) {
        return msg + ";" + getFunctionName();
    }

    /**
     *
     * @param limit
     * @return
     */
    public static  String getStackTraceLog() {  //limit =10
        final StringBuilder sb = new StringBuilder();
        try {
            throw new RuntimeException();
        } catch (final RuntimeException e) {
            final StackTraceElement[] frames = e.getStackTrace();
            // Start at 1 because the first frame is here and we don't care about it
            for (int j = 1; j < frames.length && j < 10 + 1; ++j) {
                sb.append(frames[j].toString() + "\n");
            }
        }
        return sb.toString();
    }
}
