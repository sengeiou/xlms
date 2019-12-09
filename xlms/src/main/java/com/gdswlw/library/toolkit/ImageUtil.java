package com.gdswlw.library.toolkit;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Afun on 2019/9/10.
 */

public class ImageUtil {
    private static final String IMAGE_FILE_NAME_TEMPLATE = "Image%s.jpg";
    private static final String IMAGE_FILE_PATH_TEMPLATE = "%s/%s";
    /**
     * 屏幕截图
     * @param activity
     * @return
     */
    public static Bitmap screenShot(AppCompatActivity activity, String filePath, @Nullable final ShotCallback shotCallback) {
        if (activity == null){
            return null;
        }
        View view = activity.getWindow().getDecorView();
        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        int navigationBarHeight = NavigationBarInfo.getNavigationBarHeight(view.getContext());


        //获取屏幕宽和高
        int width = ScreenUtil.getScreenWidth(view.getContext());
        int height = ScreenUtil.getScreenHeight(view.getContext());

        // 全屏不用考虑状态栏，有导航栏需要加上导航栏高度
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                    height + navigationBarHeight);
        } catch (Exception e) {
            // 这里主要是为了兼容异形屏做的处理，我这里的处理比较仓促，直接靠捕获异常处理
            // 其实vivo oppo等这些异形屏手机官网都有判断方法
            // 正确的做法应该是判断当前手机是否是异形屏，如果是就用下面的代码创建bitmap
            String msg = e.getMessage();
            // 部分手机导航栏高度不占窗口高度，不用添加，比如OppoR15这种异形屏
            if (msg.contains("<= bitmap.height()")){
                try {
                    bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                            height);
                } catch (Exception e1) {
                    msg = e1.getMessage();
                    // 适配Vivo X21异形屏，状态栏和导航栏都没有填充
                    if (msg.contains("<= bitmap.height()")) {
                        try {
                            bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                                    height - ScreenUtil.getStatusHeight(view.getContext()));
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }else {
                        e1.printStackTrace();
                    }
                }
            }else {
                e.printStackTrace();
            }
        }

        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        if (null != bitmap){
            try {
                compressAndGenImage(bitmap,filePath);
                Log.d("shot---","截图保存地址：" + filePath);
                if (null != shotCallback){
                    shotCallback.onShotComplete(bitmap,filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }



    public interface ShotCallback{
        void onShotComplete(Bitmap bitmap, String savePath);
    }



    /**
     * 存储到sdcard
     *
     * @param bmp
     * @param maxSize 为0不压缩
     * @return
     */
    public static String saveToSD(Bitmap bmp,int maxSize) {
        if (bmp == null){
            UIKit.eLog("saveToSD--->bmp is null");
            return "";
        }
        //判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //文件名
            long systemTime = System.currentTimeMillis();
            String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(systemTime));
            String mFileName = String.format(IMAGE_FILE_NAME_TEMPLATE, imageDate);

            //文件全名
            String mstrRootPath = Environment.getExternalStorageDirectory().getPath();
            String filePath = String.format(IMAGE_FILE_PATH_TEMPLATE, mstrRootPath, mFileName);

            UIKit.dLog("saveToSD--->file path:" + filePath);
            File file = new File(filePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            UIKit.dLog("saveToSD--->file AbsolutePath:" + filePath);
            try {
                compressAndGenImage(bmp,filePath,maxSize);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                bmp.recycle();
            }

            return filePath;
        }
        return "";
    }

    public static String createImagePath(){
        //判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //文件名
            long systemTime = System.currentTimeMillis();
            String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(systemTime));
            String mFileName = String.format(IMAGE_FILE_NAME_TEMPLATE, imageDate);

            //文件全名
            String mstrRootPath = Environment.getExternalStorageDirectory().getPath();//sdcard
            String filePath = String.format(IMAGE_FILE_PATH_TEMPLATE, mstrRootPath, mFileName);
            File file = new File(filePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return filePath;
        }
        return "";
    }

    public static void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 100;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        if (maxSize != 0) {
            while (os.toByteArray().length / 1024 > maxSize) {
                // Clean up os
                os.reset();
                // interval 10
                options -= 10;
                image.compress(Bitmap.CompressFormat.JPEG, options, os);
            }
        }

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        UIKit.dLog("compressAndGenImage--->文件大小：" + os.size()+"，压缩比例：" + options);
        fos.flush();
        fos.close();
    }

    public static void compressAndGenImage(Bitmap image, String outPath) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 70;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        UIKit.dLog("compressAndGenImage--->文件大小：" + os.size()+"，压缩比例：" + options);
        fos.flush();
        fos.close();
    }
}
