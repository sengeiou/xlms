package com.xl.xyl2.play;

import android.text.TextUtils;

import com.xl.xyl2.XLContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Processor {

    PlayList playList; //播放列表

    PlayStateModel lastSequence; //顺序列表最后播放节目

    PlayStateModel lastTimer; //定时列队最后播放节目

    List<TimerProgramModel> timerList; //定时播放列队

    List<PlayProgram> idfList; //标识促发播放列队

    public List<PlayProgram> getIdfList() {
        return idfList;
    }

    public void setIdfList(List<PlayProgram> idfList) {
        this.idfList = idfList;
    }

    public List<PlayProgram> getFaceList() {
        return faceList;
    }

    public void setFaceList(List<PlayProgram> faceList) {
        this.faceList = faceList;
    }

    List<PlayProgram> faceList; //人流识别促发播放列队

    PlayStateModel cur; //当前播放节目

    ProgramChangeCallBack programChangeCallBack;

    boolean isc = false;

    void setCur(PlayStateModel cur) {
        if (isc) return;
        isc = true;
        try {
            PlayProgram old =this.cur==null?null: this.cur.getPlayProgram();
            this.cur = cur;
            if (cur.getPlayPromoteModel() == 1) lastSequence = cur;
            if (cur.getPlayPromoteModel() == 2) lastTimer = cur;
            //切换回调
            if (programChangeCallBack != null) {
                programChangeCallBack.change(cur.getPlayProgram(), old);
            }
        } finally {
            isc = false;
        }
    }

    public PlayProgram getCurProgram() {
        return cur == null ? null : cur.playProgram;
    }

    public PlayList getPlayList() {
        return this.playList;
    }

    public void setPlayList(PlayList playList) {
        String oldCurProgramIdf=this.cur==null || this.cur.getPlayProgram()==null?null:this.cur.getPlayProgram().getIdentification();
        this.playList = playList;
        timerList = new ArrayList<>();
        idfList = new ArrayList<>();
        faceList = new ArrayList<>();
        lastSequence = null;
        lastTimer = null;
        cur = null;
        //分解播放列表
        if (playList == null || playList.getPrograms() == null || playList.getPrograms().size() == 0)
            return;
        for (PlayProgram pp : playList.getPrograms()) {
            Date now = getServerTime();
            //定时列队
            if (pp.getIsTimer() && !TextUtils.isEmpty(pp.getTimeFrames())) {
                String[] strs = pp.getTimeFrames().split(";");
                for (String ss : strs) {
                    String[] cstrs = ss.split("-");
                    if (cstrs.length < 2) continue;
                    TimerProgramModel tp = new TimerProgramModel();
                    tp.setStartTime(cstrs[0]);
                    tp.setEndTime(cstrs[1]);
                    tp.setPlayProgram(pp);
                    tp.setEffectiveDate(pp.getEffectiveDate());
                    tp.setExpiryDate(pp.getExpiryDate());
                    timerList.add(tp);
                }
            }
            //人脸识别列队
            if (pp.getIsAutoPlayFaceSetting() && pp.getAutoPlayFaceSetting() != null)
                faceList.add(pp);
            if (pp.getIsAutoPlayIdentifying() && !TextUtils.isEmpty(pp.getAutoPlayIdentifying()))
                idfList.add(pp);
        }
        reSet(oldCurProgramIdf);
    }

    void reSet(String idf) {
        if (!TextUtils.isEmpty(idf)) {
            Date now = getServerTime();
            PlayStateModel willPs = null;
            for (int i = 0; i < timerList.size(); i++) {
                TimerProgramModel tp = timerList.get(i);
                if (!tp.getPlayProgram().getIdentification().equals(idf)) continue;
                //不在有效期限内
                if ((tp.getPlayProgram().getEffectiveDate() != null && now.getTime() < tp.getPlayProgram().getEffectiveDate().getTime())
                        || (tp.getPlayProgram().getExpiryDate() != null && now.getTime() > tp.getPlayProgram().getExpiryDate().getTime()))
                    continue;
                String nhms = new SimpleDateFormat("HH:mm").format(now);
                //不在有效时间段内
                if (nhms.compareTo(tp.getStartTime()) < 0 || nhms.compareTo(tp.getEndTime()) >= 0) continue;
                //定时处理
                willPs = new PlayStateModel();
                willPs.setIndex(i);
                willPs.setPlayPromoteModel(2);
                willPs.setPlayProgram(tp.getPlayProgram());
            }
            if (willPs == null) {
                //顺序播放列表
                for (int i = 0; i < playList.getPrograms().size(); i++) {
                    PlayProgram pp = playList.getPrograms().get(i);
                    if (!pp.getIdentification().equals(idf)) continue;
                    //不加入顺序列队
                    if (!pp.getAddPlayQueue()) continue;
                    //不在有效期限内
                    if ((pp.getEffectiveDate() != null && now.getTime() < pp.getEffectiveDate().getTime())
                            || (pp.getExpiryDate() != null && now.getTime() > pp.getExpiryDate().getTime()))
                        continue;
                    willPs = new PlayStateModel();
                    willPs.setIndex(i);
                    willPs.setPlayPromoteModel(1);
                    willPs.setPlayProgram(pp);
                }
            }
            if (willPs != null) {
                this.cur = willPs;
                if (this.cur.getPlayPromoteModel() == 1) lastSequence = cur;
                if (this.cur.getPlayPromoteModel() == 2) lastTimer = cur;
                return;
            }
        }
        getNext();
    }

    private TimerTh timerTh = null;// 同步线程

    Date getServerTime() {
        Date date  = new Date();
        date.setTime(XLContext.getLocalServerTime());
        return date;
    }

    //初始化
    public void init(ProgramChangeCallBack programChangeCallBack) {
        this.programChangeCallBack = programChangeCallBack;
        if (timerTh == null) {
            timerTh = new TimerTh();
            timerTh.start();
        }
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    private boolean pause = false;

    //资源释放
    public void disposable() {
        if (timerTh == null) timerTh.isRun = false;
    }

    //定时播放监听
    class TimerTh extends Thread {
        public boolean isRun = true;

        @Override
        public void run() {
            while (isRun) {
                try {
                    if(pause) return;//暂停
                    if (timerList == null || timerList.size() == 0) continue;
                    //当前定时广告中
                    if (cur != null && cur.getPlayPromoteModel() == 2) continue;
                    Date now = getServerTime();
                    for (int i = 0; i < timerList.size(); i++) {
                        TimerProgramModel tp = timerList.get(i);
                        //不在有效期限内
                        if ((tp.getPlayProgram().getEffectiveDate() != null && now.getTime() < tp.getPlayProgram().getEffectiveDate().getTime())
                                || (tp.getPlayProgram().getExpiryDate() != null && now.getTime() > tp.getPlayProgram().getExpiryDate().getTime()))
                            continue;
                        String nhms = new SimpleDateFormat("HH:mm").format(now);
                        //不在有效时间段内
                        if (nhms.compareTo(tp.getStartTime()) < 0 || nhms.compareTo(tp.getEndTime()) >= 0) continue;
                        //当前播放与将要促发的节目一致 跳过本次促发
                        if (tp.getPlayProgram().getIdentification().equals(cur.getPlayProgram().getIdentification()))
                            continue;
                        if (cur != null && cur.getPlayPromoteModel() == 3 && cur.getPlayProgram().getLv() >= tp.getPlayProgram().getLv())
                            continue;
                        PlayStateModel willPs = new PlayStateModel();
                        willPs.setIndex(i);
                        willPs.setPlayPromoteModel(2);
                        willPs.setPlayProgram(tp.getPlayProgram());
                        setCur(willPs);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    //下一播放节目
    public void getNext() {
        //定时列队
        Date now = getServerTime();
        PlayStateModel willPs = null;
        for (int i = 0; i < timerList.size(); i++) {
            TimerProgramModel tp = timerList.get(i);
            //不在有效期限内
            if ((tp.getPlayProgram().getEffectiveDate() != null && now.getTime() < tp.getPlayProgram().getEffectiveDate().getTime())
                    || (tp.getPlayProgram().getExpiryDate() != null && now.getTime() > tp.getPlayProgram().getExpiryDate().getTime()))
                continue;
            String nhms = new SimpleDateFormat("HH:mm").format(now);
            //不在有效时间段内
            if (nhms.compareTo(tp.getStartTime()) < 0 || nhms.compareTo(tp.getEndTime()) >= 0) continue;
            //定时处理
            if (willPs == null || (cur != null && cur.getPlayPromoteModel() == 2 && cur.getIndex() < i)) {
                willPs = new PlayStateModel();
                willPs.setIndex(i);
                willPs.setPlayPromoteModel(2);
                willPs.setPlayProgram(tp.getPlayProgram());
            }
            if (cur == null || cur.getPlayPromoteModel() != 2 || cur.getIndex() < i) break;
        }
        if (willPs != null) {
            setCur(willPs);
            return;
        }
        //顺序播放列表
        for (int i = 0; i < playList.getPrograms().size(); i++) {
            PlayProgram pp = playList.getPrograms().get(i);
            //不加入顺序列队
            if (!pp.getAddPlayQueue()) continue;
            //不在有效期限内
            if ((pp.getEffectiveDate() != null && now.getTime() < pp.getEffectiveDate().getTime())
                    || (pp.getExpiryDate() != null && now.getTime() > pp.getExpiryDate().getTime()))
                continue;
            if (willPs == null || (lastSequence != null && lastSequence.getIndex() < i)) {
                willPs = new PlayStateModel();
                willPs.setIndex(i);
                willPs.setPlayPromoteModel(1);
                willPs.setPlayProgram(pp);
            }
            if (lastSequence == null || lastSequence.getIndex() < i) break;
        }
        setCur(willPs);
    }

    boolean idfing = false;

    //标识促发
    public void getNext(String idf) {
        if (idfing) return;
        idfing = true;
        try {
            Date now = getServerTime();
            PlayProgram pp = null;
            for (PlayProgram p : idfList) {
                if (p.getIdentification().equals(idf)) {
                    pp = p;
                    break;
                }
            }
            if (pp == null) return;
            //不在有效期限内
            if ((pp.getEffectiveDate() != null && now.getTime() < pp.getEffectiveDate().getTime())
                    || (pp.getExpiryDate() != null && now.getTime() > pp.getExpiryDate().getTime()))
                return;
            //级别判断
            if (cur != null && (cur.getPlayPromoteModel() == 3 || cur.getPlayPromoteModel() == 2) && pp.getLv() <= cur.getPlayProgram().getLv())
                return;
            //当前播放是将要促发的节目
            if (cur != null && cur.getPlayProgram().getIdentification().equals(pp.getIdentification())) return;
            PlayStateModel willPs = new PlayStateModel();
            willPs.setIndex(-1);
            willPs.setPlayPromoteModel(3);
            willPs.setPlayProgram(pp);
            setCur(willPs);
        } finally {
            idfing = false;
        }
    }

    boolean faceing = false;

    //人流促发
    public void getNext(int sex, int age) {
        if (faceing) return;
        faceing = true;
        try {
            Date now = getServerTime();
            PlayProgram pp = null;
            for (PlayProgram p : faceList) {
                AutoFaceSetting pf = p.getAutoPlayFaceSetting();
                if ((pf.getSex()==0 || pf.getSex() ==(sex)) && age >= pf.getsAge() && age <= pf.geteAge()) {
                    pp = p;
                    break;
                }
            }
            if (pp == null) return;
            //不在有效期限内
            if ((pp.getEffectiveDate() != null && now.getTime() < pp.getEffectiveDate().getTime())
                    || (pp.getExpiryDate() != null && now.getTime() > pp.getExpiryDate().getTime()))
                return;
            //级别判断
            if (cur != null && (cur.getPlayPromoteModel() == 3 || cur.getPlayPromoteModel() == 2) && pp.getLv() <= cur.getPlayProgram().getLv())
                return;
            //当前播放是将要促发的节目
            if (cur != null && cur.getPlayProgram().getIdentification().equals(pp.getIdentification())) return;
            PlayStateModel willPs = new PlayStateModel();
            willPs.setIndex(-1);
            willPs.setPlayPromoteModel(3);
            willPs.setPlayProgram(pp);
            setCur(willPs);
        } finally {
            faceing = false;
        }
    }

}
