package com.xl.xyl2.bean;

/**
 * @author hexiancheng
 * @description
 * @date 2019/8/27
 */
public class InitRequest {
    /**
     * lan : cn
     * data : {"code":"5ba05fcfc377fd46","lotCode":"lot0001","location":"121.609555&31.182709","curVersionCode":"1","curVersionName":"v1.0"}
     */

    private String lan;
    private DataBean data;

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * code : 5ba05fcfc377fd46
         * lotCode : lot0001
         * location : 121.609555&31.182709
         * curVersionCode : 1
         * curVersionName : v1.0
         */

        private String code;
        private String lotCode;
        private String location;
        private String curVersionCode;
        private String curVersionName;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getLotCode() {
            return lotCode;
        }

        public void setLotCode(String lotCode) {
            this.lotCode = lotCode;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getCurVersionCode() {
            return curVersionCode;
        }

        public void setCurVersionCode(String curVersionCode) {
            this.curVersionCode = curVersionCode;
        }

        public String getCurVersionName() {
            return curVersionName;
        }

        public void setCurVersionName(String curVersionName) {
            this.curVersionName = curVersionName;
        }
    }
}
