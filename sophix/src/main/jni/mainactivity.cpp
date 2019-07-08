#include <jni.h>
#include "art_method.h"


extern "C"
JNIEXPORT void JNICALL
Java_com_maple_sophix_util_SopHix_replace(JNIEnv *env, jclass type, jobject wrongMethod,
                                          jobject method) {
    // 修改方法表
    art::mirror::ArtMethod *wrong = reinterpret_cast<art::mirror::ArtMethod *>(env->FromReflectedMethod(wrongMethod));
    art::mirror::ArtMethod *right = reinterpret_cast<art::mirror::ArtMethod *>(env->FromReflectedMethod(method));
    wrong->declaring_class =right->declaring_class;
    wrong->access_flags_ =right->access_flags_;
    wrong->dex_cache_resolved_methods_=right->dex_cache_resolved_methods_;
    wrong->dex_cache_resolved_types_=right->dex_cache_resolved_types_;
    wrong->dex_code_item_offset_=right->dex_code_item_offset_;
    wrong->dex_method_index_=right->dex_method_index_;
    wrong->method_index_=right->method_index_;


}

