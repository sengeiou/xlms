package com.xl.xyl2.play;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * 初始化数据对象
 * Created by Afun on 2019/10/8.
 */

public class Data implements Serializable  {
    private String resSeverHost;
    private List<DeviceTerminal> deviceTerminalList;
    private String imServerIpAndPort;
    private Setting setting;

    public String getResSeverHost() {
        return resSeverHost;
    }

    public void setResSeverHost(String resSeverHost) {
        this.resSeverHost = resSeverHost;
    }

    public List<DeviceTerminal> getDeviceTerminalList() {
        return deviceTerminalList;
    }

    public void setDeviceTerminalList(List<DeviceTerminal> deviceTerminalList) {
        this.deviceTerminalList = deviceTerminalList;
    }

    public String getImServerIpAndPort() {
        return imServerIpAndPort;
    }

    public void setImServerIpAndPort(String imServerIpAndPort) {
        this.imServerIpAndPort = imServerIpAndPort;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
