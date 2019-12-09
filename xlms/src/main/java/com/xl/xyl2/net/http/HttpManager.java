package com.xl.xyl2.net.http;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author hexiancheng
 * @description http管理类
 * @date 2019/8/23
 */
public class HttpManager {

    public static void getMethod(String baseUrl, String url, final IHttpCallBack httpCallBack) {
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create()).build();
        final IHttpApi httpApi = retrofit.create(IHttpApi.class);

        final Call<String> call = httpApi.getMethod(url);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (null != response) {
                    if (response.code() == 200) {
                        httpCallBack.onResponse(response.body());
                    } else {
                        httpCallBack.onFailure(response.code(), response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                httpCallBack.onFailure(-1, t.getMessage());
            }
        });
    }

    public static void postMethod(String baseUrl, String url, Map<String, String> map, final Callback<String> callback) {
        //指定客户端
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create()).build();
        final IHttpApi httpApi = retrofit.create(IHttpApi.class);

        final Call<String> call = httpApi.postMethod(url, map);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public static void postJSON(String baseUrl, String url, String json, final IHttpCallBack callback) {
        //指定客户端
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create()).build();
        final IHttpApi httpApi = retrofit.create(IHttpApi.class);
        //
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), json);
        final Call<String> call = httpApi.postMethod(url, requestBody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (null != response) {
                    if (response.code() == 200) {
                        callback.onResponse(response.body());
                    } else {
                        callback.onFailure(response.code(), response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(-1, t.getMessage());
            }
        });
    }
}
