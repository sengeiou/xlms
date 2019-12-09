package com.xl.xyl2.widget;

/**
 * Created by Afun on 2019/10/8.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xl.xyl2.R;


/**
 * ItemMore
 */
public final class ItemMore extends LinearLayout {

    private TextView tvItemText,tvItemDesText;
    public ItemMore(Context context) {
        super(context);
        initView();
    }

    public ItemMore(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ItemMore);
        itemText(typedArray.getString(R.styleable.ItemMore_itemText));
        itemDesText(typedArray.getString(R.styleable.ItemMore_itemDesText));
        if(typedArray.getResourceId(R.styleable.ItemMore_itemIcon,-1) > 0){
            icon(typedArray.getResourceId(R.styleable.ItemMore_itemIcon,-1));
        }
        typedArray.recycle();
    }
    private void initView(){
        setClickable(true);
        LayoutInflater.from(getContext()).inflate(R.layout.item_more_layout, this);
        tvItemText = findViewById(R.id.tv_item_text);
        tvItemDesText = findViewById(R.id.tv_item_des_text);
    }

    /**
     * set the text for tabButton
     * @param text
     * @return
     */
    public ItemMore itemText(@NonNull String text){
        tvItemText.setText(text);
        return this;
    }

    public ItemMore itemDesText(@NonNull String text){
        tvItemDesText.setText(text);
        return this;
    }
    public ItemMore itemDesTextNoDrawbleMore(@NonNull String text){
        tvItemDesText.setText(text);
        tvItemDesText.setCompoundDrawables(null,null,null,null);
        return this;
    }
    public String itemDesText(){
        return tvItemDesText.getText().toString();
    }

    /**
     * set the icon for tabButton
     * @param drawableId
     * @return
     */
    public ItemMore icon (@DrawableRes int drawableId){
        setDrawableLeft(tvItemText,drawableId);
        return this;
    }


    public ItemMore colorItem(@ColorRes int colorRes){
        tvItemText.setTextColor(ContextCompat.getColor(getContext(),colorRes));
        return this;
    }

    public ItemMore colorDesItem(@ColorRes int colorRes){
        tvItemDesText.setTextColor(ContextCompat.getColor(getContext(),colorRes));
        return this;
    }


    /**
     * set drawableTop for textView
     * @param textView
     * @param drawableId
     */
    private void setDrawableLeft(TextView textView,int drawableId){
        Drawable left = ContextCompat.getDrawable(getContext(),drawableId);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        textView.setCompoundDrawables(left, null, null, null);
    }
}
