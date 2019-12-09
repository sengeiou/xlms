package com.xl.xyl2.play;

import java.io.Serializable;
import java.util.Date;

/**
 * 播放日志
 * Created by Afun on 2019/10/16.
 */

public class PlayLog implements Serializable{
    public String getIndef() {
        return indef;
    }

    public void setIndef(String indef) {
        this.indef = indef;
    }

    private String indef;
    private Date logDate;//日期
    private String logTime="";//播放时间
    private int duration;//总时间
    private int times;//次数
    private int terminalId;//终端id
    private String program;//节目json

    public int getIncre() {
        return incre;
    }

    public void setIncre(int incre) {
        this.incre = incre;
    }

    private int incre = 0;//更新次数
    public void update(String logTime,int duration,String program){
        this.logTime = this.logTime+logTime+",";
        times++;
        this.duration += duration;
        this.program = program;
        incre ++;
    }
    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(int terminalId) {
        this.terminalId = terminalId;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
}
