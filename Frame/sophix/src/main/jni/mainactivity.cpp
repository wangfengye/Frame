#include <jni.h>
#include "dalvik.h"
#include <android/log.h>

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native",FORMAT,##__VA_ARGS__)



extern "C"
JNIEXPORT void JNICALL
Java_com_maple_sophix_util_SopHix_replace(JNIEnv *env, jclass type, jobject wrongMethod,
                                          jobject method) {
    // 修改方法表
    /* int i=0;
      ArtMethod *wrong = reinterpret_cast<art::mirror::ArtMethod *>(env->FromReflectedMethod(wrongMethod));LOGI("%d",i++);
      ArtMethod *right = reinterpret_cast<art::mirror::ArtMethod *>(env->FromReflectedMethod(method));LOGI("%d",i++);
      wrong->declaring_class =right->declaring_class;LOGI("%d",i++);
      wrong->access_flags_ =right->access_flags_;LOGI("%d",i++);
      wrong->dex_cache_resolved_methods_=right->dex_cache_resolved_methods_;LOGI("%d",i++);
      wrong->dex_cache_resolved_types_=right->dex_cache_resolved_types_;LOGI("%d",i++);
      wrong->dex_code_item_offset_=right->dex_code_item_offset_;LOGI("%d",i++);
      wrong->dex_method_index_=right->dex_method_index_;LOGI("%d",i++);
      wrong->method_index_=right->method_index_;LOGI("%d",i++);*/

    Method *meth= (Method *) env->FromReflectedMethod(wrongMethod);
    Method *target=(Method *)env->FromReflectedMethod(method);
    meth->clazz=target->clazz;
    meth->accessFlags=target->accessFlags;
    meth->methodIndex=target->methodIndex;
    meth->jniArgInfo=target->jniArgInfo;
    meth->registersSize=target->registersSize;
    meth->outsSize=target->outsSize;
    meth->insns=target->insns;
    meth->insSize=meth->insSize;
    meth->nativeFunc=target->nativeFunc;
}

