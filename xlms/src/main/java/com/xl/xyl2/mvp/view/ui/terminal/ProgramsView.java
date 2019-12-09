package com.xl.xyl2.mvp.view.ui.terminal;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.gdswlw.library.toolkit.ScreenUtil;
import com.gdswlw.library.toolkit.StrUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.R;
import com.xl.xyl2.bean.Loc;
import com.xl.xyl2.mvp.view.ui.DifferentDislay;
import com.xl.xyl2.mvp.view.ui.ScrollTextView;
import com.xl.xyl2.mvp.view.ui.XLImageContainer;
import com.xl.xyl2.mvp.view.ui.XLVideoStreamContainer;
import com.xl.xyl2.mvp.view.ui.XLVideoView;
import com.xl.xyl2.mvp.view.ui.XLWeather;
import com.xl.xyl2.mvp.view.ui.webview.XLWebView;
import com.xl.xyl2.play.DeviceTerminal;
import com.xl.xyl2.play.LogsProcessor;
import com.xl.xyl2.play.PlayArea;
import com.xl.xyl2.play.PlayList;
import com.xl.xyl2.play.PlayProgram;
import com.xl.xyl2.play.PlayState;
import com.xl.xyl2.play.PlayUnit;
import com.xl.xyl2.play.Processor;
import com.xl.xyl2.play.ProgramChangeCallBack;
import com.xl.xyl2.utils.BDLocationUtil;

import java.util.List;


/**
 * 节目视图
 * Created by Afun on 2019/9/12.
 */

public class ProgramsView extends FrameLayout {
    private int terminalId;

    public void setTerminalId(int terminalId) {
        this.terminalId = terminalId;
    }

    private int screenWidth, screenHeight;//播放区域
    double scaleValue = 1;
    /**
     * 默认一个节目的最大播放时间
     */
    private final int defaultPlayTime = 30;

    /**
     * 获取当前节目最大的播放时间
     *
     * @return
     */
    private int getMaxPlaytime() {
        int duration = 0;
        if (processor.getCurProgram() != null) {
            PlayProgram playProgram = processor.getCurProgram();
            if (playProgram != null) {
                List<PlayArea> playAreas = playProgram.getAreas();
                for (int i = 0; i < playAreas.size(); i++) {
                    int times = 0;
                    PlayArea playArea = playAreas.get(i);
                    List<PlayUnit> playUnits = playArea.getUnits();
                    if (playUnits != null) {
                        for (int j = 0; j < playUnits.size(); j++) {
                            PlayUnit playUnit = playUnits.get(j);
                            times += playUnit.getDuration();
                        }
                    }
                    if (times > duration) {//比较出最大值
                        duration = times;
                    }
                }

            }
        }
        if (duration == 0) {
            duration = defaultPlayTime;
        }
        return duration;
    }

    private PlayList playList;
    private int terminalScreenWidth, terminalScreenHeight;
    private Handler handler;



    /**
     * @param context
     * @param terminalData  终端数据
     */
    public ProgramsView(@NonNull final Context context,DeviceTerminal terminalData) {
        super(context);
        this.terminalId = terminalData.getId();
        playState = new PlayState();//记录播放状态
        //计算屏幕宽高相对于界面宽高的比例值

//        LayoutTransition transitioner = new LayoutTransition();
//        ObjectAnimator animIn = ObjectAnimator.ofFloat(this, "alpha", 0, 1);
//        ObjectAnimator animOut = ObjectAnimator.ofFloat(this, "alpha", 1, 0);
//        transitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);
//        transitioner.setAnimator(LayoutTransition.APPEARING, animIn);
//        setLayoutTransition(transitioner);
        setBackgroundColor(getResources().getColor(R.color.app_blue));
        update(terminalData.getPlayList(), terminalData.getScreenWidth(), terminalData.getScreenHeight());

    }

    public void update(@NonNull PlayList playList, final int terminalScreenWidth, final int terminalScreenHeight) {
        XLWebView.hookWebView();
        this.terminalScreenHeight = terminalScreenHeight;
        this.terminalScreenWidth = terminalScreenWidth;

        this.playList = playList;
        if (processor == null) {
            processor = new Processor();
            processor.init(new ProgramChangeCallBack() {
                @Override
                public boolean change(PlayProgram newProgram, PlayProgram oldProgram) {
                    if(newProgram == null){
                        return false;
                    }
                    //日志处理
                    LogsProcessor.handleLogs(newProgram,terminalId,getMaxPlaytime());

                    acvtiveTimer();//激活定时器
                    playState.setStartPlayTime(System.currentTimeMillis());//开始播放时间
                    if (oldProgram != null && oldProgram.getIdentification().equals(newProgram.getIdentification())) {
                        return false;
                    }
                    double areaWidthScale = (Math.round(ProgramsView.this.terminalScreenWidth) * 100 / newProgram.getWidth() / 100.0);
                    double areaHeightScale = (Math.round(ProgramsView.this.terminalScreenHeight * 100 / newProgram.getHeight()) / 100.0);
                    scaleValue = areaWidthScale > areaHeightScale ? areaHeightScale : areaWidthScale;
                    loadProgram(newProgram);
                    return true;
                }
            });
        }
        processor.setPlayList(playList);
    }

    Processor processor;


    /**
     * 加载节目
     *
     * @param program
     */
    private void loadProgram(@NonNull PlayProgram program) {
        if (program == null) {
            return;
        }
        setBackgroundColor(Color.parseColor(program.getBgColor()));
        removeAllViews();//移除所有视图
        //添加区域
        List<PlayArea> playAreas = program.getAreas();
        for (int i = 0; i < playAreas.size(); i++) {
            PlayArea area = playAreas.get(i);
            addViewByArea(area);
        }

        //----------------------------------------------------调试用---------------------------------------------------------------------
//        TextView textView = new TextView(getContext());
//        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        textView.setLayoutParams(layoutParams);
//        textView.setBackgroundColor(getResources().getColor(R.color.app_blue));
//        textView.setText("Debug 当前:"+program.getIdentification()+"，时长："+getMaxPlaytime());
//        addView(textView);
//
//        List<PlayProgram> ids = processor.getIdfList();
//        if(ids != null){
//            for (int i = 0; i < ids.size(); i++) {
//                final PlayProgram id = ids.get(i);
//                TextView textViewTemp = new TextView(getContext());
//                FrameLayout.LayoutParams layoutParamsTemp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//                textViewTemp.setLayoutParams(layoutParamsTemp);
//                textViewTemp.setY((i+1)*20);
//                textViewTemp.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                textViewTemp.setText("Debug 触发标识:"+id.getIdentification());
//                textViewTemp.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        processor.getNext(id.getIdentification());
//                    }
//                });
//                addView(textViewTemp);
//            }
//        }
//
//        List<PlayProgram> faces = processor.getFaceList();
//        if(ids != null){
//            for (int i = 0; i < faces.size(); i++) {
//                PlayProgram id = faces.get(i);
//                TextView textViewTemp = new TextView(getContext());
//                FrameLayout.LayoutParams layoutParamsTemp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//                textViewTemp.setY((i+1)*60);
//                textViewTemp.setLayoutParams(layoutParamsTemp);
//                textViewTemp.setBackgroundColor(getResources().getColor(R.color.red));
//                textViewTemp.setText("Debug 人脸:"+id.getIdentification()+"，年龄["+id.getAutoPlayFaceSetting().getsAge()+"-"+id.getAutoPlayFaceSetting().geteAge()+"],sex="+id.getAutoPlayFaceSetting().getSex());
//                textViewTemp.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        processor.getNext(0,15);
//                    }
//                });
//                addView(textViewTemp);
//            }
//        }
        //----------------------------------调试用--------------------------------------
        mutipleScreen();
    }

    private void acvtiveTimer(){
        int maxTime = getMaxPlaytime();
        if (handler == null) {
            handler = new Handler();
            changePrograms = new ChangePrograms();
        }
        handler.removeCallbacks(changePrograms);
        handler.postDelayed(changePrograms, maxTime * 1000);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mutipleScreen();
    }


    DifferentDislay mPresentation = null;

    /**
     * 多屏异显
     */
    private void mutipleScreen() {
        if (getParent() != null && getParent() instanceof TerminalView && ((TerminalView) getParent()).isMain()) {
            Display[] displays = ScreenUtil.getDisplays(getContext());
            if (displays.length > 1 &&  processor!= null && processor.getCurProgram() != null) {
                if (mPresentation == null) {
                    mPresentation = new DifferentDislay(getContext(),
                            displays[displays.length - 1], processor.getCurProgram().getLinkUnits());// displays[1]是副屏
                    mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    mPresentation.show();
                } else {
                    mPresentation.updateDatas(processor.getCurProgram().getLinkUnits());
                }
            }
        }
    }




    private ChangePrograms changePrograms;
    private PlayState playState;
    private class ChangePrograms implements Runnable {
        @Override
        public void run() {
            UIKit.dLog("准备切换下一个节目");
            post(new Runnable() {
                @Override
                public void run() {
                    processor.getNext();
                }
            });
        }
    }

    /**
     * 返回播放状态
     * @return
     */
    public PlayState getPlayState(){
        return playState;
    }

    public void play(boolean isPlay){
        if(isPlay){
             playState.setPause(false);
             //继续当前的节目切换线程的剩余时间
             handler.postDelayed(changePrograms,
                     getMaxPlaytime() * 1000 - (playState.getPuaseTime()-playState.getStartPlayTime()));
             playState.setStartPlayTime(System.currentTimeMillis());//重新设置开始时间
        }else{
            //暂停切换节目线程
            if(changePrograms != null){
                handler.removeCallbacks(changePrograms);
            }
            playState.setPause(true);
            playState.setPuaseTime(System.currentTimeMillis());
        }
        playView(isPlay);
    }

    /**
     * 播放视图
     * @param isPlay
     */
    private void playView(boolean isPlay){
        if(processor != null){
            processor.setPause(!isPlay);//暂停线程
        }
        for (int i= 0;i<getChildCount();i++){
            if(getChildAt(i) instanceof XLImageContainer){
                ((XLImageContainer)getChildAt(i)).play(isPlay);
            }else  if(getChildAt(i) instanceof XLVideoView){
                ((XLVideoView)getChildAt(i)).play(isPlay);
            }
        }
        if(mPresentation != null){
           mPresentation.play(isPlay);
        }
    }
    /**
     * 通过area数据添加到View
     *
     * @param area
     */
    public void addViewByArea(PlayArea area) {
        final Context context = getContext();
        if (area.getType() == 1 || area.getType() == 2) {//图片或视频
            List<PlayUnit> units = area.getUnits();
            switch (area.getType()) {
                case 1://图片视频
                case 2:
                    XLImageContainer xlImageContainer = new XLImageContainer(context, units);
                    xlImageContainer.setTag(area.getIdentification());
                    setWHXY(xlImageContainer, area);
                    UIKit.dLog("image width=" + "" + area.getWidth() * scaleValue + ",height=" + area.getHeight() * scaleValue + "scale=" + scaleValue);
                    addView(xlImageContainer);
                    break;
            }
        } else {
            //区域类型 1-图片 2-视频 3-时间日期 4-天气 5-网页 6-直播流 7-字幕 8文本
            switch (area.getType()) {
                case 4:
                    XLWeather xlWeather = new XLWeather(context);
                    setWHXY(xlWeather, area);
                    xlWeather.getWeather(area,scaleValue);
                    xlWeather.setTag(area.getIdentification());
                    addView(xlWeather);
                    break;
                case 3://日期
                case 8:
                    RelativeLayout textViewItem = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_textview,null);
                    View outer = textViewItem.findViewById(R.id.v_outer);
                    setWHXY(textViewItem, area);

                    TextView textView = null;
                    if (area.getType() == 3) {
                        textView = new TextClock(context);
                        ((TextClock) textView).setFormat24Hour("yyyy/MM/dd  HH:mm EE");
                    } else if (area.getType() == 8) {
                        textView = new TextView(context);
                        textView.setText(area.getText());
                    }
                    if (textView != null) {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, area.getFontSize());
                        textView.setTextColor(Color.parseColor(area.getFontColor()));
                    }

                    textViewItem.addView(textView);
                    textViewItem.setTag(area.getIdentification());
                    outer.setBackgroundColor(Color.parseColor(area.getBgColor()));
                    outer.setAlpha(area.getBgOpacity());
                    addView(textViewItem);
                    break;
                case 5://网页
                    XLWebView xlWebView = new XLWebView(context);
                    xlWebView.setTag(area.getIdentification());
                    setWHXY(xlWebView, area);
                    xlWebView.loadUrl(area.getUrl());
                    addView(xlWebView);
                    break;
                case 6://直播流
                    List<PlayUnit> playUnits = area.getUnits();
                    if(playUnits!= null &&playUnits.size() > 0 ){
                        XLVideoStreamContainer xlVideoStreamContainer = new XLVideoStreamContainer(context,playUnits);
                        xlVideoStreamContainer.setTag(area.getIdentification());
                        setWHXY(xlVideoStreamContainer, area);
                        addView(xlVideoStreamContainer);
                    }
                    break;
                case 7://字幕
                    RelativeLayout textViewItem2 = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_textview,null);
                    View outer2 = textViewItem2.findViewById(R.id.v_outer);
                    setWHXY(textViewItem2, area);

                    ScrollTextView scrollTextView = new ScrollTextView(context);
                    scrollTextView.setTextSize(area.getFontSize());
                    scrollTextView.setTextColor(Color.parseColor(area.getFontColor()));
                    scrollTextView.setText(area.getText());
                    scrollTextView.setTextSpeed(area.getSpeed());
                    textViewItem2.setTag(area.getIdentification());
                    textViewItem2.addView(scrollTextView);
                    outer2.setBackgroundColor(Color.parseColor(area.getBgColor()));
                    outer2.setAlpha(area.getBgOpacity());
                    addView(textViewItem2);
                    break;
            }
        }

    }

    public int getVol(int vol){
        if(vol <= 0){
            return  0;
        }
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = (int)(((vol * 1.0) /100) * maxVolume);
        return volume;
    }

    /**
     * 设置位置信息
     *
     * @param textView
     */
    private void setLocation(TextView textView) {
        //获取定位到的位置信息
        Loc location = BDLocationUtil.getInstance().getSerilizeLocationData();
        if (location != null && location.getCity() != null) {
            textView.setText(StrUtil.appendString("Location(", location.getLat(), ",", location.getLng(), "):", location.getCity(), " ", location.getArea()));
        } else {
            textView.setText("暂无位置信息");
        }
        textView.setTag(R.id.type_location, true);
    }

    /**
     * 更新位置信息
     */
    public void updateLocation() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view.getTag(R.id.type_location) != null && view instanceof TextView) {
                setLocation((TextView) view);
                break;
            }
        }
    }

    /**
     * 设置宽高与x y
     *
     * @param view
     * @param area
     */
    public void setWHXY(View view, PlayArea area) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (area.getWidth() * scaleValue),
                (int) (area.getHeight() * scaleValue));
        view.setX((int) (area.getX() * scaleValue));
        view.setY((int) (area.getY() * scaleValue));
        view.setLayoutParams(layoutParams);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (processor.getCurProgram() != null) {
            //重新设置x y坐标和宽高
            PlayProgram playProgram = processor.getCurProgram();
            screenWidth = (int) (playProgram.getWidth() * scaleValue);
            screenHeight = (int) (playProgram.getHeight() * scaleValue);
            setMeasuredDimension(screenWidth, screenHeight);
        }
    }

    /**
     * 获取播放屏幕宽度
     *
     * @return
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * 获取终端屏幕高度
     *
     * @return
     */
    public int getScreenWidth() {
        return screenWidth;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //取消定时器，释放资源
        if (handler != null) {
            handler.removeCallbacks(changePrograms);
        }
        if (processor != null) {
            processor.disposable();
        }

        if(mPresentation != null){
            mPresentation.cancel();
        }
    }

}
