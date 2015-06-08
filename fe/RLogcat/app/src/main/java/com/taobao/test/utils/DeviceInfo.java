package com.taobao.test.utils;

import java.io.Serializable;

/**
 * Created by hongyanyin on 5/13/15.
 */
public class DeviceInfo implements Serializable {
    private String imei;
    private String ip;
    private String os;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    private String deviceModel;

}
