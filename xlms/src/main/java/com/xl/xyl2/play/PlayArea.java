package com.xl.xyl2.play;


import java.io.Serializable;
import java.util.List;

//播放区域
public class PlayArea implements Serializable{
    private String country;
    private String province;
    private String city;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<PlayUnit> getUnits() {
        return units;
    }

    public void setUnits(List<PlayUnit> units) {
        this.units = units;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontColor() {
        return fontColor == null?"#ffffff":fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String identification; //播放区域唯一标识

    public int x;
    public int y;
    public int width;
    public int height;
    //区域类型 1-视频 2-图片 3-时间日期 4-天气 5-网页 6-直播流 7-字幕 8文本
    public int type;

    private String bgColor;
    private Float bgOpacity;

    public String getBgColor() {
        return bgColor == null?"#ffffff":bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public Float getBgOpacity() {
        return bgOpacity == null ? 1:bgOpacity;
    }

    public void setBgOpacity(Float bgOpacity) {
        this.bgOpacity = bgOpacity;
    }

    //视频 图片属性
    public List<PlayUnit> units;

    //直播流 网页属性
    public String url;

    //文本 字幕 属性
    public String text;
    public int fontSize;
    public String fontColor;
    public String font;

    //字幕属性
    public int speed;// 1-慢  2-中  3-快
}
