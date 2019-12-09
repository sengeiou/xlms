package com.xl.xyl2.mvp.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.gdswlw.library.toolkit.ScreenUtil;
import com.gdswlw.library.toolkit.TextUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.R;
import com.xl.xyl2.play.Setting;

/**
 * Created by Afun on 2019/10/7.
 */

public class DialogPassword extends Dialog {
    public interface Callback {
        void onSucess();

        void onError(String msg);
    }

    private EditText etPassword;
    private Callback callback;

    public DialogPassword(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    public DialogPassword(@NonNull Context context, Callback callback) {
        this(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_password);
        setCanceledOnTouchOutside(false);
        etPassword = findViewById(R.id.et_password);
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_DONE == actionId) {
                    ok();
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok();
            }
        });
        showSoftInputFromWindow(etPassword);
    }

    private void ok() {
        if (!TextUtil.checkIsInput(etPassword)) {
            UIKit.toastShort("请输入密码");
            return;
        }
        Setting setting = XLContext.getSettingData();
        if (setting!= null) {
            String password =setting.getOpPsd();
            if (callback != null) {
                if (password.equals(TextUtil.getEditText(etPassword))) {
                    dismiss();
                    callback.onSucess();
                    //保存输入密码的最后时间
                    XLContext.config.save("passwordTime",System.currentTimeMillis());
                } else {
                    callback.onError("密码错误");
                }
            }
        }
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        int screenWidth = ScreenUtil.getScreenWidth(getContext());
        if(screenWidth > 1500){
            screenWidth -= 500;
        }else  if(screenWidth > 1000){
            screenWidth -= 300;
        }else  if(screenWidth > 500){
            screenWidth -= 200;
        }else{
            screenWidth -= 50;
        }
        layoutParams.width = screenWidth;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
    }

    public void showSoftInputFromWindow(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }
}
