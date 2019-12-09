package com.xl.xyl2.mvp.base;

import android.content.Context;

import com.xl.xyl2.mvp.IMvpPresenter;
import com.xl.xyl2.mvp.IMvpView;

public abstract class BaseMvpPres<V extends IMvpView> implements IMvpPresenter<V> {
    private Context ctx;
    public V attachedView;

    public BaseMvpPres(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    @Override
    public void attachView(V view) {
        this.attachedView = view;
    }

    @Override
    public void detachView() {
        if (attachedView != null)
            attachedView = null;
    }

    @Override
    public boolean isViewAttached() {
        return attachedView != null;
    }

    public V getView() {
        return attachedView;
    }
}
