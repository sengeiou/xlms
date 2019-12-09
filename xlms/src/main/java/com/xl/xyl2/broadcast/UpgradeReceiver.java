package com.xl.xyl2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xl.xyl2.XLContext;
import com.xl.xyl2.mvp.view.activity.XLMain;

public class UpgradeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //监听app被移除
        if (action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getDataString();
            packageName = packageName.substring(packageName.lastIndexOf(":") + 1);
            if (context.getPackageName().equals(packageName)) {
                XLContext.lauchApp(XLMain.class);
            }
        }
    }
}