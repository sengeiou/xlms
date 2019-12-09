package com.gdswlw.library.toolkit;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

/**
 * Created by Afun on 2019/9/21.
 */

public class StatUtils {
    /**
     * 获取总的存储空间大小
     * @param path
     * @return
     */
    public static long totalMemory(String path) {
        StatFs statFs = new StatFs(path);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //noinspection deprecation
            return (statFs.getBlockCount() * statFs.getBlockSize());
        } else {
            return (statFs.getBlockCountLong() * statFs.getBlockSizeLong());
        }
    }

    /**
     * 获取剩余的存储空间大小
     * @param path
     * @return
     */
    public static long freeMemory(String path) {
        StatFs statFs = new StatFs(path);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //noinspection deprecation
            return (statFs.getAvailableBlocks() * statFs.getBlockSize());
        } else {
            return (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
        }
    }

    /**
     * 获取已使用的存储大小
     * @param path
     * @return
     */
    public static long usedMemory(String path) {
        long total = totalMemory(path);
        long free = freeMemory(path);
        return total - free;
    }


    /**
     * 获取外部存设备使用百分比
     * @return
     */
    public static int  getExternalStorageUsePercent(){
        long total = totalMemory(Environment.getExternalStorageDirectory().getPath());
        long free = freeMemory(Environment.getExternalStorageDirectory().getPath());
        int percent =  (int)(((double) free / (double) total) * 100);
        return percent;
    }

}
