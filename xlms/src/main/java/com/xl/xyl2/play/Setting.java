package com.xl.xyl2.play;

import java.io.Serializable;

/**
 *
 * Created by Afun on 2019/10/8.
 */

public class Setting implements Serializable {
    private AutoOcSetting autoOcSetting;
    private String identification;
    private int vol;
    private int brightness;
    private boolean isAutoOc;
    private boolean isOpPsd;

    public boolean getIsOpPsd() {
        return isOpPsd;
    }

    public void setOpPsd(String opPsd) {
        this.opPsd = opPsd;
    }

    public String getOpPsd() {
        return this.opPsd;
    }

    public void setIsOpPsd(boolean opPsd) {
        isOpPsd = opPsd;
    }

    private String opPsd;

    public AutoOcSetting getAutoOcSetting() {
        return autoOcSetting;
    }

    public void setAutoOcSetting(AutoOcSetting autoOcSetting) {
        this.autoOcSetting = autoOcSetting;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public boolean getAutoOc() {
        return isAutoOc;
    }

    public void setAutoOc(boolean autoOc) {
        isAutoOc = autoOc;
    }

}
