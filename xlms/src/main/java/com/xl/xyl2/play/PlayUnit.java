package com.xl.xyl2.play;


import android.content.Intent;

import java.io.Serializable;

//播放单元
public class PlayUnit implements Serializable{
    private long startTime;
    private long puaseTime;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getPuaseTime() {
        return puaseTime;
    }

    public void setPuaseTime(long puaseTime) {
        this.puaseTime = puaseTime;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int tid;
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceThumUrl() {
        return sourceThumUrl;
    }

    public void setSourceThumUrl(String sourceThumUrl) {
        this.sourceThumUrl = sourceThumUrl;
    }

    public long getSourceSize() {
        return sourceSize;
    }

    public void setSourceSize(long sourceSize) {
        this.sourceSize = sourceSize;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAnimate() {
        return animate;
    }

    public void setAnimate(String animate) {
        this.animate = animate;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String sourceId;//单元唯一标识UUID
    public String sourceUrl;//文件网络地址
    public String sourceThumUrl;//文件缩略图网络地址
    public long  sourceSize;//文件大小
    public int duration; //播放时长
    public String animate; //载入动画效果 预留 图片属性
    public int vol;//音量 0-100 视频属性
    public int sourceType;
}
