package com.gdswlw.library.toolkit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 *
 * @author shi
 */
public class NetUtil {
    public final static int Type_3G = 1;//3g网络
    public final static int Type_2G = 2;//2g网络
    public final static int Type_WIFI = 3;//无线网络
    public final static int Type_4G = 4;//4g网络
    public final static int Type_Ethernet = 5;//以太网
    public final static int Type_Mobile = 6;//移动网络

    /**
     * 判断网络是否可用
     */
    public static boolean CheckNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].isConnected() && info[i].isAvailable()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否有可用的网络
     *
     * @param context
     * @return
     */
    public static boolean hasInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.isRoaming()) {
            return true;
        }
        return true;
    }

    /**
     * 获取网络的类型
     *
     * @param context
     * @return -1表示未知网络信息 ，1 表示3g网络 ，2表示2g网络，3无线网络
     */
    public static int getNetWorkType(Context context) {
        int type = -1;
        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null) {
            switch (info.getSubtype()) {
                case ConnectivityManager.TYPE_MOBILE:// 移动网络
//                    switch (info.getSubtype()) {
//                        case TelephonyManager.NETWORK_TYPE_HSUPA:
//                        case TelephonyManager.NETWORK_TYPE_UMTS:// 联通3G
//                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                        case TelephonyManager.NETWORK_TYPE_EVDO_B:// 电信3G
//                            type = Type_3G;
//                            break;
//                        case TelephonyManager.NETWORK_TYPE_EDGE:// 2G网络
//                        case TelephonyManager.NETWORK_TYPE_CDMA:
//                        case TelephonyManager.NETWORK_TYPE_GPRS:
//                            type = Type_2G;
//                            break;
//                        case TelephonyManager.NETWORK_TYPE_LTE:
//                            type = Type_4G;
//                            break;
//                    }
                    type = Type_WIFI;
                    break;
                case ConnectivityManager.TYPE_ETHERNET:// 无线网络
                    type = Type_Ethernet;
                    break;
            }
        }
        return type;
    }
}
