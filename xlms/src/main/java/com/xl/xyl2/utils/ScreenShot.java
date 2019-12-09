package com.xl.xyl2.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.gdswlw.library.toolkit.ScreenUtil;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

public class ScreenShot {

    private static float getDegreesForRotation(Display mDisplay) {
        int rotation = mDisplay.getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0f;
            case Surface.ROTATION_90:
                return 360f - 90f;
            case Surface.ROTATION_180:
                return 360f - 180f;
            case Surface.ROTATION_270:
                return 360f - 270f;
        }
        return 0f;
    }

    private static byte[] ByteBitmap(Bitmap b) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        return baos.toByteArray();
    }

    public interface ShotCallback{
        void onSucess(Bitmap bitmap);
        void onFailed(String message);
    }

    public static void screenshot(Context context,ShotCallback shotCallback,float width,float height,float x,float y) {
        Matrix mDisplayMatrix = new Matrix();
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display mDisplay = mWindowManager.getDefaultDisplay();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        mDisplay.getRealMetrics(mDisplayMetrics);
        float[] dims = {mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels};
        //float[] dims = {width, height};
        float degrees = getDegreesForRotation(mDisplay);
        boolean requiresRotation = (degrees > 0);

        if (requiresRotation) {
            // Get the dimensions of the device in its native orientation
            mDisplayMatrix.reset();
            mDisplayMatrix.preRotate(-degrees);
            mDisplayMatrix.mapPoints(dims);
            dims[0] = Math.abs(dims[0]);
            dims[1] = Math.abs(dims[1]);
        }
        Bitmap mScreenBitmap = null;
        Class<?> demo = null;
        try {
            demo = Class.forName("android.view.SurfaceControl");
            Method method = demo.getMethod("screenshot", new Class[]{int.class, int.class});
            method.setAccessible(true);
            mScreenBitmap = (Bitmap) method.invoke(null, (int) dims[0], (int) dims[1]);
        } catch (Exception e) {
            e.printStackTrace();
            if(shotCallback != null){
                shotCallback.onFailed(e.getMessage());
            }
            return;
        }
        if (requiresRotation) {
            // Rotate the screenshot to the current orientation
            Bitmap ss = Bitmap.createBitmap((int) width,
                    (int) height, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(ss);
            c.translate(ss.getWidth() / 2, ss.getHeight() / 2);
            c.rotate(degrees);
            c.translate(-dims[0] / 2, -dims[1] / 2);
            c.drawBitmap(mScreenBitmap, 0, 0, null);
            c.setBitmap(null);
            mScreenBitmap = ss;
        }
        int bitmapWidth = mScreenBitmap.getWidth();
        int bitmapHeight= mScreenBitmap.getHeight();
        //判断范围是否超出 x ,y 减至最大宽高
        if(width > mScreenBitmap.getWidth() ){
            width = mScreenBitmap.getWidth();
        }

        if(height > mScreenBitmap.getHeight()){
            height = mScreenBitmap.getHeight();
        }

        if(mScreenBitmap.getWidth() - (x+width) < 0){
            x += (mScreenBitmap.getWidth() - (x+width));

        }

        if(mScreenBitmap.getHeight() - (y+height) < 0){
            y += (mScreenBitmap.getHeight() - (y+height));
        }
        if(x < 0){
            x = 0;
        }

        if(y < 0){
            y = 0;
        }

        mScreenBitmap = Bitmap.createBitmap(mScreenBitmap,(int)x,(int) y,(int)width,(int)height);
        // Optimizations
        mScreenBitmap.setHasAlpha(true);
        mScreenBitmap.prepareToDraw();
        if(shotCallback != null){
            shotCallback.onSucess(mScreenBitmap);
        }
    }
}