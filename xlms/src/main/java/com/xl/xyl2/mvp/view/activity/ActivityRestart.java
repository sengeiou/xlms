package com.xl.xyl2.mvp.view.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;

import com.gdswlw.library.activity.GDSBaseActivity;
import com.gdswlw.library.http.Callback;
import com.gdswlw.library.http.GDSHttpClient;
import com.gdswlw.library.toolkit.FileUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.R;
import com.xl.xyl2.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Afun on 2019/10/22.
 */

public class ActivityRestart extends GDSBaseActivity {
    @Override
    public void initUI() {
        id(R.id.btn_restart).click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restart();
                timer.cancel();
            }
        });
        String path = getIntent().getStringExtra("path");
        if(path != null){
            String logs = FileUtil.readFromFile(new File(path));
            if(logs != null){
                id(R.id.tv_message).text(logs);//显示日志
                uploadLogs(logs);
            }
        }
        timer.start();
    }


    private void uploadLogs(String content){
        //判读上一次上传日志是否有超过30s
        long updateTime = XLContext.config.getLong("logTime");
        final long now  = System.currentTimeMillis();
        if(now - updateTime < (30 * 60 * 1000)){
            return;
        }
        //判断日志内容与上一次上传的一样，避免重复上传
        String preContent = XLContext.config.getString("logContent");
        if(preContent.equals(content)){
            return;
        }
        content = content.replaceAll("\n","<br/>");
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(context) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());

            JSONObject data = new JSONObject();
            data.put("terminalId", XLContext.config.getInt("main"));
            data.put("msg", content);

            json.put("data",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String uploadContent = content;

        GDSHttpClient.postWithJsonBody(context, XLContext.API_URL + "/main/device/reported/error", json, new Callback() {
            @Override
            public void onSuccess(String url,final JSONObject data, int type) {
                if(data != null){
                    UIKit.dLog("upload logs:"+data.optInt("success"));
                    XLContext.config.save("logTime",now);
                    XLContext.config.save("logContent",uploadContent);
                }

            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
            }
        }, null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void restart(){
        Intent intent =  getPackageManager().getLaunchIntentForPackage(getPackageName());
        startActivity(intent);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    @Override
    public void regUIEvent() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_err;
    }
    CountDownTimer timer = new CountDownTimer(15 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            id(R.id.btn_restart).text("Restart the App in " + millisUntilFinished / 1000 + " seconds");
        }

        @Override
        public void onFinish() {
           restart();
        }
    };

}
