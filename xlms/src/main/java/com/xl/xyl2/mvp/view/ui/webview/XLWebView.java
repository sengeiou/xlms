package com.xl.xyl2.mvp.view.ui.webview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Afun on 2019/9/16.
 */

public class XLWebView extends WebView {

    private ProgressBar progressBar;
    private Context context;
    public XLWebView(Context context) {
        super(context);
        progressBar = new ProgressBar(context,null,android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,3));//设置宽高属性
        addView(progressBar);
        //设置内部加载器
        setWebChromeClient(new MyWebChromeClient(context,progressBar));
        setWebViewClient(new WebViewClient());
        getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        getSettings().setUseWideViewPort(true);
        //自适应屏幕
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        getSettings().setLoadWithOverviewMode(true);
    }


    public  static  void hookWebView() {
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            //拿到 WebViewFactory 类
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            //拿到类对应的 field
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            //field为private，设置为可访问的
            field.setAccessible(true);
            //拿到 WebViewFactory 的 sProviderInstance 实例
            //sProviderInstance 是 static 类型，不需要传入具体对象
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                return;
            }
            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> providerConstructor = providerClass.getConstructor(delegateClass);
            if (providerConstructor != null) {
                providerConstructor.setAccessible(true);
                Constructor<?> declaredConstructor = delegateClass.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                //利用反射创建了 sProviderInstance
                sProviderInstance = providerConstructor.newInstance(declaredConstructor.newInstance());
                //完成 sProviderInstance 赋值
                field.set("sProviderInstance", sProviderInstance);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}