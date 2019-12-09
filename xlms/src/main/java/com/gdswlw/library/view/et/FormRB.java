package com.gdswlw.library.view.et;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.xl.xyl2.R;


/**
 * Created by SHZ on 2016/9/26.
 */
public class FormRB extends android.support.v7.widget.AppCompatRadioButton{
    private String value;
    public FormRB(Context context) {
        super(context);
    }

    public FormRB(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.FormAttr);
        setValue(array.getString(R.styleable.FormAttr_value));
        array.recycle();
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
