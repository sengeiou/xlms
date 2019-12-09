package com.xl.xyl2.play;

/**
 * 播放状态
 * Created by Afun on 2019/11/1.
 */

public class PlayState {
    private boolean isPause = false;//是否暂停
    private boolean isUpdate = false;//是否更新
    private long startPlayTime;//开始播放时间
    private long puaseTime;//暂停时间

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public long getStartPlayTime() {
        return startPlayTime;
    }

    public void setStartPlayTime(long startPlayTime) {
        this.startPlayTime = startPlayTime;
    }

    public long getPuaseTime() {
        return puaseTime;
    }

    public void setPuaseTime(long puaseTime) {
        this.puaseTime = puaseTime;
    }
}
