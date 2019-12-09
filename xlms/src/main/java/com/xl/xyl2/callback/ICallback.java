package com.xl.xyl2.callback;

public interface ICallback<K, V> {
    void onSuccess(K data);

    void onFail(V data,String errCode);

    void onProgress();

}
