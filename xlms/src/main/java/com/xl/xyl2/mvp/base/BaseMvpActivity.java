package com.xl.xyl2.mvp.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xl.xyl2.mvp.IMvpPresenter;
import com.xl.xyl2.mvp.IMvpView;

public abstract class BaseMvpActivity<V extends IMvpView, P extends IMvpPresenter<V>> extends AppCompatActivity {
    public P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        if (presenter == null)
            presenter = createPresenter();

        //绑定生命周期
        if (presenter != null) {
            presenter.attachView((V) this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroy();
    }

    protected abstract P createPresenter();

    protected abstract int getLayoutRes();
}
