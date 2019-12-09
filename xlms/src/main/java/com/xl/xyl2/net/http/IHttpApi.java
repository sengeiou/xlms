package com.xl.xyl2.net.http;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @author hexiancheng
 * @description retrofit2封装接口类
 * @date 2019/8/23
 */
public interface IHttpApi {

    @GET
    Call<String> getMethod(@Url String url);


    @FormUrlEncoded
    @POST
    Call<String> postMethod(@Url String url, @FieldMap Map<String, String> map);


    @Headers({"Content-Type:application/json", "Accept:application/json"})
    @POST
    Call<String> postMethod(@Url String url, @Body RequestBody body);


}
