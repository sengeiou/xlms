package com.xl.xyl2.mvp.model;

import android.content.Context;
import android.util.Log;

import com.gdswlw.library.http.Callback;
import com.gdswlw.library.http.CallbackAdapter;
import com.gdswlw.library.http.GDSHttpClient;
import com.gdswlw.library.http.IllegalState;
import com.gdswlw.library.toolkit.NetUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.callback.ICallback;
import com.xl.xyl2.mvp.contract.IMainContract;
import com.xl.xyl2.utils.AppUtils;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;


public class MainModelImpl implements IMainContract.IMainModel {
    private final String TAG = MainModelImpl.class.getSimpleName();
    private Context context;
    private ICallback callback;

    @Override
    public void getData(ICallback callback) {
        this.callback = callback;
        initDevice(
                AppUtils.isCN(context) ? "cn" : "en",
                AppUtils.getTernimalNum(),
                XLContext.lotCode,
                XLContext.getLatLng(),
                AppUtils.getAppVersionCode(context),
                AppUtils.getAppVersionName(context));
    }

    @Override
    public void init(Context context) {
        this.context = context;
    }

    /**
     * 设备初始化请求
     *
     * @param lan            设备初始化请求
     * @param code           设备编号
     * @param lotCode        批次号
     * @param location       设备经纬度
     * @param curVersionCode 版本号
     * @param curVersionName 版本名称
     *
     */

    private void initDevice(String lan, String code, String lotCode, String location,
                            int curVersionCode, String curVersionName){
        JSONObject json = new JSONObject();
        try {
            json.put("lan",  lan);
            JSONObject data = new JSONObject();
            data.put("code",  code);
            data.put("lotCode",  lotCode);
            if(location != null){
                data.put("location",  location);
            }
            data.put("curVersionCode",  curVersionCode);
            data.put("curVersionName",  curVersionName);
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
            json.put("data",data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(context, XLContext.API_URL+"/main/device/init", json, new Callback() {
            @Override
            public void onSuccess(String url, JSONObject jsonObject, int type) {
                    if(callback != null){
                        //保存初始化数据
                        callback.onSuccess(jsonObject.optJSONObject("data"));

                }

            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
                Log.e("eeee","eeee init:"+throwable.getMessage());
                if(callback != null){
                    if(throwable instanceof IllegalState){
                        IllegalState illegalState = (IllegalState)throwable;
                        callback.onFail(illegalState.getMessage(),illegalState.getErrcode());
                    }else if(throwable instanceof HttpHostConnectException){
                        callback.onFail("HttpHostConnectException",null);
                    }else if(throwable instanceof SocketTimeoutException){
                        callback.onFail("SocketTimeoutException",null);
                    }
                }
            }

        },new CallbackAdapter(){
            @Override
            public void requestStart(String url) {
                super.requestStart(url);
                if(callback != null){
                    callback.onProgress();
                }
            }
        });


    }
}
