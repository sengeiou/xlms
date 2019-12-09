package com.daimajia.slider.library.SliderTypes;

import android.content.Context;
import android.view.View;


/**
 * When you want to make your own slider view, you must extends from this class.
 * BaseSliderView provides some useful methods.
 * if you want to show progressbar, you just need to set a progressbar id as @+id/loading_bar.
 */
public abstract class BaseSliderView<T> {

    protected Context mContext;

    protected T mBundle;

    private boolean mErrorDisappear;

    protected BaseSliderView(Context context) {
        mContext = context;
    }


    /**
     * determine whether remove the image which failed to download or load from file
     *
     * @param disappear
     * @return
     */
    public BaseSliderView errorDisappear(boolean disappear) {
        mErrorDisappear = disappear;
        return this;
    }

    /**
     * lets users add a bundle of additional information
     *
     * @param bundle
     * @return
     */
    public BaseSliderView bundle(T bundle) {
        mBundle = bundle;
        return this;
    }

    public boolean isErrorDisappear() {
        return mErrorDisappear;
    }

    public Context getContext() {
        return mContext;
    }


    /**
     * When you want to implement your own slider view, please call this method in the end in `getView()` method
     *
     * @param v      the whole view
     */
    public void bindEventAndShow(final View v) {
        final BaseSliderView me = this;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onSliderClick(me, mBundle);
            }
        });
    }


    /**
     * the extended class have to implement getView(), which is called by the adapter,
     * every extended class response to render their own view.
     *
     * @return
     */
    public abstract View getView();

    public abstract void onSliderClick(BaseSliderView slider, T mbundle);

    private ImageLoadListener mLoadListener;
    /**
     * set a listener to get a message , if load error.
     *
     * @param l
     */
    public void setOnImageLoadListener(ImageLoadListener l) {
        mLoadListener = l;
    }

    /**
     * when you have some extra information, please put it in this bundle.
     *
     * @return
     */
    public T getBundle() {
        return mBundle;
    }

    public interface ImageLoadListener {
        public void onStart(BaseSliderView target);

        public void onEnd(boolean result, BaseSliderView target);
    }
}
