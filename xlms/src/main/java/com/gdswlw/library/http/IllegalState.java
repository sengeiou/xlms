package com.gdswlw.library.http;

/**
 * Created by Afun on 2019/11/15.
 */

public class IllegalState extends IllegalStateException {
    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    String errcode;

    public IllegalState(String errMsg){
        super(errMsg);
    }

}
