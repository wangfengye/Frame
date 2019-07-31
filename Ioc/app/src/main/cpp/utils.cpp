//
// Created by 10404 on 2019/7/19.
//



#include <android/bitmap.h>
#include <android/log.h>
#include "utils.h"
#define  TAG "cardocr"
#define  LOGI(...)   __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__);

void bitmap2Mat(JNIEnv *env, jobject bitmap, Mat *mat, bool needAlpha) {
    AndroidBitmapInfo info;
    void *pixels = 0;
    Mat &dst = *mat;
    CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);

    CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
              info.format == ANDROID_BITMAP_FORMAT_RGB_565);

    // lock获取数据
    CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);

    CV_Assert(pixels);

    LOGI("原图大小%d,%d",info.width,info.height);
    dst.create(info.height, info.width, CV_8UC4);
    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        Mat tmp(info.height, info.width, CV_8UC4, pixels);
        if (needAlpha)cvtColor(tmp, dst, COLOR_mRGBA2RGBA);
        else tmp.copyTo(dst);
    } else {
        Mat tmp(info.height, info.width, CV_8UC2, pixels);
        cvtColor(tmp, dst, COLOR_BGR5652BGRA);
    }
    AndroidBitmap_unlockPixels(env, bitmap);

}

void mat2Bitmap(JNIEnv *env, jobject bitmap, Mat mat, bool needAlpha) {
    AndroidBitmapInfo info;
    void *pixels = 0;

    CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);

    // rgb8888 rgb565;
    CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
              info.format == ANDROID_BITMAP_FORMAT_RGB_565);

    CV_Assert(mat.dims == 2 && info.height == (uint32_t) mat.rows &&
              info.width == (uint32_t) mat.cols);
    CV_Assert(mat.type() == CV_8UC1 || mat.type() == CV_8UC3 || mat.type() == CV_8UC4);
    CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);

    CV_Assert(pixels);
    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        Mat tmp(info.height, info.width, CV_8UC4, pixels);
        if (mat.type() == CV_8UC1) {
            cvtColor(mat, tmp, COLOR_GRAY2RGBA);
        } else if (mat.type() == CV_8UC3) {
            cvtColor(mat, tmp, COLOR_RGB2RGBA);
        } else if (mat.type() == CV_8UC4) {
            if (needAlpha)cvtColor(mat, tmp, COLOR_RGBA2mRGBA);
            else mat.copyTo(tmp);
        }
    } else {
        Mat tmp(info.height, info.width, CV_8UC2, pixels);
        if (mat.type() == CV_8UC1) {
            cvtColor(mat, tmp, COLOR_GRAY2BGR565);
        } else if (mat.type() == CV_8UC3) {
            cvtColor(mat, tmp, COLOR_RGB2BGR565);
        } else if (mat.type() == CV_8UC4) {
            cvtColor(mat, tmp, COLOR_RGBA2BGR565);
        }
    }
    AndroidBitmap_unlockPixels(env, bitmap);
}

jobject createBitmap(JNIEnv *env, Mat srcData, jobject config) {
    jclass java_bitmap_class = (jclass) env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetStaticMethodID(java_bitmap_class, "createBitmap",
                                           "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jobject bitmap = env->CallStaticObjectMethod(java_bitmap_class, mid, srcData.cols, srcData.rows,
                                                 config);
    mat2Bitmap(env, bitmap, srcData, 0);
    return bitmap;
}
