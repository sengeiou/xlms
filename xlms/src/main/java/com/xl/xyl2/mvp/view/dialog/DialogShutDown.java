package com.xl.xyl2.mvp.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gdswlw.library.toolkit.ScreenUtil;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Afun on 2019/10/7.
 */

public class DialogShutDown extends Dialog {

    private Handler mOffHandler;
    private Timer mOffTime;
    private TextView tvMessage;


    public DialogShutDown(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        setCanceledOnTouchOutside(false);
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOffTime.cancel();
                //取消本次开关机设置
                dismiss();
            }
        });
        tvMessage = findViewById(R.id.tv_message);
        mOffHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what > 0) {
                    tvMessage.setText("自动关机模式即将在"+msg.what+"秒后关机\n是否关机？");
                } else {
                    ////倒计时结束自动关闭
                    mOffTime.cancel();
                    XLContext.shutdown();
                }
                super.handleMessage(msg);
            }

        };

        mOffTime = new Timer();
        TimerTask tt = new TimerTask() {
            int countTime = 30;
            public void run() {
                if (countTime > 0) {
                    countTime--;
                }
                Message msg = new Message();
                msg.what = countTime;
                mOffHandler.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 0, 1000);
        getWindow().setType(WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mOffTime != null){
            mOffTime.cancel();
        }
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = ScreenUtil.getScreenWidth(getContext()) / 3;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
    }
}
