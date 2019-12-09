package com.xl.xyl2.mvp.view.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.xl.xyl2.R;
import com.xl.xyl2.play.PlayUnit;
import com.xl.xyl2.utils.FileUtils;

import java.io.File;

/**
 * 直播流占位符
 */
public class XLVideoLoadingPlace extends BaseSliderView<PlayUnit> {
    private boolean isShowDescription;

    /**
     * 是否显示描述
     * @param showDescription
     */
    public void setShowDescription(boolean showDescription) {
        isShowDescription = showDescription;
    }

    public XLVideoLoadingPlace(Context context) {
        super(context);
    }


    @Override
    public View getView() {
        final View  v  = LayoutInflater.from(getContext()).inflate(R.layout.video_place_holder,null);
        return  v;
    }

    @Override
    public void onSliderClick(BaseSliderView slider, PlayUnit data) {

    }


}
