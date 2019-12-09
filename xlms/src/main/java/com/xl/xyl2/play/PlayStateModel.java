package com.xl.xyl2.play;

import java.io.Serializable;

public class PlayStateModel implements Serializable{
    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public PlayProgram getPlayProgram() {
        return playProgram;
    }

    public void setPlayProgram(PlayProgram playProgram) {
        this.playProgram = playProgram;
    }

    public int getPlayPromoteModel() {
        return playPromoteModel;
    }

    public void setPlayPromoteModel(int playPromoteModel) {
        this.playPromoteModel = playPromoteModel;
    }

    int Index;
    PlayProgram playProgram;
    int playPromoteModel;//0-未开始 1-顺序播放 2-定时播放 3-条件触发
}
