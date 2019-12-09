package com.gdswlw.library.view.et;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.xl.xyl2.R;


public class FormCheckBox extends android.support.v7.widget.AppCompatCheckBox {
    private String formKey,value;
    private boolean ignore = false;

    public FormCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FormCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.FormAttr);
        setFormKey(array.getString(R.styleable.FormAttr_key));
        setValue(array.getString(R.styleable.FormAttr_value));
        setIgnore(array.getBoolean(R.styleable.FormAttr_ignore,false));
        array.recycle();
    }

    public FormCheckBox(Context context) {
        super(context);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = (value == null ? "noValue":value);
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = (formKey == null ? "noKey":formKey);
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}
