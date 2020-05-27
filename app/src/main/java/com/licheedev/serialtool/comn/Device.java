package com.licheedev.serialtool.comn;

import java.io.File;

/**
 * 串口设备
 */
public class Device {
    private String path;
    private String baudrate;
    private File device;

    public Device() {
    }

    public Device(String path, String baudrate) {
        this.path = path;
        this.baudrate = baudrate;
        this.device = new File(path);
    }

    public Device(String path, int baudrate) {
        this.path = path;
        this.baudrate = new String(""+baudrate);
        this.device = new File(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(String baudrate) {
        this.baudrate = baudrate;
    }

    public int getBaudrateInt() {
        return Integer.parseInt(baudrate);
    }

    @Override
    public String toString() {
        return "Device{" + "path='" + path + '\'' + ", baudrate='" + baudrate + '\'' + '}';
    }

    public File getDevice() {
        return device;
    }
}
