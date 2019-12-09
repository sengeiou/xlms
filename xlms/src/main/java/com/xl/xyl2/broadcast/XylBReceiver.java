package com.xl.xyl2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xl.xyl2.XLContext;
import com.xl.xyl2.mvp.view.activity.XLMain;
import com.xl.xyl2.mvp.view.activity.ActivityShow;
import com.xl.xyl2.play.Setting;
import com.xl.xyl2.utils.AutoOc;

/**
 * Created by Afun on 2019/10/7.
 */

public class XylBReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {//开机
            Log.d("XylBReceiver","boot started");
            if(XLMain.getInstance() == null){
                XLContext.lauchApp(XLMain.class);
            }
            if(ActivityShow.instatnce == null){
                XLContext.lauchApp(ActivityShow.class);
            }

        }
        if (action.equals(Intent.ACTION_SHUTDOWN)) {//关机
            Setting setting = XLContext.getSettingData();
            if(setting != null){
                AutoOc.syncOcSettings(setting);//同步开关机设置
            }
        }
    }
}
