package com.xl.xyl2.utils;

import android.content.Context;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gdswlw.library.toolkit.FileUtil;
import com.xl.xyl2.bean.Loc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 百度地图定位工具类
 * Created by Afun on 2019/9/16.
 */

public class BDLocationUtil {
    private LocationClient mLocationClient = null;
    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口，原有BDLocationListener接口
    private MyLocationListener myListener = new MyLocationListener();
    private LocCallback locCallback;
    private Context context;
    /**
     * 定位回调监听器
     */
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(myListener);
            if(locCallback != null){
                locCallback.onLocResult(location);
            }
            if(location != null){
                serilizeLocationData(location);
            }

        }
    }
    public interface LocCallback{
        void onLocStart();
        void onLocResult(BDLocation location);
    }
    private static BDLocationUtil instance;

    /**
     * 获取实例
     * @param context 定位必须传此参数
     * @return
     */
    public static BDLocationUtil getInstance( Context context,LocCallback locCallback) {
        if(instance == null){
            instance = new BDLocationUtil();
        }
        instance.context = context;
        instance.locCallback = locCallback;
        return instance;
    }

    /**
     * 获取实例
     * @return
     */
    public static BDLocationUtil getInstance( Context context) {
     return getInstance(context,null);
    }

    /**
     * 获取实例
     * @return
     */
    public static BDLocationUtil getInstance() {
        return getInstance(null,null);
    }

    /**
     * 开始定位
     */
    public void  startLocation(){
        if(context == null){
            return;
        }
        mLocationClient = new LocationClient(context);
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        //开始定位
        mLocationClient.start();
        locCallback.onLocStart();
    }

    /**
     * 配置定位参数
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
//        int span = 5000;
//        option.setScanSpan(span);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setLocationNotify(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }
    /**
     * 停止定位
     */
    public void stopLocation(){
        mLocationClient.stop();
        mLocationClient.unRegisterLocationListener(myListener);
        if(locCallback != null){
            locCallback.onLocResult(null);
        }
    }

    /**
     * 序列化位置数据
     * @param location
     * @return
     */
    public void serilizeLocationData(BDLocation location) {
        Loc loc = new Loc();
        loc.setAddr(location.getAddrStr());
        loc.setProvince(location.getProvince());
        loc.setCity(location.getCity());
        loc.setArea(location.getDistrict());
        loc.setLat(location.getLatitude());
        loc.setLng(location.getLongitude());
        FileUtil.serilizenData(new File(FileUtils.getRootFile().getPath()+"/other/"),"locaion.loc",loc);
    }


    /**
     * 获取序列化位置数据
     * @return
     */
    public Loc getSerilizeLocationData() {
        Loc bean = (Loc)FileUtil.getSerilizeData(FileUtils.getRootFile().getPath()+"/other/locaion.loc");
        return bean;
    }

}
