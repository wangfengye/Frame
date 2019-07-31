//
// Created by 10404 on 2019/7/17.
//

#include <jni.h>
#include<android/log.h>
#include "utils.h"
#include <opencv2/opencv.hpp>


#define  DEFAULT_CARD_SIZE Size(640,400)

using namespace std;
using namespace cv;


extern "C"
JNIEXPORT jobject JNICALL
Java_com_maple_ioc_Ocr_getCardIdBitmap(JNIEnv *env, jclass type, jobject bitmap, jobject config,jint i) {
    Mat src_img;
    Mat dst_img;
    bitmap2Mat(env, bitmap, &src_img);//填充src_img;

    // 归一化,规范图片
    Mat dst;
    resize(src_img, dst, DEFAULT_CARD_SIZE);
    // 灰度化
    cvtColor(src_img, dst, COLOR_RGBA2GRAY);
    if (i==1)return createBitmap(env, dst, config);
    // 二值化
    threshold(dst, dst, 50, 255, THRESH_BINARY);
    if (i==2)return createBitmap(env, dst, config);
    // 膨胀处理
    Mat erodeElement = getStructuringElement(MORPH_RECT, Size(40, 10));
    erode(dst, dst, erodeElement);
    if (i==3)return createBitmap(env, dst, config);
    // 轮廓检测
    vector<vector<Point>> contours;
    findContours(dst, contours, RETR_TREE, CHAIN_APPROX_SIMPLE, Point(0, 0));

    // 获取身份证号区域
    vector<Rect> rects;
    for (int i = 0; i < contours.size(); ++i) {
        Rect rect = boundingRect(contours.at(i));
        // 身份证号长宽比 1:8~1:16
        if (rect.width > 8 * rect.height && rect.width < 16 * rect.height) {
            rects.push_back(rect);
        }
    }
    int lowPoint = 0;
    Rect finalRect;
    for (int i = 0; i < rects.size(); ++i) {
        Rect rect = rects.at(i);
        Point p = rect.tl();
        if (p.y > lowPoint) {
            lowPoint = p.y;
            finalRect = rect;
        }
    }

    // 图形分割
    dst_img = src_img(finalRect);
    // 返回更易识别的二值图
    cvtColor(dst_img, dst_img, COLOR_RGBA2GRAY);
    threshold(dst_img, dst_img, 100, 255, THRESH_BINARY);
    // 返货身份证号码图
    return createBitmap(env, dst_img, config);

}