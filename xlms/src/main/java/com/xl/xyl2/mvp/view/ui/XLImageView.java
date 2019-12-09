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
 * 展示图片
 */
public class XLImageView extends BaseSliderView<PlayUnit> {
    private boolean isShowDescription;

    /**
     * 是否显示描述
     * @param showDescription
     */
    public void setShowDescription(boolean showDescription) {
        isShowDescription = showDescription;
    }

    public XLImageView(Context context) {
        super(context);
    }


    @Override
    public View getView() {
        final View  v  = LayoutInflater.from(getContext()).inflate(R.layout.render_type_text,null);
        final ImageView target = v.findViewById(R.id.daimajia_slider_image);
        target.setScaleType(ImageView.ScaleType.FIT_XY);
        if(!isShowDescription){
            TextView description =v.findViewById(R.id.description);
            description.setVisibility(View.GONE);
            v.findViewById(R.id.description_layout).setVisibility(View.GONE);
        }
        v.findViewById(R.id.loading_bar).setVisibility(View.GONE);
        Glide.with(getContext())
                .load(new File(FileUtils.getDownloadDir().getPath()+"/"+mBundle.getSourceId()))
                .listener(new RequestListener<File, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (v.findViewById(R.id.loading_bar) != null) {
                            v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (v.findViewById(R.id.loading_bar) != null) {
                            v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                        }
                        return false;
                    }
                })
                .thumbnail(0.5f)
                .dontAnimate()
                .into(target);
        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSliderClick(XLImageView.this,mBundle);
            }
        });
        return  v;
    }

    @Override
    public void onSliderClick(BaseSliderView slider, PlayUnit data) {

    }


}
