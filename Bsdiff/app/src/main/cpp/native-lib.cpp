#include <jni.h>
#include <string>

extern "C" {
int bspatch_main(int argc, const char *argv[]);
int bsdiff_main(int argc, const char *argv[]);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_maple_bsdiff_BsdiffUtil_patch(JNIEnv *env, jclass type, jstring oldApkPath_,
                                       jstring newApkPath_, jstring patch_) {
    const char *oldApkPath = env->GetStringUTFChars(oldApkPath_, 0);
    const char *newApkPath = env->GetStringUTFChars(newApkPath_, 0);
    const char *patch = env->GetStringUTFChars(patch_, 0);

    const char *argv[] = {"", oldApkPath, newApkPath, patch};
    bspatch_main(4, argv);
    env->ReleaseStringUTFChars(oldApkPath_, oldApkPath);
    env->ReleaseStringUTFChars(newApkPath_, newApkPath);
    env->ReleaseStringUTFChars(patch_, patch);
}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_bsdiff_BsdiffUtil_diff(JNIEnv *env, jclass type, jstring oldApkPath_,
                                      jstring newApkPath_, jstring patch_) {
    const char *oldApkPath = env->GetStringUTFChars(oldApkPath_, 0);
    const char *newApkPath = env->GetStringUTFChars(newApkPath_, 0);
    const char *patch = env->GetStringUTFChars(patch_, 0);

    const char* argv[]={"bsdiff",oldApkPath,newApkPath,patch};
    bsdiff_main(4,argv);


    env->ReleaseStringUTFChars(oldApkPath_, oldApkPath);
    env->ReleaseStringUTFChars(newApkPath_, newApkPath);
    env->ReleaseStringUTFChars(patch_, patch);
}