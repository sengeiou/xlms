package com.xl.xyl2.mvp.view.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderLayout.Transformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.xl.xyl2.play.PlayState;
import com.xl.xyl2.play.PlayUnit;

import java.util.HashMap;
import java.util.List;

/**
 * 视频流容器
 * Created by Afun on 2019/9/17.
 */

public class XLVideoStreamContainer extends RelativeLayout {
    PlayState playState;
    /**
     * 动画效果
     */
    private HashMap<String, Transformer> transformers = new HashMap<>();

    public XLVideoStreamContainer(Context context) {
        super(context);
        init(context);
    }

    public XLVideoStreamContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private SliderLayout sliderLayout;

    public XLVideoStreamContainer(Context context, List<PlayUnit> units) {
        super(context);
        playState = new PlayState();
        init(context);
        loadByDatas(units);
    }

    private void init(Context context) {
        transformers.put("Default", Transformer.Default);
        transformers.put("Accordion", Transformer.Accordion);
        transformers.put("Background2Foreground", Transformer.Background2Foreground);
        transformers.put("CubeIn", Transformer.CubeIn);
        transformers.put("DepthPage", Transformer.DepthPage);
        transformers.put("Fade", Transformer.Fade);
        transformers.put("FlipHorizontal", Transformer.FlipHorizontal);
        transformers.put("FlipPage", Transformer.FlipPage);
        transformers.put("Foreground2Background", Transformer.Foreground2Background);
        transformers.put("RotateDown", Transformer.RotateDown);
        transformers.put("RotateUp", Transformer.RotateUp);
        transformers.put("Stack", Transformer.Stack);
        transformers.put("Tablet", Transformer.Tablet);
        transformers.put("ZoomIn", Transformer.ZoomIn);
        transformers.put("ZoomOutSlide", Transformer.ZoomOutSlide);
        transformers.put("ZoomOut", Transformer.ZoomOut);
        sliderLayout = new SliderLayout(context);
        sliderLayout.setPresetTransformer(Transformer.Default);//默认动画效果
        sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        addView(sliderLayout);
    }

    XLVideoView videoView;
    List<PlayUnit> units;

    /**
     * 加载图片数据
     *
     * @param units
     */
    public void loadByDatas(final List<PlayUnit> units) {
        this.units = units;
        sliderLayout.removeAllSliders();
        sliderLayout.stopAutoCycle();
        if (units != null) {
            for (int j = 0; j < units.size(); j++) {
                PlayUnit unit = units.get(j);
                XLVideoLoadingPlace xlVideoLoadingPlace = new XLVideoLoadingPlace(getContext());
                xlVideoLoadingPlace.bundle(unit);
                sliderLayout.addSlider(xlVideoLoadingPlace);
            }

            //添加滑动监听器
            sliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (videoView != null) {
                        removeView(videoView);
                        videoView = null;
                    }
                    current = position;
                    final PlayUnit unit = units.get(position);
                    //直播流
                    videoView = new XLVideoView(getContext(), unit,true);
                    LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    videoView.setLayoutParams(layoutParams);
                    addView(videoView);
                    sliderLayout.startAutoCycle();
                    sliderLayout.setDuration(unit.getDuration() * 1000);
                    if (playState == null) {
                        playState = new PlayState();
                    }

                    unit.setStartTime(System.currentTimeMillis());
                    String animate = unit.getAnimate();
                    if (!transformers.containsKey(animate)) {
                        animate = "Default";
                    }
                    sliderLayout.setPresetTransformer(transformers.get(animate));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            sliderLayout.setCurrentPosition(0);
        }
    }


    private int current;


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (sliderLayout != null) {
            sliderLayout.removeAllSliders();
            sliderLayout.stopAutoCycle();
        }
    }


    /**
     * 播放或暂停
     *
     * @param isPlay
     */
    public void play(boolean isPlay) {
        if (!(sliderLayout != null && sliderLayout.isAttachedToWindow())) {
            return;
        }
        if (playState == null) {
            playState = new PlayState();
        }
        PlayUnit unit = units.get(current);
        playState.setPause(!isPlay);
        if (isPlay) {
            if (videoView != null && videoView.isAttachedToWindow()) {
                videoView.mediaPlayer.start();
                sliderLayout.setDuration(unit.getDuration() * 1000 - (unit.getPuaseTime() - unit.getStartTime()));//图片剩余时间
                sliderLayout.startAutoCycle();
                playState.setStartPlayTime(System.currentTimeMillis());
            }
        } else {
            if (videoView != null && videoView.isAttachedToWindow()) {
                videoView.mediaPlayer.pause();
            }
            sliderLayout.stopAutoCycle();
            unit.setPuaseTime(System.currentTimeMillis());
        }

    }

}
