package com.gdswlw.library.view.et;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.xl.xyl2.R;


/**
 * Created by SHZ on 2016/9/26.
 */
public class FormRG extends RadioGroup{
    private boolean ignore = false;
    private String key;
    public FormRG(Context context) {
        super(context);
    }

    public FormRG(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.FormAttr);
        setKey(array.getString(R.styleable.FormAttr_key));
        setIgnore(array.getBoolean(R.styleable.FormAttr_ignore,false));
        array.recycle();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = (key == null ? "noKey":key);
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}
