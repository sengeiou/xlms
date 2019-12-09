package com.xl.xyl2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.gdswlw.library.http.Callback;
import com.gdswlw.library.http.GDSHttpClient;
import com.gdswlw.library.toolkit.AppUtil;
import com.gdswlw.library.toolkit.NetUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.callback.CmdHandler;
import com.xl.xyl2.mvp.view.activity.ActivityShow;
import com.xl.xyl2.net.udp.UDPServer;
import com.xl.xyl2.play.Data;
import com.xl.xyl2.play.Setting;
import com.xl.xyl2.task.ClearStorageSpaceThread;
import com.xl.xyl2.utils.AppUtils;
import com.xl.xyl2.utils.BDLocationUtil;
import com.xl.xyl2.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.xl.xyl2.mvp.view.activity.ActivityShow.ACTION_UPDATE_LOCATION;
import static com.xl.xyl2.mvp.view.activity.ActivityShow.ACTION_UPDATE_PROGRAM;
import static com.xl.xyl2.mvp.view.activity.ActivityShow.ACTION_VAL;

/**
 * MainService
 */
public class MainService extends Service {
    private Handler handler;
    /**
     * 初始化UDP
     */
    public static final String ACTION_INIT_UDP = "action_init_udp";

    /**
     * 获取服务器时间
     */
    public static final String ACTION_GET_SERVER_TIME = "action_get_server_time";



    /**
     * 定时时间记录
     */
    HashMap<Integer,Long> delays = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getBaseContext();
    }


    CmdHandler cmdHandler;
    Timer timer;
    SyncTask syncTask;

    /**
     * 本地服务器时间差
     */
    private long timedifference = 0l;
    private Context context;
    BDLocationUtil bdLocationUtil;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction().equals(ACTION_INIT_UDP) && intent.getStringExtra("im") != null) {
                final String[] urlArr = intent.getStringExtra("im").split(":");
                //初始化UDP服务
                cmdHandler = new CmdHandler(getApplicationContext());
                UDPServer.start(urlArr[0], Integer.parseInt(urlArr[1]), AppUtils.getTernimalNum(), 0, cmdHandler);
                uploadInfo(null);
                getServerTime();//首次获取服务器时间
                //配置轮询同步数据
                handler = new Handler();
                timer = new Timer();
                syncTask = new SyncTask();
                timer.schedule(syncTask, 0, 3 * 1000);//定时检测开始
                location();
            } else if (intent.getAction().equals(ACTION_GET_SERVER_TIME)) {
                getServerTime();
            }
        }
        if(clearStorageSpace == null){
            //启动存储空间检测线程
            clearStorageSpace = new ClearStorageSpaceThread();
            new Thread(clearStorageSpace).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 定位
     */
    private void location() {
        bdLocationUtil = BDLocationUtil.getInstance(getBaseContext(), new BDLocationUtil.LocCallback() {
            @Override
            public void onLocStart() {
                UIKit.dLog("location start");
            }

            @Override
            public void onLocResult(BDLocation location) {

                if (location != null) {
                    //上传经纬度
                    uploadInfo(location);
                    UIKit.eLog("准备就绪，通知前台更新地理位置");
                    Intent intent = new Intent(ActivityShow.ACTIVITY_SHOW_RECEIVER);
                    intent.putExtra(ACTION_VAL, ACTION_UPDATE_LOCATION);
                    sendBroadcast(intent);
                }
            }
        });
        bdLocationUtil.startLocation();
    }

    ClearStorageSpaceThread clearStorageSpace;


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (syncTask != null) {
            syncTask.cancel();
            syncTask = null;
        }

        if (bdLocationUtil != null) {
            bdLocationUtil.stopLocation();
        }
        if(clearStorageSpace != null){
            clearStorageSpace.isRun = false;
        }
    }



    /**
     * TimerTask对象，主要用于定时调用api
     */
    public class SyncTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis();
                    if(isNeedPost( Constant.TIMER_CODE_SYNC,20 * 60 * 1000,time)){//20min
                        //同步数据
                        syncDataTimer();
                    }
                    if(isNeedPost( Constant.TIMER_CODE_GETTIME,10 * 60 * 1000,time)){//10min
                        //同步时间
                        getServerTime();
                    }
                }
            });
        }
    }


    /**
     * 是否需要post请求
     * @param code 标识码
     * @param maxTime 间隔时间
     * @param currentTime 当前的时间
     * @return
     */
    private boolean isNeedPost(int code,int maxTime,long currentTime){
        if(delays.containsKey(code)){
            if(currentTime - delays.get(code) >= maxTime){
                delays.put(code,currentTime);
                return  true;
            }

        }else{
            delays.put(code,currentTime);
        }
        return  false;
    }

    /**
     * 定时同步数据,处理播放列表和设置信息
     */
    private void syncDataTimer() {
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(getBaseContext()) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(getBaseContext(), XLContext.API_URL + "/main/device/api/syn", json, new Callback() {
            @Override
            public void onSuccess(String url, JSONObject jsonObject, int type) {
                JSONObject dataJson = jsonObject.optJSONObject("data");
                Data data = XLContext.getGson().fromJson(dataJson.toString(),Data.class);
                Setting setting = data.getSetting();
                handleData(dataJson);//处理数据
                XLContext.saveSetting(setting);//保存设置
            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
            }

        }, null);
    }


    /**
     * upload information
     */
    private void uploadInfo(BDLocation bdLocation) {
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(getBaseContext()) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());
            JSONObject data = new JSONObject();
            if(bdLocation != null){
                if(bdLocation.getLatitude() != 4.9E-324 && bdLocation.getLongitude()!= 4.9E-324){
                    data.put("location", bdLocation.getLongitude()+","+bdLocation.getLatitude());
                    data.put("locationName", bdLocation.getAddrStr());
                    data.put("city", bdLocation.getCity());
                }
            }
            data.put("curVersionCode", AppUtil.getVersionCode(getBaseContext()));
            data.put("curVersionName", AppUtil.getVersionName(getBaseContext()));
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(getBaseContext(), XLContext.API_URL + "/main/device/reported/dInfo", json, new Callback() {
            @Override
            public void onSuccess(String url, JSONObject jsonObject, int type) {
                JSONObject data = jsonObject.optJSONObject("data");
                UIKit.dLog("reported", data.toString());
            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
            }

        }, null);
    }


    /**
     * get server time
     */
    private void getServerTime() {
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(getBaseContext()) ? "cn" : "en");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(getBaseContext(), XLContext.API_URL + "/main/validate/getServerTime", json, new Callback() {
            @Override
            public void onSuccess(String url, JSONObject jsonObject, int type) {
                try {
                    Date serverTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject.optJSONObject("data").optString("now"));
                    timedifference = System.currentTimeMillis() - serverTime.getTime();//保存时间差
                    XLContext.config.save("now", String.valueOf(timedifference));  //保存本地与服务器时间差
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
            }

        }, null);
    }


    /**
     * init data
     *
     * @param lan
     * @param code
     * @param lotCode
     * @param location
     * @param curVersionCode
     * @param curVersionName
     */
    private void initDevice(String lan, String code, String lotCode, String location,
                            int curVersionCode, String curVersionName) {
        JSONObject json = new JSONObject();
        try {
            json.put("lan", lan);
            JSONObject data = new JSONObject();
            data.put("code", code);
            data.put("lotCode", lotCode);
            if (location != null) {
                data.put("location", location);
            }
            data.put("curVersionCode", curVersionCode);
            data.put("curVersionName", curVersionName);
            int netType = NetUtil.getNetWorkType(context);
            if(netType == NetUtil.Type_Mobile ){
                data.put("netModel",  3);
            }else if(netType == NetUtil.Type_WIFI){
                data.put("netModel",  2);
            }else if(netType == NetUtil.Type_Ethernet){
                data.put("netModel",  1);
            }else{
                data.put("netModel",  0);
            }
            data.put("dufVersion",android.os.Build.DISPLAY);
            json.put("data", data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(getBaseContext(), XLContext.API_URL + "/main/device/init", json, new Callback() {
            @Override
            public void onSuccess(String url, JSONObject jsonObject, int type) {
                final JSONObject initData = jsonObject.optJSONObject("data");
                handleData(initData);
            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
            }

        }, null);

    }

    /**
     * 处理数据，对于初始化数据，同步数据采用的逻辑处理
     *
     * @param json
     */
    private void handleData(final JSONObject json) {
        //获取有效的节目
        UIKit.dLog("准备就绪，通知前台更新节目");
        Intent intent = new Intent(ActivityShow.ACTIVITY_SHOW_RECEIVER);
        intent.putExtra(ACTION_VAL, ACTION_UPDATE_PROGRAM);
        intent.putExtra("json", json.toString());
        sendBroadcast(intent);
    }
}
