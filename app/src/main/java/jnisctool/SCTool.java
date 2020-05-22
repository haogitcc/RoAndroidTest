package jnisctool;

public class SCTool {
    public static final byte DECRYPT_TYPE1 = 65;
    public static final byte DECRYPT_TYPE2 = 48;
    public static final byte DECRYPT_TYPE3 = 50;
    public static final int SUCCESS = 0;
    public static final int ERROR_PARAM = -1;
    public static final int ERROR_NO_CARD = -2;
    public static final int ERROR_TRANSMISSION = -3;
    public static final int ERROR_TYPE = -4;
    public static final int ERROR_DECRYPT = -5;

    public SCTool() {
    }

    public static int decrypt(byte type, byte[] src, byte[] dec) {
        if (src != null && dec != null) {
            if (src.length % 8 == 0 && src.length <= dec.length) {
                return type != 65 && type != 48 && type != 50 ? -1 : nativeDecrypt(type, src, dec);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static void close() {
        nativeClose();
    }

    private static native int nativeDecrypt(int type, byte[] src, byte[] dec);

    private static native void nativeClose();

    static {
        System.loadLibrary("sc_tool");
    }
}