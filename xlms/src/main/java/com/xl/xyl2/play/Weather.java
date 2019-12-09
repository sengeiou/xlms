package com.xl.xyl2.play;

import java.io.Serializable;

/**
 * 天气
 * Created by Afun on 2019/10/9.
 */

public class Weather implements Serializable {
    /**
     *  "data":{
             "date":"2019-10-08",  //日期
             "city":”city”,    //城市
             "weather":”多云”,    //天气情况
             "mintemp":”15”,    //最低温度
             "maxtemp":”25”   //最高温度
         }
     */
    private String date;
    private String city;
    private String weather;
    private int mintemp;
    private int maxtemp;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public int getMintemp() {
        return mintemp;
    }

    public void setMintemp(int mintemp) {
        this.mintemp = mintemp;
    }

    public int getMaxtemp() {
        return maxtemp;
    }

    public void setMaxtemp(int maxtemp) {
        this.maxtemp = maxtemp;
    }
}
