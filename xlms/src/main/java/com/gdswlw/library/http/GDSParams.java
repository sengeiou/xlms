package com.gdswlw.library.http;

import com.loopj.android.http.RequestParams;

/**
 * Created by shihuanzhang on 2017-12-14.
 */

public class GDSParams extends RequestParams {
    public Object get(Object key){
        if(urlParams.containsKey(key)){
            return  urlParams.get(key);
        }else if(urlParamsWithObjects.containsKey(key)){
            return urlParamsWithObjects.get(key);
        }
        return null;
    }
}
