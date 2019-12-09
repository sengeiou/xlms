package com.xl.xyl2.play;

import java.util.Date;

public class TimerProgramModel {
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public PlayProgram getPlayProgram() {
        return playProgram;
    }

    public void setPlayProgram(PlayProgram playProgram) {
        this.playProgram = playProgram;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    String startTime;
    String endTime;
    PlayProgram playProgram;
    public Date effectiveDate;//生效日期
    public Date expiryDate;//失效日期
}
