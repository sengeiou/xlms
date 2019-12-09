package com.xl.xyl2.play;


import java.io.Serializable;

public class AutoFaceSetting implements Serializable{
    public int getsAge() {
        return sAge;
    }

    public void setsAge(int sAge) {
        this.sAge = sAge;
    }

    public int geteAge() {
        return eAge;
    }

    public void seteAge(int eAge) {
        this.eAge = eAge;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int sAge;
    public int eAge;
    public int sex;//性别 0-不限  1-女 2-男
}
