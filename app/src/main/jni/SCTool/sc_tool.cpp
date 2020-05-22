//#include <jni.h>
#include <string>
#include <android/log.h>
#include <cainiao_crypto.h>

#include "sc_tool.h"

#define TAG "jnisctool"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__)

extern "C"
JNIEXPORT jint JNICALL
Java_jnisctool_SCTool_nativeDecrypt(JNIEnv *env, jclass clazz, jint type, jbyteArray src,
                                            jbyteArray dec) {
    jint result;
    jbyte *bSrc = env->GetByteArrayElements(src, JNI_FALSE);
    jsize jLen = env->GetArrayLength(src);
    jbyte *bDec = env->GetByteArrayElements(dec, JNI_FALSE);

    int dec_len;
    auto ucType = static_cast<unsigned char>(type);
    auto *ucSrc = reinterpret_cast<unsigned char *>(bSrc);
    auto *ucDec = reinterpret_cast<unsigned char *>(bDec);

    result = cainiao_data_decrypt(ucType, ucSrc, jLen, ucDec, &dec_len);

    LOGE("dec_len= %d", dec_len);
    env->ReleaseByteArrayElements(src, bSrc, JNI_COMMIT);
    env->ReleaseByteArrayElements(dec, bDec, JNI_COMMIT);
    return result;
}

extern "C"
JNIEXPORT void JNICALL
Java_jnisctool_SCTool_nativeClose(JNIEnv *env, jclass clazz) {
    cainiao_disable_crypto();
}