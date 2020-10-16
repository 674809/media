package com.egar.usbmusic.utils;

import android.content.Context;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
//import android.os.storage.VolumeInfo;
import java.lang.reflect.Method;

public class UdiskUtil {
    private static String TAG = "UtilsUdisk";

    /**
     * 获取sd卡和U盘路径
     *
     * @return
     */
    public static List<String> getAllExterSdcardPath() {
        List<String> SdList = new ArrayList<String>();
        try {
            Runtime runtime = Runtime.getRuntime();
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
               // Log.d("", line);
                // 将常见的linux分区过滤掉
                // SdList.add(line);
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                // 下面这些分区是我们需要的
                if (line.contains("vfat") || line.contains("fuse")
                        || line.contains("fat") || (line.contains("ntfs"))) {
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                    String items[] = line.split(" ");
                    if (items != null && items.length > 1) {
                        String path = items[2].toLowerCase(Locale.getDefault());
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        if (path != null && !SdList.contains(path)
                                && path.contains("media_rw")) {
                          /*  if(path.contains("media_rw/sdcard")){
                                    continue;
                            }*/
                            SdList.add(items[2]);
                        }

                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return SdList;
    }

    public static boolean isHasSupperUDisk(Context context) {
        /*for (String list : getAllExterSdcardPath()){
            LogUtil.i(TAG,"udisk path ="+list);
        }*/

       return getStorageVolumesPath(context).size() > 0;
       // return isUdiskMount(context);
    }


    	  //获取U盘节点
   private static ArrayList<String> getStorageVolumesPath(Context context) {
        ArrayList<String> paths = new ArrayList<>();
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        //获取所有挂载的设备（内部sd卡、外部sd卡、挂载的U盘）
        List<StorageVolume> volumes = mStorageManager.getStorageVolumes();
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            //通过反射调用系统hide的方法
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            for (int i = 0; i < volumes.size(); i++) {
                StorageVolume storageVolume = volumes.get(i);//获取每个挂载的StorageVolume
                //通过反射调用getPath、isRemovable
                String storagePath = (String) getPath.invoke(storageVolume); //获取路径
                Log.d(TAG, "storagePath=" +storagePath);
                if ("/mnt/media_rw/sdcard".equals(storagePath)) { //排除内部存储节点，剩下的就是U盘节点
                    Log.d(TAG, "/storage/sdcard    continue");
                    continue;
                }else if("/storage/emulated/0".equals(storagePath)){
						Log.d(TAG, "/storage/emulated/0    continue");
                    continue;
                }else{
                	 boolean isRemovableResult = (boolean) isRemovable.invoke(storageVolume);//是否可移除
                    Log.d(TAG, "isRemovableResult=" +isRemovableResult);
                    paths.add(storagePath);
                }

            }
        } catch (Exception e) {
            Log.d("UtilsUdisk", " e:" + e);
        }
        return paths;
    }



 /*   private static boolean isUdiskMount(Context context) {
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        //获取所有挂载的设备（内部sd卡、外部sd卡、挂载的U盘）
        try {
            List<VolumeInfo> volumes = mStorageManager.getVolumes();
            Log.d(TAG,"volumes size ="+volumes.size());
            for(int i = 0; i<volumes.size(); i++){
                DiskInfo disk = volumes.get(i).getDisk();
                String path = volumes.get(i).path;
                Log.d(TAG,"path="+path);
                boolean ismounted = volumes.get(i).isMountedReadable();
                if(disk !=null){
                    Log.d(TAG,"disk="+disk);
                    if(disk.isUsb()){
                        Log.d(TAG,"isMountedReadable ="+ismounted);
                        return ismounted;
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, " e:" + e.toString());
        }

        return false;
    }*/




    /**
     * 判断文件是否存在
     */
    public static boolean isFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static boolean isExist() {
        List<String> list = getAllExterSdcardPath();
        boolean isexist = false;
        if (list.size() > 0) {
            isexist = isFile(list.get(0) + "/Android");
            Log.e(TAG, "Android is " + isexist);
            return isexist;
        }
        return false;

    }
}
