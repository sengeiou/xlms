package com.xl.xyl2.play;

import java.io.Serializable;
import java.util.Date;

/**
 * 自动开关机设置
 * Created by Afun on 2019/10/8.
 */

public class AutoOcSetting implements Serializable {

    public Date sdate;

    public Date edate;

    public Boolean week0;
    public Boolean week1;

    public Boolean week2;

    public Boolean week3;

    public Boolean week4;

    public Boolean week5;

    public Boolean week6;


    private String timeInterval0;
    private String timeInterval1;
    private String timeInterval2;
    private String timeInterval3;
    private String timeInterval4;
    private String timeInterval5;
    private String timeInterval6;

    public String getTimeInterval0() {
        if("".equals(timeInterval0)){
            return null;
        }
        return timeInterval0;
    }

    public void setTimeInterval0(String timeInterval0) {
        this.timeInterval0 = timeInterval0;
    }

    public String getTimeInterval1() {
        return timeInterval1;
    }

    public void setTimeInterval1(String timeInterval1) {
        this.timeInterval1 = timeInterval1;
    }

    public String getTimeInterval2() {
        return timeInterval2;
    }

    public void setTimeInterval2(String timeInterval2) {
        this.timeInterval2 = timeInterval2;
    }

    public String getTimeInterval3() {
        return timeInterval3;
    }

    public void setTimeInterval3(String timeInterval3) {
        this.timeInterval3 = timeInterval3;
    }

    public String getTimeInterval4() {
        return timeInterval4;
    }

    public void setTimeInterval4(String timeInterval4) {
        this.timeInterval4 = timeInterval4;
    }

    public String getTimeInterval5() {
        return timeInterval5;
    }

    public void setTimeInterval5(String timeInterval5) {
        this.timeInterval5 = timeInterval5;
    }

    public String getTimeInterval6() {
        return timeInterval6;
    }

    public void setTimeInterval6(String timeInterval6) {
        this.timeInterval6 = timeInterval6;
    }

    public Date getSdate() {
        return sdate;
    }

    public void setSdate(Date sdate) {
        this.sdate = sdate;
    }

    public Date getEdate() {
        return edate;
    }

    public void setEdate(Date edate) {
        this.edate = edate;
    }

    public Boolean getWeek0() {
        return week0;
    }

    public void setWeek0(Boolean week0) {
        this.week0 = week0;
    }

    public Boolean getWeek1() {
        return week1;
    }

    public void setWeek1(Boolean week1) {
        this.week1 = week1;
    }

    public Boolean getWeek2() {
        return week2;
    }

    public void setWeek2(Boolean week2) {
        this.week2 = week2;
    }

    public Boolean getWeek3() {
        return week3;
    }

    public void setWeek3(Boolean week3) {
        this.week3 = week3;
    }

    public Boolean getWeek4() {
        return week4;
    }

    public void setWeek4(Boolean week4) {
        this.week4 = week4;
    }

    public Boolean getWeek5() {
        return week5;
    }

    public void setWeek5(Boolean week5) {
        this.week5 = week5;
    }

    public Boolean getWeek6() {
        return week6;
    }

    public void setWeek6(Boolean week6) {
        this.week6 = week6;
    }


}
