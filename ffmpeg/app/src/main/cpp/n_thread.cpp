//
// Created by 10404 on 2019/10/18.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"native",FORMAT,##__VA_ARGS__);
void* th_fun(void* arg)
JNIEXPORT void JNICALL
Java_com_maple_ffmpeg_NativeThreadUtil_thread(JNIEnv *env, jobject instance) {
    char
    for (int i = 0; i < 5; ++i) {
    }
}