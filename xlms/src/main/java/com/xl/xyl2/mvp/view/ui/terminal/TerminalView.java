package com.xl.xyl2.mvp.view.ui.terminal;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.R;
import com.xl.xyl2.download.DownLoadCallBack;
import com.xl.xyl2.download.DownLoadManager;
import com.xl.xyl2.play.Data;
import com.xl.xyl2.play.DeviceTerminal;
import com.xl.xyl2.play.PlayList;
import com.xl.xyl2.play.PlayState;
import com.xl.xyl2.utils.FileUtils;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 终端视图
 * Created by Afun on 2019/9/12.
 */

public class TerminalView extends FrameLayout {
    /**
     * 终端数据
     */
    private DeviceTerminal terminalData;
    /**
     * 播放列表
     */
    private PlayList playList;
    /**
     * 屏幕宽高
     */
    private int screenWidth, screenHeight;
    /**
     * 播放列表唯一标识符
     */
    private String identification;
    private ProgramsView programsView;
    private View progressView;
    private ProgressBar progressBar;
    private TerminalHandler handler;
    private boolean isMain;


    public boolean isMain() {
        return isMain;
    }

    /**
     * 是否已添加播放
     *
     * @return
     */
    public boolean isPlay() {
        return programsView != null && (programsView.getPlayState() != null && !programsView.getPlayState().isPause());
    }


    public void play(boolean isPlay) {
        removeView(noProgramasView);
        if (programsView != null && programsView.isAttachedToWindow()) {
            programsView.play(isPlay);
            if (isPlay) {
                //判断是否更新节目了
                PlayState playState = programsView.getPlayState();
                if (playState.isUpdate()) {
                    playState.setUpdate(false);
                    loadView(true);//更新视图
                }
                noProgramasView.setText("");
            } else {
                noProgramasView.setText(getContext().getResources().getString(R.string.jmtzz));
                addView(noProgramasView);
            }
        }

    }

    public TerminalView(@NonNull Context context, boolean isMain) {
        super(context);
        this.isMain = isMain;
        setBackgroundColor(Color.parseColor("#185186"));
    }


    public String getIdentification() {
        return identification;
    }

    /**
     * 是否设置了x y坐标和宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (terminalData != null) {
            //重新设置x y坐标和宽高
            setX(terminalData.getScreenX());
            setY(terminalData.getScreenY());
            setMeasuredDimension(screenWidth, screenHeight);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(screenWidth, screenHeight);
            noProgramasView.setLayoutParams(layoutParams);
            noProgramasView.setGravity(Gravity.CENTER);
        }
    }

    /**
     * 获取终端屏幕宽度
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

    private TextView noProgramasView;

    /**
     * 加载终端数据
     *
     * @param terminalData
     */
    public void loadTerminalData(final DeviceTerminal terminalData, boolean isUpdate) {
        this.terminalData = terminalData;
        screenWidth = terminalData.getScreenWidth();
        screenHeight = terminalData.getScreenHeight();
        requestLayout();

        noProgramasView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_no_programs, null);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        noProgramasView.setLayoutParams(layoutParams);


        handler = new TerminalHandler(this);

        //更新进度提示
        progressView = LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_bar, null);
        FrameLayout.LayoutParams progressViewLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        progressViewLayoutParams.gravity = Gravity.TOP;
        progressView.setLayoutParams(progressViewLayoutParams);
        progressBar = progressView.findViewById(R.id.pbProgress);

        if (terminalData == null || terminalData.getPlayList() == null) {
            addView(noProgramasView);
            noProgramasView.setText(getResources().getString(R.string.no_program));
            return;
        }

        playList = terminalData.getPlayList();//获取播放列表
        identification = playList.getIdentification();//获取播放列表唯一标识
        init(isUpdate, terminalData.getId(), playList);
    }


    static class TerminalHandler extends Handler {
        private final WeakReference<TerminalView> terminalViewService;

        TerminalHandler(TerminalView terminalView) {
            terminalViewService = new WeakReference(terminalView);
        }

        @Override
        public void handleMessage(Message msg) {
            TerminalView terminalView = terminalViewService.get();
            if (terminalView != null) {
                terminalView.handleMessage(msg);
            }
        }
    }


    private void removeTopViews(boolean isRemovetop){
        if(!isRemovetop && noProgramasView.isAttachedToWindow()){
            removeView(noProgramasView);
        }
        if(progressView.isAttachedToWindow()){
            removeView(progressView);
        }
    }

    /**
     * 处理handler消息
     *
     * @param msg
     */
    public void handleMessage(Message msg) {
        if (msg.what == 1000) {
            boolean isPause = (programsView != null && programsView.isAttachedToWindow() &&  programsView.getPlayState()!=null
                    &&  programsView.getPlayState().isPause());
            removeTopViews(isPause);
            if (!isPause) {
                loadView((Boolean) msg.obj);
                handler.removeMessages(terminalData.getId());
            }
        } else if (msg.what == terminalData.getId()) {
            int progress = DownLoadManager.getCur(terminalData.getId()).getProgress();
            boolean isUpdate = (Boolean) msg.obj;
            if (progressView.isAttachedToWindow()) {//如果当前非初始化下载
                progressBar.setProgress(progress);//更新进度条

            }
            if (!(programsView != null && programsView.isAttachedToWindow() &&  programsView.getPlayState()!=null
                    &&  programsView.getPlayState().isPause())) {
                if (noProgramasView.isAttachedToWindow()) {
                    noProgramasView.setText(String.format(getResources().getString(R.string.text_download_progress), progress));
                }else{
                    addView(noProgramasView);
                }
            }

            Message message = handler.obtainMessage();
            message.what = terminalData.getId();
            message.obj = isUpdate;
            handler.sendMessageDelayed(message, 500);
        }

    }


    /**
     * 初始化
     *
     * @param isUpdate
     * @param tid
     * @param playList
     */
    private void init(boolean isUpdate, int tid, PlayList playList) {
        if(playList == null){
            return;
        }
        this.playList = playList;
        new Thread(new DownloadThread(isUpdate, tid, playList)).start();
        if (isUpdate) {
            if (!progressView.isAttachedToWindow()) {
                addView(progressView);
            }
        } else {
            if (!noProgramasView.isAttachedToWindow()) {
                addView(noProgramasView);
            }
        }
        if (isUpdate) {
            UIKit.dLog("准备更新下载:" + tid);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (DownLoadManager.getCur(terminalData.getId()) != null) {
            DownLoadManager.getCur(terminalData.getId()).stop();//停止下载
        }

    }

    private void loadView(boolean isUpdate) {
        if (isUpdate && programsView != null) {
            programsView.update(playList, screenWidth, screenHeight);
        } else {
            programsView = new ProgramsView(getContext(),terminalData);
            addView(programsView);
        }
    }

    /**
     * 下载线程
     */
    class DownloadThread implements Runnable {
        private boolean isUpdate;
        private int tid;
        private PlayList playList;

        public DownloadThread(boolean isUpdate, int tid, PlayList playList) {
            this.isUpdate = isUpdate;
            this.tid = tid;
            this.playList = playList;
        }

        @Override
        public void run() {
            DownLoadManager.toDownLoad(tid, playList, FileUtils.getTempDir().getPath(),
                    FileUtils.getDownloadDir().getPath(), new DownLoadCallBack() {
                        @Override
                        public boolean toPlay(int tid, boolean b) {
                            if (b) {
                                saveToLocal();//下载完成后保存本地覆盖老数据
                                if (programsView != null && programsView.isAttachedToWindow()) {
                                    PlayState playState = programsView.getPlayState();
                                    if (playState.isPause()) {
                                        playState.setUpdate(true);//如果有更新先不更新视图 待恢复播放后更新
                                    }
                                }
                                Message message = handler.obtainMessage();
                                message.what = 1000;
                                message.obj = isUpdate;
                                message.sendToTarget();
                                return true;
                            }
                            return false;
                        }
                    }, 6, 0);
            //发送进度更新
            Message message = handler.obtainMessage();
            message.what = terminalData.getId();
            message.obj = isUpdate;
            handler.sendMessageDelayed(message, 500);
        }

    }

    private void saveToLocal(){
        //更新到本地数据
        Data localData = XLContext.getData();
        if(localData != null){
            List<DeviceTerminal> deviceTerminals = localData.getDeviceTerminalList();
            for (DeviceTerminal deviceTerminal:deviceTerminals){
                if(deviceTerminal.getId()==(terminalData.getId())){
                    deviceTerminal.setPlayList(playList);
                    XLContext.saveData(localData);
                    break;
                }
            }
        }
    }

    /**
     * 更新位置信息
     */
    public void updateLocation() {
        if (programsView != null) {
            programsView.updateLocation();
        }
    }

    public void update(PlayList playList) {

        init(true, terminalData.getId(), playList);
    }


}
