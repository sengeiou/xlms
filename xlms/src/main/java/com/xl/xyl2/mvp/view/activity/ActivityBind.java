package com.xl.xyl2.mvp.view.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gdswlw.library.activity.GDSBaseActivity;
import com.gdswlw.library.http.Callback;
import com.gdswlw.library.http.CallbackAdapter;
import com.gdswlw.library.http.GDSHttpClient;
import com.gdswlw.library.toolkit.TextUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.R;
import com.xl.xyl2.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;


/**
 * Created by Afun on 2019/10/8.
 */

public class ActivityBind extends GDSBaseActivity {
    Toolbar toolbar;

    @Override
    public void initUI() {
        toolbar = viewId(R.id.toolbar);
        toolbar.setTitle(getString(R.string.bind_termainal));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        id(R.id.btn_bind).click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind();
            }
        });
        id(R.id.et_shebeibianhao).text(AppUtils.getTernimalNum());
        id(R.id.et_shebeibianhao).enable(false);
    }


    private void bind() {
        if(!TextUtil.checkIsInput(viewId(R.id.et_shebeibianhao),viewId(R.id.et_yonghuzhanghao),
                viewId(R.id.et_yonghumima))){
            UIKit.toastShort("请输入必填数据【绑定码、账号、密码】");
            return;
        }
        /**
         * 生成绑定二维码
         */
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(context) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());
            JSONObject data = new JSONObject();
            data.put("bindCode",getEditText(R.id.et_bangdingma));
            data.put("dcode",AppUtils.getTernimalNum());
            data.put("nname",getEditText(R.id.et_zhongduanbieming));
            data.put("userCode",getEditText(R.id.et_yonghuzhanghao));
            data.put("userPsd",string2MD5(getEditText(R.id.et_yonghumima)));
            json.put("data",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(context, XLContext.API_URL + "/main/userTerminal/bind", json, new Callback() {
            @Override
            public void onSuccess(String url, final JSONObject jsonObject, int type) {
                if (jsonObject.optInt("success") == 1) {
                    UIKit.toastShort("绑定成功");
                    finish();
                } else {
                    UIKit.toastShort("绑定失败：" + jsonObject.optString("errMsg"));
                }

            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.toastShort("绑定失败：" + throwable.getMessage());
                UIKit.eLog(throwable.getMessage());
            }
        }, new CallbackAdapter() {
            @Override
            public void requestStart(String url) {
                super.requestStart(url);
                showProgressDialog("", true);
            }

            @Override
            public void requestFinish(String url) {
                super.requestFinish(url);
                dismissProgressDialog();
            }
        });
    }

    public static String string2MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return null;
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    @Override
    public void regUIEvent() {
    }


    @Override
    public int getLayout() {
        return R.layout.activity_bind;
    }
}
