//
// Created by ChrisRodinbell on 2020/5/19.
//
//#include <jni.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>

#include "GPIOControl.h"

#include <android/log.h>
#define TAG "jni_gpio"
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#define IN              0
#define OUT             1
#define LOW             0
#define HIGH            1

#define BUFFER_MAX    3
#define DIRECTION_MAX 48

/*
 * Class:     com_zhuang_jnigpio_GPIOControl
 * Method:    exportGpio
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_jnigpio_GPIOControl_exportGpio(JNIEnv *env, jclass clazz, jint gpio) {

    char buffer[BUFFER_MAX];
    int len;
    int fd;

    fd = open("/sys/class/gpio/export", O_WRONLY);
    if (fd < 0) {
        LOGE("Failed to open export for writing!\n");
        return(0);
    }

    len = snprintf(buffer, BUFFER_MAX, "%d", gpio);
    if (write(fd, buffer, len) < 0) {
        LOGE("Fail to export gpio!\n");
        return 0;
    }

    close(fd);
    return 1;
}

/*
 * Class:     com_zhuang_jnigpio_GPIOControl
 * Method:    setGpioDirection
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL
Java_jnigpio_GPIOControl_setGpioDirection(JNIEnv *env, jclass clazz, jint gpio,
                                          jint direction) {

    static const char dir_str[]  = "in\0out";
    char path[DIRECTION_MAX];
    int fd;

    snprintf(path, DIRECTION_MAX, "/sys/class/gpio/gpio%d/direction", gpio);
    fd = open(path, O_WRONLY);
    if (fd < 0) {
        LOGE("failed to open gpio direction for writing!\n");
        return 0;
    }

    if (write(fd, &dir_str[direction == IN ? 0 : 3], direction == IN ? 2 : 3) < 0) {
        LOGE("failed to set direction!\n");
        return 0;
    }

    close(fd);
    return 1;
}

/*
 * Class:     com_zhuang_jnigpio_GPIOControl
 * Method:    readGpioStatus
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_jnigpio_GPIOControl_readGpioStatus(JNIEnv *env, jclass clazz, jint gpio) {

    char path[DIRECTION_MAX];
    char value_str[3];
    int fd;

    snprintf(path, DIRECTION_MAX, "/sys/class/gpio/gpio%d/value", gpio);
    fd = open(path, O_RDONLY);
    if (fd < 0) {
        LOGE("failed to open gpio value for reading!\n");
        return -1;
    }

    if (read(fd, value_str, 3) < 0) {
        LOGE("failed to read value!\n");
        return -1;
    }

    close(fd);
    return (atoi(value_str));
}

/*
 * Class:     com_zhuang_jnigpio_GPIOControl
 * Method:    writeGpioStatus
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL
Java_jnigpio_GPIOControl_writeGpioStatus(JNIEnv *env, jclass clazz, jint gpio, jint value) {

    static const char values_str[] = "01";
    char path[DIRECTION_MAX];
    int fd;

    snprintf(path, DIRECTION_MAX, "/sys/class/gpio/gpio%d/value", gpio);
    fd = open(path, O_WRONLY);
    if (fd < 0) {
        LOGE("failed to open gpio value for writing!\n");
        return 0;
    }

    if (write(fd, &values_str[value == LOW ? 0 : 1], 1) < 0) {
        LOGE("failed to write value!\n");
        return 0;
    }

    close(fd);
    return 1;
}

/*
 * Class:     com_zhuang_jnigpio_GPIOControl
 * Method:    unexportGpio
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_jnigpio_GPIOControl_unexportGpio(JNIEnv *env, jclass clazz, jint gpio) {

    char buffer[BUFFER_MAX];
    int len;
    int fd;

    fd = open("/sys/class/gpio/unexport", O_WRONLY);
    if (fd < 0) {
        LOGE("Failed to open unexport for writing!\n");
        return 0;
    }

    len = snprintf(buffer, BUFFER_MAX, "%d", gpio);
    if (write(fd, buffer, len) < 0) {
        LOGE("Fail to unexport gpio!");
        return 0;
    }

    close(fd);
    return 1;
}

JNIEXPORT jint JNICALL
Java_jnigpio_GPIOControl_getDeviceStatus(JNIEnv *env, jclass clazz, jstring device_path) {
    char path[DIRECTION_MAX];
    char value_str[3];
    int fd;
    //jstring -> char* -> char[]
    strcpy(path, stringToChar(env, device_path));

    LOGD("getDeviceStatus device_path=%s\n", path);
//    snprintf(path, DIRECTION_MAX, "/sys/class/gpio/gpio%d/value", gpio);
    fd = open(path, O_RDONLY);
    if (fd < 0) {
        LOGE("failed to open device for reading!\n");
        return -1;
    }

    if (read(fd, value_str, 3) < 0) {
        LOGE("failed to read value!\n");
        return -1;
    }

    close(fd);
    return (atoi(value_str));
}

JNIEXPORT jint JNICALL
Java_jnigpio_GPIOControl_setDeviceStatus(JNIEnv *env, jclass clazz, jstring device_path, jint status) {
//    static const char dir_str[]  = "in\0out";
    static const char values_str[] = "01";
    char path[DIRECTION_MAX];
    int fd;
    int ret;

    //jstring -> char* -> char[]
    strcpy(path, stringToChar(env, device_path));
    LOGD("setDeviceStatus path=%s, status=%d\n", path, status);
//    snprintf(path, DIRECTION_MAX, "/sys/class/gpio/gpio%d/direction", gpio);
    fd = open(path, O_WRONLY);
    if (fd < 0) {
        LOGE("failed to open device status for writing!\n");
        return 0;
    }

    ret = write(fd, &values_str[status == LOW ? 0 : 1], 1);
    if (ret < 0) {
        LOGE("failed to set device status!\n");
        return 0;
    }

    close(fd);
    return 1;
}

//Utils
jstring charToJstring(JNIEnv *env, const char *pat) {
    jclass strClass = (*env)->FindClass(env, "java/lang/String");
    jmethodID ctorID = (*env)->GetMethodID(env, strClass, "<init>","([BLjava/lang/String;)V");
    jbyteArray bytes = (*env)->NewByteArray(env, strlen(pat));
    (*env)->SetByteArrayRegion(env, bytes, 0, strlen(pat), (jbyte *) pat);
    jstring encoding = (*env)->NewStringUTF(env, "utf-8");
    return (jstring) (*env)->NewObject(env, strClass, ctorID, bytes, encoding);
}

char *stringToChar(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = (*env)->FindClass(env, "java/lang/String");
    jstring strencode = (*env)->NewStringUTF(env, "utf-8");
    jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, mid, strencode);
    jsize alen = (*env)->GetArrayLength(env, barr);
    jbyte *ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env, barr, ba, 0);
    return rtn;
}
