package com.jiajunhui.multiscreen_udp_terminal.bean;

import java.io.Serializable;

/**
 * Created by Taurus on 16/11/13.
 */

public class BasePackage implements Serializable {
    private int eventCode;
    private String eventMessage;
    private String deviceInfo;

    public BasePackage() {
    }

    public BasePackage(int eventCode, String eventMessage, String deviceInfo) {
        this.eventCode = eventCode;
        this.eventMessage = eventMessage;
        this.deviceInfo = deviceInfo;
    }

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
