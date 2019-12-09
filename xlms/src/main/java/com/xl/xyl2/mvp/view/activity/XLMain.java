package com.xl.xyl2.mvp.view.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gdswlw.library.toolkit.NetUtil;
import com.gdswlw.library.toolkit.ScreenUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.R;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.mvp.base.BaseMvpActivity;
import com.xl.xyl2.mvp.contract.IMainContract;
import com.xl.xyl2.mvp.presenter.MainPresImpl;
import com.xl.xyl2.play.Data;
import com.xl.xyl2.service.MainService;
import com.xl.xyl2.utils.WindowUtils;

import java.lang.ref.WeakReference;

public class XLMain extends BaseMvpActivity<IMainContract.IMainView, IMainContract.IMainPresenter>
        implements IMainContract.IMainView {
    private static final String TAG = XLMain.class.getSimpleName();
    private MainReceiver mainReceiver;
    public static final String ACTION_MAIN_RECEIVER = "action_main_receiver";
    public static final String ACTION_KEY = "action_main_receiver_key";
    public static final int ACTION_VALUE_GET_DATA = 0X108;
    public static XLMain instance;
    private TextView tv_status;
    private  MyHandler myHandler;

    /**
     * 根据类型显示视图
     * @param type 1 正常显示 2 无网络 3 无数据或所有节目过期
     */
    private void showView(int type){
        switch (type){
            case 1:
                findViewById(R.id.ll_loading).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_not_network).setVisibility(View.GONE);
                findViewById(R.id.ll_no_data).setVisibility(View.GONE);
                break;
            case 2:
                findViewById(R.id.ll_loading).setVisibility(View.GONE);
                findViewById(R.id.ll_not_network).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_no_data).setVisibility(View.GONE);
                break;
            case 3:
                findViewById(R.id.ll_loading).setVisibility(View.GONE);
                findViewById(R.id.ll_not_network).setVisibility(View.GONE);
                findViewById(R.id.ll_no_data).setVisibility(View.VISIBLE);
                break;
        }
    }


    /**
     * lauch play activity
     *
     * @param context
     */
    public static void luach(Context context){
        if(context == null){
            Log.e(TAG,"Context object is null");
            return;
        }
        if(! XLContext.isInit()){
            Log.e(TAG,"XLContext is not init with application");
            return;
        }

        if(ActivityShow.instatnce != null){
            ActivityShow.instatnce.finish();
            Intent intent = new Intent(context,ActivityShow.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else {
            if (XLMain.getInstance() != null) {
                XLMain.getInstance().finish();
            }
            Intent intent = new Intent(context, XLMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * release activitys
     */
    public static void release(){
        if(ActivityShow.instatnce != null) {
            ActivityShow.instatnce.finish();
        }
        if(XLMain.getInstance() != null){
            XLMain.getInstance().finish();
        }
    }

    /**
     * play or puase
     * @param isPLay
     */
    public static void play(boolean isPLay){
        if(ActivityShow.instatnce != null){
            ActivityShow.instatnce.play(isPLay);
        }
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(! XLContext.isInit()){
            Log.e(TAG,"XLContext is not init with application");
            finish();
            return;
        }
        instance = this;
        initView();
        //注册广播
        mainReceiver = new MainReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_MAIN_RECEIVER);
        registerReceiver(mainReceiver, intentFilter);
        myHandler = new MyHandler(this);
        tv_status = findViewById(R.id.tv_status);
        UIKit.dLog("screen:"+ ScreenUtil.getScreenWidth(getBaseContext())+"*"+ScreenUtil.getScreenHeight(getBaseContext()));
        UIKit.dLog("realscreen:"+ ScreenUtil.getRealScreenWidth(getBaseContext())+"*"+ScreenUtil.getRealScreenHeight(getBaseContext()));
        loadData();
    }

    public static XLMain getInstance() {
        return instance;
    }



    private void initView() {
        findViewById(R.id.btn_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        findViewById(R.id.btn_setting_network).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开设置
                 startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        if(XLContext.config.getBoolean("debug")){
            WindowUtils.showPopupWindow(getApplicationContext());
        }
    }


    /**
     * 加载数据
     */
    private  void loadData(){
        if (! NetUtil.CheckNetworkAvailable(getBaseContext())) {
            showFail("No connect network","err.nonetwork");
            return;
        }
        tv_status.setText(getString(R.string.load_data));
        presenter.getData();
    }



    @Override
    public void showSuccess(Data data) {
        XLContext.saveData(data);//保存初始化数据
        handleData();
    }

    @Override
    public void showFail(String msg,String errCode) {
        tv_status.setText(msg);
        if(errCode!=null && errCode.equals("err.business")){
            myHandler.sendEmptyMessageDelayed(100,20 * 1000);//20s重试一次
            return;
        }
        UIKit.eLog(msg);
        handleData();
    }


    /**
     * 处理数据
     */
    private void  handleData(){
        Data data = XLContext.getData();
        if(data != null && instance != null){
            if (data.getImServerIpAndPort() != null) {
                //初始化成功，启动UDP服务
                startService(new Intent(getApplicationContext(), MainService.class).putExtra("im"
                        ,data.getImServerIpAndPort()).setAction(MainService.ACTION_INIT_UDP));
            }
            startActivity(new Intent(getApplicationContext(),ActivityShow.class));
            finish();
        }else{
            if (! NetUtil.CheckNetworkAvailable(getBaseContext())) {
                //showView(2);
                tv_status.setText(getString(R.string.no_network));
            }else{
                //showView(3);
                tv_status.setText(getString(R.string.no_data));
            }
            //重新请求
            myHandler.sendEmptyMessageDelayed(100,20 * 1000);//20s重试一次
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected IMainContract.IMainPresenter createPresenter() {
        return new MainPresImpl(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance  = null;
        if (mainReceiver != null) {
            unregisterReceiver(mainReceiver);
            mainReceiver = null;
        }
        myHandler.removeCallbacksAndMessages(null);
    }



    private static class MyHandler extends Handler {
        private WeakReference<XLMain> mWeakReference;

        public MyHandler(XLMain activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            XLMain mainActivity = mWeakReference.get();
            switch (msg.what) {
                case 100:
                    if (mainActivity != null) {
                        mainActivity.loadData();
                    }
                    break;
            }
        }
    }







    @Override
    public void showProgress() {
        showView(1);
    }

    @Override
    public void dimissProgress() {
        if (appProgressDialog != null && appProgressDialog.isShowing()) {
            appProgressDialog.cancel();
            appProgressDialog.dismiss();
        }
    }

    ProgressDialog appProgressDialog;

    private ProgressDialog createProgressDialog() {
        if (appProgressDialog == null) {
            appProgressDialog = new ProgressDialog(this);
            appProgressDialog.setCancelable(false);
            appProgressDialog.setMessage("Loading...");
        }
        return appProgressDialog;
    }


    /**
     * 接受UDP发过来等通知
     */
    public class MainReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int ationCode = intent.getIntExtra(ACTION_KEY, -1);
            if (ationCode != -1) {
                switch (ationCode) {
                    case ACTION_VALUE_GET_DATA://加载数据
                        loadData();
                        break;
                }
            }
        }
    }
}
