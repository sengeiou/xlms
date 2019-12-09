package com.xl.xyl2.mvp.view.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gdswlw.library.activity.GDSBaseActivity;
import com.gdswlw.library.toolkit.AppUtil;
import com.gdswlw.library.toolkit.StatUtils;
import com.gdswlw.library.toolkit.StrUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.suke.widget.SwitchButton;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.R;
import com.xl.xyl2.net.udp.UDPServer;
import com.xl.xyl2.utils.AppUtils;
import com.xl.xyl2.utils.WindowUtils;
import com.xl.xyl2.widget.ItemMore;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Afun on 2019/10/8.
 */

public class ActivitySetting extends GDSBaseActivity {
    Toolbar toolbar;
    private Timer timer;

    @Override
    public void initUI() {
        id(R.id.im_wifi_setting).click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        }).id(R.id.im_system_setting).click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }).id(R.id.im_apps).click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ActivityAllApps.class));
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        }).id(R.id.im_bind).click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ActivityBind.class));
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
        toolbar = viewId(R.id.toolbar);
        toolbar.setTitle(getString(R.string.settings));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((ItemMore) viewId(R.id.im_service_host)).itemDesTextNoDrawbleMore(XLContext.URL_IP);
        ((ItemMore) viewId(R.id.im_serivce_status)).itemDesTextNoDrawbleMore("检测中");
        ((ItemMore) viewId(R.id.im_version_code)).itemDesTextNoDrawbleMore(StrUtil.appendString("RK-", AppUtil.getVersionName(context), "-", AppUtil.getVersionCode(context)));
        ((ItemMore) viewId(R.id.im_termainal_number)).itemDesTextNoDrawbleMore(AppUtils.getTernimalNum());
        int free = StatUtils.getExternalStorageUsePercent();
        ((ItemMore) viewId(R.id.im_use)).itemDesText(free + "%");
        id(R.id.im_use).click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = getPackageManager().getLaunchIntentForPackage("com.android.rk");
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(mIntent);
                } catch (ActivityNotFoundException e) {
                    UIKit.dLog(e.getMessage());
                }
            }
        });
        timer = new Timer();
        id(R.id.btn_logout).click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        com.suke.widget.SwitchButton switchButton =
                findViewById(R.id.switch_button);
        switchButton.setChecked(XLContext.config.getBoolean("debug"));
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job
                if(isChecked){
                    WindowUtils.showPopupWindow(getApplicationContext());
                    XLContext.config.save("debug",true);
                    finish();
                }else{
                    XLContext.config.remove("debug");
                    WindowUtils.hidePopupWindow();
                }
            }
        });

    }





    private void exit() {
        new AlertDialog.Builder(ActivitySetting.this)
                .setMessage("确定要退出么?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        WindowUtils.hidePopupWindow();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).create().show();
    }

    @Override
    public void regUIEvent() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UDPServer udpServer =UDPServer.current();
                        if (udpServer == null) {
                            ((ItemMore) viewId(R.id.im_serivce_status)).itemDesTextNoDrawbleMore("连接失败");
                        } else {
                            ((ItemMore) viewId(R.id.im_serivce_status)).itemDesTextNoDrawbleMore(
                                    udpServer.IsLink() ? "连接成功" : "连接失败"
                            );
                        }
                    }
                });
            }
        }, 0, 10 * 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public int getLayout() {
        return R.layout.activity_settings;
    }
}
