package com.xl.xyl2.bean;

import java.util.List;

/**
 * @author hexiancheng
 * @description
 * @date 2019/8/27
 */
public class InitReturn {

    /**
     * success : 1
     * errCode :
     * errMsg :
     * data : {"resSeverHost":"http://192.168.0.30:10003","deviceTerminalList":[{"id":3,"type":"S01","unitName":"单屏980","isMain":true,"screenCount":1,"screenWidth":980,"screenHeight":720,"screenX":0,"screenY":0,"userTerminalId":2,"userTerminalName":"单屏9806","playId":0}],"imServerIpAndPort":"192.168.0.30:8888"}
     */

    private int success;
    private String errCode;
    private String errMsg;
    private DataBean data;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * resSeverHost : http://192.168.0.30:10003
         * deviceTerminalList : [{"id":3,"type":"S01","unitName":"单屏980","isMain":true,"screenCount":1,"screenWidth":980,"screenHeight":720,"screenX":0,"screenY":0,"userTerminalId":2,"userTerminalName":"单屏9806","playId":0}]
         * imServerIpAndPort : 192.168.0.30:8888
         */

        private String resSeverHost;
        private String imServerIpAndPort;
        private List<DeviceTerminalListBean> deviceTerminalList;

        public String getResSeverHost() {
            return resSeverHost;
        }

        public void setResSeverHost(String resSeverHost) {
            this.resSeverHost = resSeverHost;
        }

        public String getImServerIpAndPort() {
            return imServerIpAndPort;
        }

        public void setImServerIpAndPort(String imServerIpAndPort) {
            this.imServerIpAndPort = imServerIpAndPort;
        }

        public List<DeviceTerminalListBean> getDeviceTerminalList() {
            return deviceTerminalList;
        }

        public void setDeviceTerminalList(List<DeviceTerminalListBean> deviceTerminalList) {
            this.deviceTerminalList = deviceTerminalList;
        }

        public static class DeviceTerminalListBean {
            /**
             * id : 3
             * type : S01
             * unitName : 单屏980
             * isMain : true
             * screenCount : 1
             * screenWidth : 980
             * screenHeight : 720
             * screenX : 0
             * screenY : 0
             * userTerminalId : 2
             * userTerminalName : 单屏9806
             * playId : 0
             */

            private int id;
            private String type;
            private String unitName;
            private boolean isMain;
            private int screenCount;
            private int screenWidth;
            private int screenHeight;
            private int screenX;
            private int screenY;
            private int userTerminalId;
            private String userTerminalName;
            private int playId;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getUnitName() {
                return unitName;
            }

            public void setUnitName(String unitName) {
                this.unitName = unitName;
            }

            public boolean isIsMain() {
                return isMain;
            }

            public void setIsMain(boolean isMain) {
                this.isMain = isMain;
            }

            public int getScreenCount() {
                return screenCount;
            }

            public void setScreenCount(int screenCount) {
                this.screenCount = screenCount;
            }

            public int getScreenWidth() {
                return screenWidth;
            }

            public void setScreenWidth(int screenWidth) {
                this.screenWidth = screenWidth;
            }

            public int getScreenHeight() {
                return screenHeight;
            }

            public void setScreenHeight(int screenHeight) {
                this.screenHeight = screenHeight;
            }

            public int getScreenX() {
                return screenX;
            }

            public void setScreenX(int screenX) {
                this.screenX = screenX;
            }

            public int getScreenY() {
                return screenY;
            }

            public void setScreenY(int screenY) {
                this.screenY = screenY;
            }

            public int getUserTerminalId() {
                return userTerminalId;
            }

            public void setUserTerminalId(int userTerminalId) {
                this.userTerminalId = userTerminalId;
            }

            public String getUserTerminalName() {
                return userTerminalName;
            }

            public void setUserTerminalName(String userTerminalName) {
                this.userTerminalName = userTerminalName;
            }

            public int getPlayId() {
                return playId;
            }

            public void setPlayId(int playId) {
                this.playId = playId;
            }
        }
    }
}
