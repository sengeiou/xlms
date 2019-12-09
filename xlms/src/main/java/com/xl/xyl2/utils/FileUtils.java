package com.xl.xyl2.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import com.gdswlw.library.toolkit.FileUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.play.PlayUnit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();
    public static final String DATA_NAME="DATA";

    public static void printLog(String txt) {
        printLog(txt, true);
    }

    public static void delFile(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory() + "/" + fileName;
            File file = new File(path);
            if (file != null) {
                if (file.exists() && file.isFile()) {
                    LogUtils.e(TAG, "删除文件：" + fileName);
                    file.delete();
                }
            }
        }
    }

    public static void printLog(String txt, boolean append) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory() + "/xyl2/Log.txt";
            FileOutputStream fos2 = null;// 第二個参数为true表示程序每次运行都是追加字符串在原有的字符上
            try {
                fos2 = new FileOutputStream(path, append);
                if (fos2 != null) {
                    fos2.write((DateUtils.formatTime(new Date()) + ":" + txt).getBytes());
                    fos2.write("\r\n".getBytes());// 写入一个换行
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos2 != null) {
                    try {
                        fos2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 保存图片到SD卡
     *
     * @param bitmap
     * @param name
     */
    public static void saveBitmap(Bitmap bitmap, String name) {
        LogUtils.e(TAG, "bitmap:" + bitmap.getByteCount());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory() + "/xyl2/";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                File picture = new File(path + name + ".jpg");
                FileOutputStream fos = new FileOutputStream(picture.getPath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                LogUtils.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    //判断是否安装SDCard
    public static boolean isSdOk(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
    //创建一个文件夹，用来存放下载的文件
    public static File getRootFile(){
        File sd = Environment.getExternalStorageDirectory();
        File rootFile = new File(sd,"XYLApp");
        if (!rootFile.exists()){
            rootFile.mkdirs();
        }
        return rootFile;
    }


    /**
     * 获取数据目录
     * @return
     */
    public static File getDataDir(){
        return new File(getRootFile().getPath()+"/Data/");
    }


    /**
     * 获取播放日志目录
     * @return
     */
    public static File getLogDir(){
        return getDir("/Logs/");
    }
    /**
     * 获取异常日志目录
     * @return
     */
    public static File getCrashLogDir(){
        return getDir("/CrashLogs/");
    }
    public static File getDownloadDir(){
        return getDir("/source/");
    }

    public static File getTempDir(){
        return getDir("/temp/");
    }

    private static File getDir(String dir){
        File file =  new File(getRootFile().getPath()+dir);
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }


    public static void clearDownloadUnPlay(){
        final HashMap<String, PlayUnit> downloadList  = XLContext.getDownloadList();
        File[] files = FileUtil.listDirectoryFiles(FileUtils.getDownloadDir().getPath());
        for (int i = 0; i < files.length; i++) {
            File temp = files[i];
            if (temp.isDirectory()) {
                continue;
            }
            //如果不存在播放列表中，则删除文件
            if (!downloadList.containsKey(temp.getName())) {
                temp.delete();
                UIKit.eLog("delete:"+temp.getName());
            }
        }
    }
}

