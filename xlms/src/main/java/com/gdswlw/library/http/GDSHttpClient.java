package com.gdswlw.library.http;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.gdswlw.library.toolkit.FileUtil;
import com.gdswlw.library.toolkit.MD5;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * For network request
 * Created by shihuanzhang on 2017-11-13.
 */

public class GDSHttpClient {
    public static final String KEY_PAGE = "key_page";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient syncClient = new SyncHttpClient();
    private static File cacheBaseDir;
    private static final String CACHE_DIR_NAME = "gdsHttp";
    private static final long cacheValidityTime = 7 * 24 * 3600;
    private static NetWorkCheck netWorkCheck;
    private static ParseCondition parseCondition;
    private static int TIME_OUT = 30 * 1000;
    static {
        //set timeout time
        client.setTimeout(TIME_OUT);
        client.setConnectTimeout(TIME_OUT);
        client.setResponseTimeout(TIME_OUT);
        client.setMaxRetriesAndTimeout(1, TIME_OUT);

        syncClient.setTimeout(TIME_OUT);
        syncClient.setConnectTimeout(TIME_OUT);
        syncClient.setResponseTimeout(TIME_OUT);
        syncClient.setMaxRetriesAndTimeout(1, TIME_OUT);
    }
    public static HashMap<String,Object> createParam(){
        return new HashMap<>();
    }
    /**
     * global params for request,eg:login token
     */
    private static HashMap<String,Object> globalParams =  new HashMap<>();

    /**
     * Add a global param
     * @param key requet key
     * @param value  value
     */
    public static void addGlobalParam(String key,Object value){
        globalParams.put(key, value);
    }

    /**
     * remove a param from global param
     * @param key
     */
    public static void removeGlobalParam(String key){
        globalParams.remove(key);
    }

    /**
     * clear all params from global param
     */
    public static void clearGlobalParam(){
        globalParams.clear();
    }
    /**
     * register a  network check interface
     *
     * @param netWorkCheck
     */
    public static void registerNetWorkCheck(@NonNull NetWorkCheck netWorkCheck) {
        GDSHttpClient.netWorkCheck = netWorkCheck;
    }

    public static void registerParseCondition(@NonNull ParseCondition parseCondition){
        GDSHttpClient.parseCondition = parseCondition;
    }



    public static AsyncHttpClient getClient() {
        return getClient(false);
    }


    public static AsyncHttpClient getClient(boolean isSync) {
        return isSync ? syncClient : client;
    }


    /**
     * interface for check network,support by context
     */
    public interface NetWorkCheck {
        boolean isConnect();
    }

    public interface ParseCondition<T>{
        /**
         * check response is succeed
         * @param jsonObject  response data
         * @return
         */
        boolean isSuccee(JSONObject jsonObject);

        /**
         * get error messge from response
         * @param jsonObject response data
         * @return
         */
        String errorMessage(JSONObject jsonObject);


        void onRequestTimeout(String message);
    }

    /**
     * set cache base dir,common global config
     *
     * @param cacheBaseDir
     */
    public static void setCacheDir(@NonNull File cacheBaseDir) {
        GDSHttpClient.cacheBaseDir = cacheBaseDir;
    }

    private static @NonNull
    String getCachePath() {
        if (cacheBaseDir == null) {
            cacheBaseDir = Environment.getExternalStorageDirectory();
        }
        File file = new File(cacheBaseDir.getPath() + "/" + CACHE_DIR_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath() + "/";
    }

    /**
     * update data cache file by url
     *
     * @param url
     * @param response
     */
    private static void updateCache(String url, String response) {
        if(TextUtils.isEmpty(url) || (response == null || response.trim().length() == 0)){
            return;
        }
        FileUtil.writeStr2File(getCachePath() + MD5.getMD5(url), response.toString());
    }

    /**
     * delete data cache file by url
     *
     * @param url
     */
    private static void deleteCache(String url) {
        File file = new File(getCachePath() + MD5.getMD5(url));
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * get json from cache by url
     *
     * @param url
     * @return
     */
    private static JSONObject getCache(String url) {
        File file = new File(getCachePath() + MD5.getMD5(url));
        if (file != null && file.exists()) {
            String data = FileUtil.readFromFile(file);
            try {
                return new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * get cache file by url
     *
     * @param url
     * @return
     */
    private static File getCacheFile(String url) {
        File file = new File(getCachePath() + MD5.getMD5(url));
        if (file != null && file.exists()) {
            return file;
        }
        return null;
    }

    /**
     * Call a get request
     *
     * @param url             request url
     * @param params          request params,k-v
     * @param responseHandler request callback
     */
    public static void get(String url, HashMap<String, ?> params, AsyncHttpResponseHandler responseHandler) {
        responseHandler.setTag(url);
        client.get(url, convertMap(params), responseHandler);
    }

    /**
     * Call a post request
     *
     * @param url             request url
     * @param params          request params,k-v
     * @param responseHandler request callback
     */
    public static void post(String url, HashMap<String, Object> params, AsyncHttpResponseHandler responseHandler) {
        responseHandler.setTag(url);
        if(globalParams.keySet().size() > 0){
            params.putAll(globalParams);
        }
        client.post(url, convertMap(params), responseHandler);
    }

    /**
     * Call a post request
     *
     * @param url            request url
     * @param params         request params,k-v
     * @param callback       request callback
     * @param callbackAction action callback when post a http request,start,calcel,finish
     * @param isCache
     */
    private  static void post(final String url, final GDSParams params, final @NonNull Callback callback,
                              final CallbackAction callbackAction, final boolean isCache) {
        if (netWorkCheck != null) {
            if (!netWorkCheck.isConnect()) {
                String tempUrl = url;
                if(globalParams.containsKey(KEY_PAGE) && params.has((String)globalParams.get(KEY_PAGE))){
                    tempUrl += params.get(globalParams.get(KEY_PAGE));
                }
                File file = getCacheFile(tempUrl);
                if (file != null) {
                    if (System.currentTimeMillis() - file.lastModified() > cacheValidityTime) {
                        deleteCache(tempUrl);
                        callback.onFailure(url, new IllegalStateException("No network"));
                    } else {
                        JSONObject data = getCache(tempUrl);
                        if (data != null) {
                            successResult(url, data,-1,callback);
                        } else {
                            callback.onFailure(url, new IllegalStateException("No network"));
                        }
                    }
                } else {
                    callback.onFailure(url, new IllegalStateException("No network"));
                }
                return;
            }
        }
        //json callback
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler("UTF-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 200) {
                    if( callback != null){
                        successResult(url, response,0,callback);
                    }
                    if(isCache){//cache data
                        //Whether there is paging
                        if(globalParams.containsKey(KEY_PAGE) && params.has((String)globalParams.get(KEY_PAGE))){
                            updateCache(url+params.get(globalParams.get(KEY_PAGE)), response.toString());
                        }else{
                            updateCache(url, response.toString());
                        }
                    }
                }
            }

//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                if (callback != null) {
//                    callback.onFailure(url, throwable);
//                }
//            }
            @Override
            public void  onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                if (callback != null) {
                    callback.onFailure(url, throwable);
                    if(throwable instanceof SocketException){
                        //request timeout
                        if(GDSHttpClient.parseCondition != null){
                             GDSHttpClient.parseCondition.onRequestTimeout("Request timeout");
                        }
                    }
                }
            }


            @Override
            public void onStart() {
                if (callbackAction != null) {
                    callbackAction.requestStart(url);
                }
            }

            @Override
            public void onFinish() {
                if (callbackAction != null) {
                    callbackAction.requestFinish(url);
                }
            }

            @Override
            public void onCancel() {
                if (callbackAction != null) {
                    callbackAction.requestCancel(url);
                }
            }
        };
        jsonHttpResponseHandler.setTag(url);
        client.post(url, params, jsonHttpResponseHandler);
    }

    /**
     *
     * @param url request url
     * @param data request result
     * @param type 0 network,-1 offline file cache
     * @param callback
     */
    private static void successResult(String url,JSONObject data,int type,Callback callback ){
        if(parseCondition != null){
            if(parseCondition.isSuccee(data)){
                callback.onSuccess(url,data,type);
            }else {
                callback.onFailure(url,new IllegalStateException(parseCondition.errorMessage(data)));
            }
        }else{
            callback.onSuccess(url,data,type);
        }
    }


    /**
     * Call a post request
     *
     * @param url      request url
     * @param params   request params,k-v
     * @param callback request callback
     */
    public static void post(String url, HashMap<String, Object> params, final @NonNull Callback callback) {
        if(globalParams.keySet().size() > 0){
            params.putAll(globalParams);
        }
        post(url, params,callback,true);
    }

    /**
     * Call a post request
     *
     * @param url      request url
     * @param params   request params,k-v
     * @param callback request callback
     * @param isCache true cache network data,false no cache
     */
    public static void post(String url, HashMap<String, Object> params, final @NonNull Callback callback,boolean isCache) {
        if(globalParams.keySet().size() > 0){
            params.putAll(globalParams);
        }
        post(url, convertMap(params), callback, null,isCache);
    }

    /**
     * Call a post request
     *
     * @param url            request url
     * @param params         request params,k-v
     * @param callback       request callback
     * @param callbackAction action callback when post a http request,start,calcel,finish
     */
    public static void post(String url, HashMap<String, Object> params, final @NonNull Callback callback, CallbackAction callbackAction) {
        if(globalParams.keySet().size() > 0){
            params.putAll(globalParams);
        }
        post(url, convertMap(params), callback, callbackAction,true);
    }

    /**
     * post json in body
     * @param url
     * @param json
     * @param callback
     */
    public static void postWithJsonBody(Context context,final String url, @NonNull  JSONObject json, final @NonNull Callback callback,final CallbackAction callbackAction,boolean isSync) {
        try {
            StringEntity params = new StringEntity(json.toString(),"UTF-8");
            getClient(isSync).post(context,url,params,"application/json",new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    //response is  null
                    if(response == null){
                        callback.onFailure(url,new IllegalStateException("response is null"));
                        return;
                    }
                    //parseCondition is sucess or fail
                    if(parseCondition != null){
                        if(parseCondition.isSuccee(response)){
                            if(callback!= null){
                                callback.onSuccess(url,response,0);
                            }
                        }else {
                            if(callback!= null){
                                IllegalState illegalState = new IllegalState(parseCondition.errorMessage(response));
                                illegalState.setErrcode(response.optString("errCode"));
                                callback.onFailure(url,illegalState);
                            }
                        }
                    }else{
                        if(callback!= null){
                            callback.onSuccess(url,response,0);
                        }
                    }

                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    if(callback != null){
                        callback.onFailure(url,throwable);
                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
                    if(callbackAction != null){
                        callbackAction.requestStart(url);
                    }
                }

                @Override
                public void onCancel() {
                    super.onCancel();
                    if(callbackAction != null){
                        callbackAction.requestStart(url);
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if(callbackAction != null){
                        callbackAction.requestFinish(url);
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public static void postWithJsonBody(Context context,final String url, @NonNull  JSONObject json, final @NonNull Callback callback,final CallbackAction callbackAction) {
        postWithJsonBody(context,url,json,callback,callbackAction,false);
    }

    /**
     * post a request with file upload
     *
     * @param url        request url
     * @param params     request params
     * @param callback   request callback
     * @param uploadType file upload type
     */
    public static void upload(String url, HashMap<String, Object> params, final @NonNull
            UploadCallback callback, @NonNull UploadType uploadType) {
        upload(url,params,callback,null,uploadType);
    }

    /**
     * post a request with file upload
     *
     * @param url            request url
     * @param params         request params
     * @param callback       request callback
     * @param uploadType     file upload type
     * @param callbackAction action callback when post a http request,start,calcel,finish
     */
    public static void upload(final String url, HashMap<String, Object> params,
                              final @NonNull UploadCallback callback ,final CallbackAction callbackAction, @NonNull UploadType uploadType) {
        if (netWorkCheck != null) {
            if (!netWorkCheck.isConnect()) {
                callback.onFailure(url, new IllegalStateException("No network"));
                return;
            }
        }
        if(globalParams.keySet().size() > 0){
            params.putAll(globalParams);
        }
        //json callback
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler("UTF-8") {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (statusCode == 200) {
                    callback.onSuccess(url,response,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (callback != null) {
                    callback.onFailure(url, throwable);
                }
            }

            @Override
            public void onStart() {
                if (callbackAction != null) {
                    callbackAction.requestStart(url);
                }
            }

            @Override
            public void onFinish() {
                if (callbackAction != null) {
                    callbackAction.requestFinish(url);
                }
            }

            @Override
            public void onCancel() {
                if (callbackAction != null) {
                    callbackAction.requestCancel(url);
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                if(callback != null){
                    callback.progress(bytesWritten,totalSize);
                }
            }
        };
        jsonHttpResponseHandler.setTag(url);
        client.post(url, convertFileMap(params, uploadType), jsonHttpResponseHandler);
    }


    /**
     * covert hashMap to GDSParams
     * @param source
     * @return
     */
    private static GDSParams convertMap(HashMap<String, ?> source) {
        GDSParams GDSParams = new GDSParams();
        if (source != null) {
            for (Map.Entry<String, ?> entry : source.entrySet()) {
                GDSParams.put(entry.getKey(), entry.getValue());
            }
        }
        return GDSParams;
    }

    /**
     * Convert the file filed to the specified UploadType
     *
     * @param source     post params
     * @param uploadType file,inputStream,byteArrayInputStream
     * @return
     */
    private static GDSParams convertFileMap(HashMap<String, ?> source, UploadType uploadType) {
        GDSParams GDSParams = convertMap(source);
        if (source != null) {
            for (Map.Entry<String, ?> entry : source.entrySet()) {
                if (entry.getValue() instanceof File) {
                    File file = (File) entry.getValue();
                    if (file != null && file.isFile() && file.exists()) {
                        switch (uploadType) {
                            case INPUT_FILE:
                                try {
                                    GDSParams.put(entry.getKey(),file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case INPUT_STREAM:
                                try {
                                    GDSParams.put(entry.getKey(), new FileInputStream(file));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case BYTE_ARRAY_INPUT_STREAM:
                                GDSParams.put(entry.getKey(),
                                        new ByteArrayInputStream(FileUtil.getBytesByFilePath(file.getAbsolutePath())));
                                break;
                        }
                    }

                }
            }
        }
        return GDSParams;
    }

    /**
     * Set request timeout,common global config
     *
     * @param second min 5s,max 60s
     */
    public static void requestTimeOut(int second) {
        if (second < 5) {
            second = 5;
        } else if (second > 60) {
            second = 60;
        }
        client.setTimeout(second * 1000);

    }

    /**
     * Set response timeout,common global config
     *
     * @param second min 5s,max 60s
     */
    public static void responseTimeout(int second) {
        if (second < 5) {
            second = 5;
        } else if (second > 60) {
            second = 60;
        }
        client.setResponseTimeout(second * 1000);
    }

    /**
     * Calcel request by tag
     *
     * @param tag
     */
    public static void cancelByTag(@NonNull Object tag) {
        client.cancelRequestsByTAG(tag, true);
    }

    /**
     * Calcel request by context object
     *
     * @param context
     */
    public static void cancelByContext(@NonNull Context context) {
        client.cancelRequests(context, true);
    }

    /**
     * Calcel all http request
     */
    public static void cancelAll() {
        client.cancelAllRequests(true);
    }

    /**
     * download file
     *
     * @param fileUrl          remote file url
     * @param savedFile        saved file
     * @param callbackDownload download callback
     */
    public static void download(@NonNull String fileUrl, @NonNull File savedFile, final
    @NonNull CallbackDownload callbackDownload,boolean isSync,HashMap<String,String> headers) {
        if(headers != null){
            Iterator<String> keys = headers.keySet().iterator();
            while (keys.hasNext()){
                String key = keys.next();
                getClient(isSync).addHeader(key,headers.get(key));
            }
        }
        getClient(isSync).get(fileUrl, new FileAsyncHttpResponseHandler(savedFile,true) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                if (callbackDownload != null) {
                    callbackDownload.onFailure(throwable);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                if (callbackDownload != null) {
                    callbackDownload.onSuccess(file);
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                if (callbackDownload != null) {
                    callbackDownload.progress(bytesWritten, totalSize);
                }
            }
        });
    }


    public static void download(@NonNull String fileUrl, @NonNull File savedFile, final
    @NonNull CallbackDownload callbackDownload,boolean isSync) {
        download(fileUrl,savedFile,callbackDownload,isSync,null);
    }

    public static void download(@NonNull String fileUrl, @NonNull File savedFile, final
    @NonNull CallbackDownload callbackDownload) {
        download(fileUrl,savedFile,callbackDownload,false,null);
    }

    /**
     * download file
     *
     * @param fileUrl             remote file url
     * @param savedFile           saved file
     * @param callbackDownload    download callback
     * @param allowedContentTypes all download file type,eg:new String[] { "image/png", "image/jpeg" };
     */
    public static void download(@NonNull String fileUrl, @NonNull final File savedFile, final
    @NonNull CallbackDownload callbackDownload, String[] allowedContentTypes) {
        client.get(fileUrl, new BinaryHttpResponseHandler(allowedContentTypes) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                if (callbackDownload != null) {
                    if (FileUtil.byte2File(savedFile, binaryData)) {
                        callbackDownload.onSuccess(savedFile);
                    } else {
                        callbackDownload.onSuccess(null);
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                if (callbackDownload != null) {
                    callbackDownload.onFailure(error);
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                if (callbackDownload != null) {
                    callbackDownload.progress(bytesWritten, totalSize);
                }
            }
        });
    }
}
