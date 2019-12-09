package com.xl.xyl2.bean;

import java.io.Serializable;

/**
 * Created by Afun on 2019/9/19.
 */

public class Device implements Serializable{
    public String getLotCode() {
        return lotCode;
    }

    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }

    /**
     * 批次编号
     */
    String lotCode;
}
