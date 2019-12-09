package com.xl.xyl2.net.udp;

import java.io.Serializable;

//通信机制
public class MsgModel implements Serializable {
    public String f;//发送方的ID
    public String t;//接收方的ID
    public String c;//发送命令
    public Object v;//发送值
    public String i;//返回标识

    public MsgModel() {
        super();
    }

    public MsgModel(String f, String t, String c, Object v, String i) {
        super();
        this.f = f;
        this.t = t;
        this.c = c;
        this.v = v;
        this.i = i;
    }

    public String getF() {
        return this.f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getT() {
        return this.t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getC() {
        return this.c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public Object getV() {
        return this.v;
    }

    public void setV(Object v) {
        this.v = v;
    }

    public String getI() {
        return this.i;
    }

    public void setI(String i) {
        this.i = i;
    }

    @Override
    public String toString() {
        return "MsgModel [f=" + f + ", t=" + t + ", c=" + c + ", v=" + v
                + ", i=" + i + "]";
    }

}
