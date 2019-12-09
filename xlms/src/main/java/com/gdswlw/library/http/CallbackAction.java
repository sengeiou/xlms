package com.gdswlw.library.http;

/**
 * Created by shihuanzhang on 2017-11-13.
 */

public interface CallbackAction {
    /**
     * On request Start
     * @param url  request url
     */
    void requestStart(String url);

    /**
     * @param url  request url
     * On request finish
     */
    void requestFinish(String url);

    /**
     * @param url  request url
     * On request cancel
     */
    void requestCancel(String url);
}
