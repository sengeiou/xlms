package com.gdswlw.library.adapter.viewholders;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * ViewHolder操作子视图的实现类
 */
public class ViewHolderImpl {

    /**
     * 缓存子视图,key为view id, 值为View。
     */
    private SparseArray<View> mCahceViews = new SparseArray<View>();
    /**
     * Item View
     */
    View mItemView;

    /**
     * @param itemView
     */
    ViewHolderImpl(View itemView) {
        mItemView = itemView;
    }

    public View getItemView() {
        return mItemView;
    }


    /**
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T view(int viewId) {
        View target = mCahceViews.get(viewId);
        if (target == null) {
            target = mItemView.findViewById(viewId);
            mCahceViews.put(viewId, target);
        }
        return (T) target;
    }
    public void setText(int viewId, int stringId) {
        TextView textView = view(viewId);
        textView.setText(stringId);
    }

    public void setText(int viewId, String text) {
        TextView textView = view(viewId);
        textView.setText(text);
    }

    public void setTextColor(int viewId, int color) {
        TextView textView = view(viewId);
        textView.setTextColor(color);
    }

    /**
     * @param viewId
     * @param color
     */
    public void setBackgroundColor(int viewId, int color) {
        View target = view(viewId);
        target.setBackgroundColor(color);

    }


    /**
     * @param viewId
     * @param resId
     */
    public void setBackgroundResource(int viewId, int resId) {
        View target = view(viewId);
        target.setBackgroundResource(resId);
    }


    /**
     * @param viewId
     * @param drawable
     */
    public void setBackgroundDrawable(int viewId, Drawable drawable) {
        View target = view(viewId);
        target.setBackgroundDrawable(drawable);
    }

    /**
     * @param viewId
     * @param drawable
     */
    @TargetApi(16)
    public void setBackground(int viewId, Drawable drawable) {
        View target = view(viewId);
        target.setBackground(drawable);
    }


    public void setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView target = view(viewId);
        target.setImageBitmap(bitmap);
    }


    public void setImageResource(int viewId, int resId) {
        ImageView target = view(viewId);
        target.setImageResource(resId);
    }


    public void setImageDrawable(int viewId, Drawable drawable) {
        ImageView target = view(viewId);
        target.setImageDrawable(drawable);
    }


    public void setImageDrawable(int viewId, Uri uri) {
        ImageView target = view(viewId);
        target.setImageURI(uri);
    }


    @TargetApi(16)
    public void setImageAlpha(int viewId, int alpha) {
        ImageView target = view(viewId);
        target.setImageAlpha(alpha);
    }

    public void setChecked(int viewId, boolean checked) {
        Checkable checkable = view(viewId);
        checkable.setChecked(checked);
    }


    public void setProgress(int viewId, int progress) {
        ProgressBar view = view(viewId);
        view.setProgress(progress);
    }

    public void setProgress(int viewId, int progress, int max) {
        ProgressBar view = view(viewId);
        view.setMax(max);
        view.setProgress(progress);
    }

    public void setMax(int viewId, int max) {
        ProgressBar view = view(viewId);
        view.setMax(max);
    }

    public void setRating(int viewId, float rating) {
        RatingBar view = view(viewId);
        view.setRating(rating);
    }

    public void setVisibility(int viewId, int visible) {
        View view = view(viewId);
        view.setVisibility(visible);
    }


    public void setRating(int viewId, float rating, int max) {
        RatingBar view = view(viewId);
        view.setMax(max);
        view.setRating(rating);
    }

    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = view(viewId);
        view.setOnClickListener(listener);

    }

    public void setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = view(viewId);
        view.setOnTouchListener(listener);
    }


    public void setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = view(viewId);
        view.setOnLongClickListener(listener);
    }

    public void setOnItemClickListener(int viewId, AdapterView.OnItemClickListener listener) {
        AdapterView view = view(viewId);
        view.setOnItemClickListener(listener);
    }


    public void setOnItemLongClickListener(int viewId, AdapterView.OnItemLongClickListener listener) {
        AdapterView view = view(viewId);
        view.setOnItemLongClickListener(listener);
    }


    public void setOnItemSelectedClickListener(int viewId, AdapterView.OnItemSelectedListener listener) {
        AdapterView view = view(viewId);
        view.setOnItemSelectedListener(listener);
    }
}
