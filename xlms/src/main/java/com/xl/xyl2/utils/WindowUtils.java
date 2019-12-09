package com.xl.xyl2.utils;
import android.content.Context;
import android.graphics.PixelFormat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gdswlw.library.toolkit.AppUtil;
import com.gdswlw.library.toolkit.DateUtil;
import com.gdswlw.library.toolkit.ScreenUtil;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.R;

/**
 * 弹窗辅助类
 *
 * @ClassName WindowUtils
 *
 *
 */
public class WindowUtils {
    private static final String LOG_TAG = "WindowUtils";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    public static Boolean isShown = false;
    /**
     * 显示弹出框
     *
     * @param context
     */
    public static void showPopupWindow(final Context context) {
        if (isShown) {
            Log.i(LOG_TAG, "return cause already shown");
            return;
        }
        isShown = true;
        Log.i(LOG_TAG, "showPopupWindow");
        // 获取应用的Context
        mContext = context.getApplicationContext();
        // 获取WindowManager
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView(context);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 类型
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        // 设置flag
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;;
        // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
        // 不设置这个flag的话，home页的划屏会有问题
        params.width = ScreenUtil.getRealScreenWidth(context)-150;
        params.height =  ScreenUtil.getRealScreenHeight(context)-150;
        params.gravity = Gravity.CENTER;
        mWindowManager.addView(mView, params);
    }
    /**
     * 隐藏弹出框
     */
    public static void hidePopupWindow() {
        Log.i(LOG_TAG, "hide " + isShown + ", " + mView);
        if (isShown && null != mView) {
            Log.i(LOG_TAG, "hidePopupWindow");
            mWindowManager.removeView(mView);
            scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            isShown = false;
        }
    }
    private static TextView textView;
    private static ScrollView  scrollView;
    private static View setUpView(final Context context) {
        Log.i(LOG_TAG, "setUp view");
        View view = LayoutInflater.from(context).inflate(R.layout.popupwindow,
                null);
        scrollView = view.findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        view.findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                XLContext.config.remove("debug");
                hidePopupWindow();
            }
        });
        textView = view.findViewById(R.id.tv_message);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowUtils.hidePopupWindow();
            }
        });
        setMessage("当前软件版本 "+ AppUtil.getVersionName(context),true);
        return view;
    }

    private static ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    };

    public static void setMessage(final String message, final boolean isApped){
        if(message != null && textView != null && isShown){
            textView.post(new Runnable() {
                @Override
                public void run() {
                    if(isApped){
                        textView.append("\n");
                    }
                    textView.append(DateUtil.getDateStrYmdHms()+"   ");
                    textView.append(Html.fromHtml("<font color=\"red\">"+message+"</font>"));
                }
            });

        }
    }
}