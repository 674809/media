package com.egar.usbimage.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 常用共同方法类，不涉及逻辑
 *
 * @author Jun.Wang
 */
public class UsbImageUtils {
    private static final String TAG = "UsbUtils";

    /**
     * 显示软键盘
     */
    public static void showSoftKeyBoard(Context cxt) {
        InputMethodManager imm = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 关闭软键盘
     */
    public static void hideSoftKeyBoard(Context cxt, View focusV) {
        if (focusV != null) {
            InputMethodManager imm = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusV.getWindowToken(), 0);
        }
    }

    /**
     * px -> dp
     */
    public static int px2dip(Context cxt, float pxValue) {
        final float scale = cxt.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp -> px
     */
    public static int dip2px(Context cxt, float dipValue) {
        final float scale = cxt.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px -> sp
     */
    public static int px2sp(Context cxt, float pxValue) {
        final float fontScale = cxt.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp -> px
     */
    public static int sp2px(Context cxt, float spValue) {
        final float fontScale = cxt.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取UUID
     *
     * @param context {@link Context}
     * @return MAP，key 存储设备路径，value 存储设备UUID
     */
    public static Map<String, String> getStorageUUID(Context context) {
        Map<String, String> mapUUIDs = new HashMap<>();
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<StorageVolume> listStorageVolumes = storageManager.getStorageVolumes();
                for (StorageVolume sv : listStorageVolumes) {
                    Class cls = sv.getClass();
                    @SuppressWarnings("unchecked")
                    Method method = cls.getDeclaredMethod("getPath");
                    Object obj = method.invoke(sv);
                    if (obj instanceof String) {
                        String rootPath = (String) obj;
                        String uuid = sv.getUuid();
                        mapUUIDs.put(rootPath, uuid == null ? "inner" : uuid);
                    }
                }
            }
        } catch (Exception e) {
            Log.i("", "");
        }
        return mapUUIDs;
    }

    /**
     * 移动设备信息
     * <p>Permission require "android.permission.READ_PHONE_STATE"</p>
     *
     * @return String[]
     * <p>[0] 移动设备国际识别码,是手机的唯一识别号码</p>
     * <p>[1] "Android"</p>
     * <p>[2] SDK Version</p>
     * <p>[3] MODEL</p>
     */
    public static String[] getTelIMEI(Context cxt) {
        TelephonyManager telManager = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);

        // 移动设备国际识别码,是手机的唯一识别号码
        String[] telInfo = new String[4];
        if (telManager != null) {
            telInfo[0] = telManager.getDeviceId();
            telInfo[1] = "Android";
            telInfo[2] = android.os.Build.VERSION.RELEASE;
            telInfo[3] = android.os.Build.MODEL;
        }

        return telInfo;
    }

    /**
     * 拨打电话
     * <p>permissions required by intent Intent.ACTION_CALL: android.permission.CALL_PHONE</p>
     *
     * @param telNum     : 电话号码
     * @param isJustDial ： 是否直接拨号
     */
    public static void dialTel(Context cxt, String telNum, boolean isJustDial) {
        // 直接拨打电话
        if (isJustDial) {
            Intent justDialIntent = new Intent(Intent.ACTION_CALL);
            justDialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri data = Uri.parse("tel:" + telNum);
            justDialIntent.setData(data);
            cxt.startActivity(justDialIntent);

            // 进入拨号页面，由用户决定是否真正拨打电话
        } else {
            Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNum));
            dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cxt.startActivity(dialIntent);
        }
    }

    /**
     * 发送邮件
     */
    public static void sendEmail(Context cxt, String emailAddr) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setData(Uri.parse("mailto:" + emailAddr));
        // emailIntent.putExtra(Intent.EXTRA_SUBJECT, "这是标题");
        // emailIntent.putExtra(Intent.EXTRA_TEXT, "这是内容");
        cxt.startActivity(emailIntent);
    }

    /**
     * 发送广播
     * <p>该方法解决发送广播给静态Receiver，如果应用从未启动而收不到广播的问题</p>
     *
     * @param cxt  :{@link Context}
     * @param data : {@link Intent}
     */
    @SuppressLint("ObsoleteSdkInt")
    public static void sendBroadcast(Context cxt, Intent data) {
        data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1) {
            data.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        }
        cxt.sendBroadcast(data);
    }

    /**
     * 绑定服务
     *
     * @param context    {@link Context}
     * @param pkg        e.g. "com.android.music"
     * @param sName      e.g. "com.android.music.MediaPlaybackService"
     * @param connection {@link ServiceConnection}
     */
    public static void bindService(Context context, String pkg, String sName, ServiceConnection connection) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName(pkg, sName));
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            Log.i(TAG, "bindService - e:" + e.getMessage());
        }
    }

    /**
     * Set view Enable and alpha
     */
    public static void setViewEnable(View v, boolean isEnable) {
        //
        v.setEnabled(isEnable);

        //
        if (isEnable) {
            v.setAlpha(1f);
        } else {
            v.setAlpha(0.5f);
        }
    }

    /**
     * 获取指定范围内的随机数
     *
     * <p>Example : getRandomNum(1,10),结果为不等于1,且0～10之间的随机数</p>
     *
     * @param oldNum 当前已经出现的旧数据,即新的随机数不应当等于此数值
     * @param bound  范围
     * @return int 随机数
     */
    public static int getRandomNum(int oldNum, int bound) {
        int randomNum;
        if (bound == 1) {
            randomNum = 0;
        } else {
            Random random = new Random();
            while (true) {
                int tmpNum = random.nextInt(bound);
                if (oldNum != tmpNum) {
                    randomNum = tmpNum;
                    break;
                }
            }
        }
        return randomNum;
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图
     *
     * <p>此方法有两点好处:</p>
     * <p>1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。</p>
     * <p>2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     * 用这个工具生成的图像不会被拉伸。</p>
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // 获取这个图片的宽和高，注意此处的bitmap为null
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            options.inJustDecodeBounds = false; // 设为 false

            // 计算缩放比
            int h = options.outHeight;
            int w = options.outWidth;
            int beWidth = w / width;
            int beHeight = h / height;

            int be = 1;
            if (beWidth < beHeight) {
                be = beWidth;
            } else {
                be = beHeight;
            }
            if (be <= 0) {
                be = 1;
            }

            options.inSampleSize = be;
            // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (Exception e) {
            Log.i(TAG, "getImageThumbnail - e:" + e.getMessage());
        }

        return bitmap;
    }

    /**
     * 获取视频的缩略图
     * <p>先通过ThumbnailUtils来创建一个视频的缩略图;</p>
     * <p>然后再利用ThumbnailUtils来生成指定大小的缩略图.</p>
     * <p>如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存.</p>
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 其中，MINI_KIND: 512 x
     *                  384，MICRO_KIND: 96 x 96
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        try {
            // 获取视频的缩略图
            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (Exception e) {
            Log.i(TAG, "getVideoThumbnail - e:" + e.getMessage());
        }
        return bitmap;
    }

    /**
     * Get Video Resolution
     *
     * @return int[2] : [0] width, [1]height
     */
    public static int[] getVideoResolutions(Context cxt, String mediaUrl) {
        int w = 0, h = 0;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(cxt, Uri.parse(mediaUrl));
            Bitmap bm = mmr.getFrameAtTime();
            if (bm != null) {
                w = bm.getWidth();
                h = bm.getHeight();
            }
        } catch (Exception e) {
            Log.i(TAG, "getVideoResolutions - e:" + e.getMessage());
        }
        return new int[]{w, h};
    }

    /**
     * Cancel AsyncTask
     */
    public static <Params, Progress, Result> void cancelTask(AsyncTask<?, ?, ?> task) {
        if (task != null) {
            // if (task.getStatus() != AsyncTask.Status.FINISHED) {
            // task.cancel(true);
            // }
            task.cancel(true);
            task = null;
        }
    }

    /**
     * Open Other APK
     *
     * @param pkg : Like "com.xxx.package"
     * @param cls : Like "com.xxx.package.ApkActivity"
     */
    public static void openApk(Context cxt, String pkg, String cls) {
        ComponentName comp = new ComponentName(pkg, cls);
        Intent gmIntent = new Intent();
        gmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        gmIntent.setComponent(comp);
        gmIntent.setAction("android.intent.action.VIEW");
        cxt.startActivity(gmIntent);
    }

    /**
     * @return String[] : [0] 应用名称 ; [1] 得到安装包名称 ; [2] 得到版本信息 ; [3]版本号
     */
    public static String[] getApkInfo(Context cxt, String apkFilePath) {
        String[] strApkInfos = new String[4];
        PackageManager pm = cxt.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            // 应用名称
            strApkInfos[0] = pm.getApplicationLabel(appInfo).toString();
            // 得到安装包
            strApkInfos[1] = appInfo.packageName;
            // 得到版本信息
            strApkInfos[2] = info.versionName;
            // 版本号
            strApkInfos[3] = String.valueOf(info.versionCode);
        }
        return strApkInfos;
    }

    /**
     * Get Installed APK Information
     *
     * @return String[] : [0] Is Install,"true" or "false";
     * <p>[1] 应用名称 ;</p>
     * <p>[2] 得到安装包名称 ;</p>
     * <p>[3] 得到版本信息 ;</p>
     * <p>[4] 版本号</p>
     */
    public static String[] getApkInstalledInfo(Context cxt, String pkg) {
        String[] strApkInfos = new String[5];

        try {
            PackageManager pm = cxt.getPackageManager();
            PackageInfo info = pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
            strApkInfos[0] = "true";
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                // 应用名称
                strApkInfos[1] = pm.getApplicationLabel(appInfo).toString();
                // 得到安装包
                strApkInfos[2] = appInfo.packageName;
                // 得到版本信息
                strApkInfos[3] = info.versionName;
                // 版本号
                strApkInfos[4] = String.valueOf(info.versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.i(TAG, "getApkInstalledInfo - e:" + e.getMessage());
            strApkInfos[0] = "false";
        }
        return strApkInfos;
    }

    /**
     * Is Application Running Foreground
     */
    @SuppressWarnings("deprecation")
    public static boolean isRunningBackground(Context context, String targetPackageName) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName topActivity = am.getRunningTasks(1).get(0).topActivity;
            String topPkgName = topActivity.getPackageName();
            if (TextUtils.isEmpty(topPkgName)) {
                return true;
            } else {
                return !topPkgName.equals(targetPackageName);
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Cancel Timer
     */
    public static void cancelTimer(Timer timer) {
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            Log.i(TAG, "cancelTimer - e:" + e.getMessage());
        }
    }

    /**
     * Cancel Timer Task
     */
    public static void cancelTimerTask(TimerTask timerTask) {
        try {
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
        } catch (Exception e) {
            Log.i(TAG, "cancelTimerTask - e:" + e.getMessage());
        }
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] hexStr2ByteArray(String hexString) {
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            // 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            // 将hex 转换成byte "&" 操作为了防止负数的自动扩展
            // hex转换成byte 其实只占用了4位，然后把高位进行右移四位
            // 然后“|”操作 低四位 就能得到 两个 16进制数转换成一个byte.
            //
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    /**
     * Get Formated MAC String
     *
     * @param strMacNum : Format Must Be "223456789910"
     * @return like "22:34:56:78:99:10"
     */
    public static String getMacFromMacStr(String strMacNum, boolean isUpperCase) {
        //
        if (TextUtils.isEmpty(strMacNum) || strMacNum.length() != 12) {
            return "";
        }

        //
        if (isUpperCase) {
            strMacNum = strMacNum.toUpperCase();
        } else {
            strMacNum = strMacNum.toLowerCase();
        }

        //
        StringBuffer strFormatMAC = new StringBuffer();
        strFormatMAC.append(strMacNum.substring(0, 2) + ":");
        strFormatMAC.append(strMacNum.substring(2, 4) + ":");
        strFormatMAC.append(strMacNum.substring(4, 6) + ":");
        strFormatMAC.append(strMacNum.substring(6, 8) + ":");
        strFormatMAC.append(strMacNum.substring(8, 10) + ":");
        strFormatMAC.append(strMacNum.substring(10));

        return strFormatMAC.toString();
    }

    /**
     * Cover Byte to Integer
     */
    public static int byteToInt(byte b) {
        return b & 0xFF;
    }

    /**
     * Print HEX String
     *
     * @param b : byte[]
     */
    public static void printHexString(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            Log.i(TAG, "i=" + i + "; b[i]=" + hex);
        }
    }

    /**
     * 根据图片资源名称获取资源ID
     */
    public static int getResID(Context cxt, String resName) {
        return cxt.getResources().getIdentifier(resName, "drawable", cxt.getPackageName());
    }

    /**
     * 设置状态栏显示及隐藏
     * <p>View.SYSTEM_UI_FLAG_HIDE_NAVIGATION, //隐藏导航栏</p>
     * <p>View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION，</p>
     * <p>View.SYSTEM_UI_LAYOUT_FLAGS，</p>
     * <p>View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, //全屏，状态栏会盖在布局上</p>
     * <p>View.SYSTEM_UI_FLAG_FULLSCREEN, //全屏，状态栏和导航栏不显示</p>
     * <p>View.SYSTEM_UI_FLAG_VISIBLE, //显示状态栏和导航栏</p>
     * <p>View.SYSTEM_UI_FLAG_LAYOUT_STABLE,// 保持View Layout不变，隐藏状态栏或者导航栏后，View不会拉伸。</p>
     * <p>View.SYSTEM_UI_FLAG_LOW_PROFILE,//状态栏:低能显示状态，状态栏上一些图标显示会被隐藏。</p>
     * <p>
     * View.SYSTEM_UI_FLAG_IMMERSIVE
     * //只有当设置了 SYSTEM_UI_FLAG_HIDE_NAVIGATION 才起作用。
     * 如果没有设置，任意的View相互动作都退出SYSTEM_UI_FLAG_HIDE_NAVIGATION模式。如果设置就不会退出。
     * </p>
     * <p>
     * View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
     * //只有当设置了 SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION 时起作用。
     * 如果没有设置，任意的View相互动作都退出SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION模式。
     * </p>
     *
     * @param activity {@link Activity}
     * @param visible  0 hide; 1 show.
     */
    public static void setNavigationBar(Activity activity, int visible) {
        try {
            View decorView = activity.getWindow().getDecorView();
            switch (visible) {
                case 0:
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    break;
                case 1:
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
