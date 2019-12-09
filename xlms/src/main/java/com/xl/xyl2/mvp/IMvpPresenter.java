package com.xl.xyl2.mvp;


public interface IMvpPresenter<V extends IMvpView> {
    /**
     * 依附生命view
     *
     * @param view
     */
    void attachView(V view);

    /**
     * 分离View
     */
    void detachView();

    /**
     * 判断View是否已经销毁
     *
     * @return
     */
    boolean isViewAttached();
}
