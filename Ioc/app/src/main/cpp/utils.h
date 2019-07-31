//
// Created by 10404 on 2019/7/19.
//


#ifndef FRAME_UTILS_H
#define FRAME_UTILS_H

#include <android/bitmap.h>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C" {
void bitmap2Mat(JNIEnv *env, jobject bitmap, Mat *mat, bool needAlpha = 0);
void mat2Bitmap(JNIEnv *env, jobject bitmap, Mat mat, bool needAlpha = 0);
jobject createBitmap(JNIEnv *env, Mat srcData, jobject config);
}

#endif //FRAME_UTILS_H