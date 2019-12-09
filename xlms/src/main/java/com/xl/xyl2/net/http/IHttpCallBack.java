package com.xl.xyl2.net.http;

/**
 * @author hexiancheng
 * @description http请求回调接口类
 * @date 2019/8/23
 */
public interface IHttpCallBack<T> {

    /*
    请求返回正常
     */
    void onResponse(T val);

    /*
    请求返回错误
     */
    void onFailure(int code, String message);

}
