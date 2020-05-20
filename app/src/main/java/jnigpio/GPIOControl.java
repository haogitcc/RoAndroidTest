package jnigpio;

import android.util.Log;

public class GPIOControl {
    private static final String TAG = "GPIOControl";

    static {
        System.loadLibrary("GPIOControl");
    }

    public final static native int exportGpio(int gpio);
    public final static native int setGpioDirection(int gpio, int direction);
    public final static native int readGpioStatus(int gpio);
    public final static native int writeGpioStatus(int gpio, int value);
    public final static native int unexportGpio(int gpio);

    public final static native int getDeviceStatus(String device_path);//指定 /sys/class/hwmon/hwmon0/led2
    public final static native int setDeviceStatus(String device_path, int status);

    public int getGpioStatus(int i) {
        Log.d(TAG, "getGpioStatus: " + i);
        return readGpioStatus(i);
    }

    public int setGpioStatus(int gpio, int status) {
        Log.d(TAG, "setGpioStatus: " + gpio + " -> " + status);
        return writeGpioStatus(gpio, status);
    }

}