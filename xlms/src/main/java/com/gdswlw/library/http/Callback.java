package com.gdswlw.library.http;

import org.json.JSONObject;

/**
 * Created by shihuanzhang on 2017-11-13.
 */

public interface Callback {
    /**
     * onSuccess
     * @param url  request url
     * @param jsonObject response data
     * @param type  0 for Nerwork data,-1 for Local data
     */
    void onSuccess(String url, JSONObject jsonObject, int type);

    /**
     * onFailure
     * @param url request url
     * @param throwable failure message
     */
    void onFailure(String url, Throwable throwable);
}
