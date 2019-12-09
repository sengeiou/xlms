package com.xl.demo;

import android.app.Application;

import com.xl.xyl2.XLContext;

/**
 * Created by Afun on 2019/12/4.
 */

public class AppContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XLContext.init(this);//init
    }
}
