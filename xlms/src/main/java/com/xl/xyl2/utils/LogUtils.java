package com.xl.xyl2.utils;

import android.util.Log;

public class LogUtils {
    //规定每段显示的长度
    private static final int LOG_MAXLENGTH = 4000;
    public static final boolean DEBUG = true;

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                //剩下的文本还是大于规定长度则继续重复截取并输出
                if (strLength > end) {
                    Log.e(tag + i, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.e(tag, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void JSON(String tag, Object object) {
        LogUtils.e(tag, JsonUtils.format(JsonUtils.beanToJson(object)));
    }
}
