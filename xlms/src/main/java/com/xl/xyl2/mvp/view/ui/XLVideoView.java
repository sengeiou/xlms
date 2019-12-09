package com.xl.xyl2.mvp.view.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.play.PlayUnit;
import com.xl.xyl2.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afun on 2019/9/12.
 */

public class XLVideoView extends SurfaceView implements MediaPlayer.OnVideoSizeChangedListener, android.view.GestureDetector.OnGestureListener {
    public interface PlayCallback {
        void onInit(String path);

        void onPlay();
    }

    MediaPlayer mediaPlayer;

    // 视频宽度
    private int videoWidth;
    // 视频高度
    private int videoHeight;
    volatile boolean isCreate;
    GestureDetector detector;
    List<PlayUnit> units;
    int currentPos = 0;
    PlayCallback playCallback;
    private Context context;
    private boolean isLive;

    public XLVideoView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(context);
    }

    public XLVideoView(Context context, PlayUnit unit) {
        this(context);
        ArrayList<PlayUnit> arrayList = new ArrayList<>();
        arrayList.add(unit);
        updateDatas(arrayList);

    }
    public XLVideoView(Context context, List<PlayUnit> units) {
        this(context);
        updateDatas(units);
    }

    public XLVideoView(Context context, PlayUnit unit,boolean isLive) {
        this(context);
        this.isLive = isLive;
        ArrayList<PlayUnit> arrayList = new ArrayList<>();
        arrayList.add(unit);
        updateDatas(arrayList);
    }

    public void setPlayCallback(PlayCallback playCallback) {
        this.playCallback = playCallback;
    }

    /**
     * 更新数据
     *
     * @param units
     */
    public void updateDatas(List<PlayUnit> units) {
        currentPos = 0;
        this.units = units;
        if (units != null && units.size() > 0) {
            mediaPlayer.setLooping(units.size() == 1);//单个设置循环播放
            PlayUnit unit = units.get(currentPos);//播放第一个
            play(unit);
        }
    }


    public XLVideoView(final Context context) {
        super(context);
        init(context);

    }

    private void init(Context context){
        if(this.context != null){
            return;
        }
        this.context = context;
        videoWidth = 0;
        videoHeight = 0;
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setKeepScreenOn(true);
        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                mediaPlayer.setDisplay(holder);
                if(mediaPlayer.isPlaying()){
                    current = mediaPlayer.getCurrentPosition();
                    currentPos--;
                    playNextOrPre(true);
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //播放器初始化完成
                //mediaPlayer.setDisplay(getHolder());
                if(current > 0){
                    mediaPlayer.seekTo(current);
                    current = 0;
                }
                mediaPlayer.start();
                try {
                    Thread.sleep(100);
                    if (playCallback != null) {
                        playCallback.onPlay();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        detector = new GestureDetector(context, this);
    }


    public void play(boolean isPlay){
        if(mediaPlayer!= null){
            if(isPlay){
                mediaPlayer.start();
            }else{
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
            }
        }
    }
    /**
     * 播放
     */
    public void play(PlayUnit unit) {
        String path;
        if(isLive){
            path = unit.getSourceUrl();
        }else{
            path = unit.getSourceId();
            if (!path.contains(FileUtils.getRootFile().getPath())) {
                path = FileUtils.getDownloadDir().getPath() + "/" + path;
            }
        }

        if (playCallback != null) {
            playCallback.onInit(path);
        }
        if (mediaPlayer == null) {
            return;
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, Uri.parse(path));
            if(isLive){
                mediaPlayer.prepareAsync();
            }else{
                mediaPlayer.prepare();
            }
            int vol = getVol(unit.getVol());
            mediaPlayer.setVolume(vol,vol);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        }


    }


    private int current = 0;
    /**
     * 播放上一个或下一个
     *
     * @param isNext true 播放下一个 false 播放上一个
     */
    private void playNextOrPre(boolean isNext) {
        if (units == null || units.size() == 0) {
            return;
        }
        if (isNext) {
            currentPos++;
            if (currentPos >= units.size()) {
                currentPos = 0;
            }
        } else {
            currentPos--;
            if (currentPos < 0) {
                currentPos = units.size() - 1;
            }
        }
        PlayUnit unit = units.get(currentPos);
        play(unit);
    }


    /**
     * 根据百分百获取音量
     * @param vol
     * @return
     */
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
     * 根据视频的宽高设置SurfaceView的宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);
        if (videoWidth > 0 && videoHeight > 0) {
            // 获取测量模式和测量大小
            int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
            // 分情况设置大小
            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // layout_width = 确定值或match_parent
                // layout_height = 确定值或match_parent
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;
                // 做适配，不让视频拉伸，保持原来宽高的比例
                // for compatibility, we adjust size based on aspect ratio
                if (videoWidth * height < width * videoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * videoWidth / videoHeight;
                } else if (videoWidth * height > width * videoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * videoHeight / videoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // layout_width = 确定值或match_parent
                // layout_height = wrap_content
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                // 计算高多少，保持原来宽高的比例
                height = width * videoHeight / videoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // layout_width = wrap_content
                // layout_height = 确定值或match_parent
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                // 计算宽多少，保持原来宽高的比例
                width = height * videoWidth / videoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // layout_width = wrap_content
                // layout_height = wrap_content
                // neither the width nor the height are fixed, try to use actual video size
                width = videoWidth;
                height = videoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        // 设置SurfaceView的宽高
        setMeasuredDimension(width, height);
    }

    /**
     * 调整大小
     *
     * @param videoWidth
     * @param videoHeight
     */
    public void adjustSize(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) return;
        // 赋值自己的宽高
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        // 设置Holder固定的大小
        getHolder().setFixedSize(videoWidth, videoHeight);
        // 重新设置自己的大小
        requestLayout();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        adjustSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        XLContext.config.save("sourceId",e.getSource());
        super.onTouchEvent(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float minMove = 120; // 最小滑动距离
        float minVelocity = 0; // 最小滑动速度
        float beginX = e1.getX();
        float endX = e2.getX();
        float beginY = e1.getY();
        float endY = e2.getY();

        if (beginX - endX > minMove && Math.abs(velocityX) > minVelocity) { // 左滑
            UIKit.dLog("左滑");
            //playNextOrPre(true);
        } else if (endX - beginX > minMove && Math.abs(velocityX) > minVelocity) { // 右滑
            UIKit.dLog("右滑");
            //playNextOrPre(false);
        } else if (beginY - endY > minMove && Math.abs(velocityY) > minVelocity) { // 上滑
            UIKit.dLog("上滑");
        } else if (endY - beginY > minMove && Math.abs(velocityY) > minVelocity) { // 下滑
            UIKit.dLog("下滑");
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
