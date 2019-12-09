package com.xl.xyl2.net.udp;

//执行缓存机制
public class HandleCacheModle {
    public long RunTime;
    public Object Value;

    public HandleCacheModle() {
        super();
    }

    public HandleCacheModle(long runTime, Object value) {
        super();
        RunTime = runTime;
        Value = value;
    }

    public long getRunTime() {
        return RunTime;
    }

    public void setRunTime(long runTime) {
        RunTime = runTime;
    }

    public Object getValue() {
        return Value;
    }

    public void setValue(Object value) {
        Value = value;
    }
}
