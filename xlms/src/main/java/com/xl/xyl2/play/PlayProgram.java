package com.xl.xyl2.play;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 *播放界面 播放列表有1-N个播放界面组成
 */
public class PlayProgram implements Serializable{
    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    private  int tid;//终端id
    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getBgColor() {
        return bgColor == null?"#ffffff":bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    private String bgColor;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<PlayArea> getAreas() {
        return areas;
    }

    public void setAreas(List<PlayArea> areas) {
        this.areas = areas;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
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

    public Boolean getAddPlayQueue() {
        return addPlayQueue;
    }

    public void setAddPlayQueue(Boolean addPlayQueue) {
        this.addPlayQueue = addPlayQueue;
    }

    public Boolean getIsTimer() {
        return isTimer;
    }

    public void setIsTimer(Boolean timer) {
        isTimer = timer;
    }

    public String getTimeFrames() {
        return timeFrames;
    }

    public void setTimeFrames(String timeFrames) {
        this.timeFrames = timeFrames;
    }

    public Boolean getIsAutoPlayFaceSetting() {
        return isAutoPlayFaceSetting;
    }

    public void setIsAutoPlayFaceSetting(Boolean isAutoPlayFaceSetting) {
        this.isAutoPlayFaceSetting = isAutoPlayFaceSetting;
    }

    public AutoFaceSetting getAutoPlayFaceSetting() {
        return this.autoPlayFaceSetting;
    }

    public void setAutoPlayFaceSetting(AutoFaceSetting autoPlayFaceSetting) {
        this.autoPlayFaceSetting = autoPlayFaceSetting;
    }

    public Boolean getIsAutoPlayIdentifying() {
        return isAutoPlayIdentifying;
    }

    public void setIsAutoPlayIdentifying(Boolean isAutoPlayIdentifying) {
        this.isAutoPlayIdentifying = isAutoPlayIdentifying;
    }

    public String getAutoPlayIdentifying() {
        return this.autoPlayIdentifying;
    }

    public void setAutoPlayIdentifying(String autoPlayIdentifying) {
        this.autoPlayIdentifying = autoPlayIdentifying;
    }

    public ArrayList<PlayUnit> getLinkUnit() {
        return linkUnits;
    }

    public void setLinkUnit(ArrayList<PlayUnit> linkUnits) {
        this.linkUnits = linkUnits;
    }


    public String identification; //播放节目唯一标识
    //节目自身属性--------------------------------------------------------------
    public String name;//节目名称
    public int width;//宽
    public int height;//高
    public String pic;//节目快照
    public List<PlayArea> areas; //区域

    //节目发布属性--------------------------------------------------------------
    public int lv;  //优先级 数字越大越优先  后台定义 1-4，外部插入 6-9

    //播放条件
    public Date effectiveDate;//生效日期
    public Date expiryDate;//失效日期

    //顺序播放条件---------------------------
    public Boolean addPlayQueue;//是否加入顺序播放列队

    //促发播放条件---------------------------
    //当多条件出发时 优先按优先等级处理-高等级不被低等级覆盖 同等级或低于当前播放等级时不被覆盖
    public Boolean isTimer;//是否定时播放
    public String timeFrames;//定时时段，格式 09:00:00-09:01:00;10:00:00-10:01:00

    public Boolean isAutoPlayFaceSetting;//是否开启根据人脸特征切换
    public AutoFaceSetting autoPlayFaceSetting;//自动播放设置-根据人脸特征

    public Boolean isAutoPlayIdentifying;//是否开启标识识别自动播放
    public String autoPlayIdentifying;//自动播放标识  用于扩展 传感器等促发

    public ArrayList<PlayUnit> getLinkUnits() {
        return linkUnits == null ? new ArrayList<PlayUnit>():linkUnits;
    }

    public void setLinkUnits(ArrayList<PlayUnit> linkUnits) {
        this.linkUnits = linkUnits;
    }

    //分屏关联播放---------------------------
    public ArrayList<PlayUnit> linkUnits;//联动单元  根据需求 可改联动目

}
