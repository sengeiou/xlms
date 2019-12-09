package com.xl.xyl2.mvp.presenter;

import android.content.Context;

import com.xl.xyl2.XLContext;
import com.xl.xyl2.callback.ICallback;
import com.xl.xyl2.mvp.base.BaseMvpPres;
import com.xl.xyl2.mvp.contract.IMainContract;
import com.xl.xyl2.mvp.model.MainModelImpl;
import com.xl.xyl2.play.Data;

import org.json.JSONObject;

public class MainPresImpl extends BaseMvpPres<IMainContract.IMainView> implements IMainContract.IMainPresenter {
    private final String TAG = MainPresImpl.class.getSimpleName();
    private IMainContract.IMainModel mainModel;
    private Context context;

    public MainPresImpl(Context ctx) {
        super(ctx);
        this.context = ctx;
        mainModel = new MainModelImpl();
        mainModel.init(context);
    }

    @Override
    public void getData() {
        mainModel.getData(new ICallback<JSONObject, String>() {
            @Override
            public void onSuccess(JSONObject data) {
                if (isViewAttached()) {
                    attachedView.dimissProgress();
                    Data initData = XLContext.getGson().fromJson(data.toString(),Data.class);
                    attachedView.showSuccess(initData);
                }
            }

            @Override
            public void onFail(String data,String errCode) {
                if (isViewAttached()) {
                    attachedView.dimissProgress();
                    attachedView.showFail(data,errCode);
                }
            }

            @Override
            public void onProgress() {
                if (isViewAttached()) {
                    attachedView.showProgress();
                }
            }
        });
    }
}
