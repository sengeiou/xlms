package com.gdswlw.library.toolkit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import java.lang.reflect.Method;

/**
 * Created by Afun on 2019/9/10.
 */

public class NavigationBarInfo {
    private static final String TAG = "NavigationBarInfo";

    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else {
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     */
    public static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        return sNavBarOverride;
    }

    public static void adaptiveStartPage(Activity activity, ImageView imageView, boolean bottom) {
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        float height = dm.heightPixels + NavigationBarInfo.getNavigationBarHeight(activity);
//        float height = dm.heightPixels ;
        float width = dm.widthPixels;

        float standard = (float) (1920.0 / 1080.0);
        float actual = height / width;
        Log.d(TAG, "heigth =" +height);
        Log.d(TAG, "standard = " + standard);
        Log.d(TAG, "actual = " + actual);
        Log.d(TAG, "actual/standard = " + actual / standard);
        Log.d(TAG, "standard/actual = " + standard / actual);
        Log.d(TAG, "getNavigationBarHeight = " + NavigationBarInfo.getNavigationBarHeight(activity));
        Log.d(TAG, "hasNavBar = " + NavigationBarInfo.hasNavBar(activity));
        //以宽为参考
        if (actual > standard) {
            imageView.setScaleY(actual / standard);
            imageView.setScaleX(actual / standard);
        } else if (actual < standard) {
            imageView.setScaleY(standard / actual);
            imageView.setScaleX(standard / actual);
        }
    }

    public static void hideNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
