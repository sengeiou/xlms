package com.xl.xyl2.mvp.contract;

import android.content.Context;

import com.xl.xyl2.callback.ICallback;
import com.xl.xyl2.mvp.IMvpModel;
import com.xl.xyl2.mvp.IMvpPresenter;
import com.xl.xyl2.mvp.IMvpView;
import com.xl.xyl2.play.Data;

import org.json.JSONException;
import org.json.JSONObject;

public interface IMainContract {

    interface IMainView extends IMvpView {
        void showSuccess(Data data);
        void showFail(String msg,String errCode);
        void showProgress();
        void dimissProgress();
    }

    interface IMainModel extends IMvpModel {
        void getData(ICallback callback);

        void init(Context context);
    }

    interface IMainPresenter extends IMvpPresenter<IMainView> {
        void getData();
    }
}
