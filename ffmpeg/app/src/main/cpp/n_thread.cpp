//
// Created by 10404 on 2019/10/18.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include <pthread.h>

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"native",FORMAT,##__VA_ARGS__);
JavaVM *mVm;

JNIEXPORT void *JNICALL
th_fun(void *arg) {
    JNIEnv *env;
    mVm->AttachCurrentThread(&env, nullptr);
    char *no = (char *) arg;
    LOGI("%s", no);
    jclass c=env->FindClass("java/util/UUID");
    jmethodID method =env->GetStaticMethodID(c,"randomUUID","()Ljava/util/UUID;");
    jobject  uuid=env->CallStaticObjectMethod(c,method);
    jmethodID  toStringMethod=env->GetMethodID(c,"toString","()Ljava/lang/String;");
    jstring uuidStr = (jstring)(env->CallObjectMethod(uuid, toStringMethod));
    const char* uuidStrChar= env->GetStringUTFChars(uuidStr, nullptr);
    LOGI("UUID: %s",uuidStrChar);
    env->ReleaseStringUTFChars(uuidStr, uuidStrChar);
    mVm->DetachCurrentThread();
    pthread_exit(nullptr);
}
/**
 * 动态库加载时调用
 */
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("%s", "JNI_onLoad");
    //表示最低支持版本,
    mVm =vm;
    return JNI_VERSION_1_4;
}
/**
 * 可以通过JAVAVM获取到每个线程关联的JNIEnv
 * 每个线程的JNIEnv不同.
 * JavaVM:java虚拟机.
 * 获取步骤:
 *  1. 获取JVM:通过`env->GetJavaVM`;JNI_ONLoad函数在库加载时调用,函数回调参数包括JVM,
 *  2. 子线程中` vm->AttachCurrentThread(&env, nullptr);`获取
 *  3. 使用完释放    `vm->DetachCurrentThread();`
 */

extern "C"
JNIEXPORT void JNICALL
Java_com_maple_ffmpeg_NativeThreadUtil_thread(JNIEnv *env, jobject instance) {
    //获取方式2
    JavaVM *vm;
    env->GetJavaVM(&vm);

    pthread_t tid;
    pthread_create(&tid, nullptr, th_fun, (void *) "sub thread");
    pthread_join(tid, nullptr);
}