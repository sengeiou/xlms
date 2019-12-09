package com.xl.xyl2.mvp.view.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderLayout.Transformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.gdswlw.library.toolkit.FileUtil;
import com.xl.xyl2.R;
import com.xl.xyl2.play.PlayState;
import com.xl.xyl2.play.PlayUnit;
import com.xl.xyl2.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 图片容器
 * Created by Afun on 2019/9/17.
 */

public class XLImageContainer extends RelativeLayout {
    PlayState playState;
    /**
     * 动画效果
     */
    private HashMap<String, Transformer> transformers = new HashMap<>();

    public XLImageContainer(Context context) {
        super(context);
        init(context);
    }

    public XLImageContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private SliderLayout sliderLayout;

    public XLImageContainer(Context context, List<PlayUnit> units) {
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
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);//默认动画效果
        sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        addView(sliderLayout);
    }

    XLVideoView  videoView;
    List<PlayUnit> units;

    /**
     * 加载图片数据
     *
     * @param units
     */
    public void loadByDatas(final List<PlayUnit> units) {
        this.units = units;
        sliderLayout.removeAllSliders();
        if (units != null) {
            for (int j = 0; j < units.size(); j++) {
                PlayUnit unit = units.get(j);
                XLImageView xlImageView = new XLImageView(getContext());
                xlImageView.bundle(unit);
                sliderLayout.addSlider(xlImageView);
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
                    //视频层
                    if (unit.getSourceType() == 2) {
                        videoView = new XLVideoView (getContext(),unit);
                        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        videoView.setLayoutParams(layoutParams);
                        addView(videoView);
                        videoView.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                sliderLayout.moveNextPosition();
                            }
                        });
                        sliderLayout.stopAutoCycle();
                    }else{
                        sliderLayout.startAutoCycle();
                        sliderLayout.setDuration(unit.getDuration() * 1000);//重新设置播放时长
                    }
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
        };
    }


    private int current;

    public int getVol(int vol) {
        if (vol <= 0) {
            return 0;
        }
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = (int) (((vol * 1.0) / 100) * maxVolume);
        return volume;
    }

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
                sliderLayout.setDuration(videoView.mediaPlayer.getDuration() - videoView.mediaPlayer.getCurrentPosition());//视频剩余时间
                videoView.mediaPlayer.start();
            } else {
                sliderLayout.setDuration(unit.getDuration() * 1000 - (unit.getPuaseTime() - unit.getStartTime()));//图片剩余时间
            }
            sliderLayout.startAutoCycle();
            playState.setStartPlayTime(System.currentTimeMillis());
        } else {
            if (videoView != null && videoView.isAttachedToWindow()) {
                videoView.mediaPlayer.pause();
            }
            sliderLayout.stopAutoCycle();
            unit.setPuaseTime(System.currentTimeMillis());
        }

    }

}
