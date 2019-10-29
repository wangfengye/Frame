#include <jni.h>
#include <string>


extern "C" JNIEXPORT jstring JNICALL
Java_com_maple_douyu_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_startPush(JNIEnv *env, jobject instance) {

    // TODO

}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_stopPush(JNIEnv *env, jobject instance) {

    // TODO

}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_release(JNIEnv *env, jobject instance) {

    // TODO

}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_setVideoOptions(JNIEnv *env, jobject instance, jint width,
                                                     jint height, jint birate, jint fps) {

    // TODO

}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_setAudioOptions(JNIEnv *env, jobject instance,
                                                     jint sampleRateInHz, jint channel) {

    // TODO

}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_fireVideo(JNIEnv *env, jobject instance, jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // TODO

    env->ReleaseByteArrayElements(data_, data, 0);
}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_fireAudio(JNIEnv *env, jobject instance, jbyteArray data_,
                                               jint len) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // TODO

    env->ReleaseByteArrayElements(data_, data, 0);
}