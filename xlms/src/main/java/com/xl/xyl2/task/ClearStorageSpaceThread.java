package com.xl.xyl2.task;

/**
 * Created by Afun on 2019/10/30.
 */

import com.gdswlw.library.toolkit.StatUtils;
import com.xl.xyl2.utils.FileUtils;

/**
 * 清除存储空间线程
 */
public class ClearStorageSpaceThread implements Runnable{
    public boolean isRun = true;
    @Override
    public void run() {
        while (isRun){
            int percent = StatUtils.getExternalStorageUsePercent();
            if (percent <= 20) {//当剩余存储空间低于总存储的20%，清理未使用的资源文件
                FileUtils.clearDownloadUnPlay();//清除不在播放列表的文件
            }
            try {
                Thread.sleep(1000 * 60 * 2);//每2分钟检测一次
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
