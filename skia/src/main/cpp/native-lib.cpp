//
// Created by 10404 on 2019/7/11.
//
#include<jni.h>
#include <android/bitmap.h>
#include <malloc.h>

extern "C" {
#include "jpeglib.h"
}

typedef uint8_t BYTE;


void writeImg(BYTE *data, const char *path, int w, int h) {
    //初始化
    struct jpeg_compress_struct jpeg_sturct;
    struct jpeg_error_mgr err;
    jpeg_create_compress(&jpeg_sturct);
    jpeg_sturct.err = jpeg_std_error(&err);

    FILE *file = fopen(path, "wb");
    jpeg_stdio_dest(&jpeg_sturct, file);

    jpeg_sturct.image_width = w;
    jpeg_sturct.image_height = h;
    // true: arithmetic coding直接加载, false: human coding ,先扫描再加载
    //true:省加载内存,false:减少文件大小
    jpeg_sturct.arith_code = false;
    jpeg_sturct.optimize_coding = true;
    jpeg_sturct.in_color_space = JCS_RGB;
    jpeg_sturct.input_components = 3;
    jpeg_set_defaults(&jpeg_sturct);
    //20 优化质量,20 是个调优值(压缩速度,压缩性能平衡).
    jpeg_set_quality(&jpeg_sturct, 60, true);
    jpeg_start_compress(&jpeg_sturct, true);

    //输出数据
    JSAMPROW row_pointer[1];//记录首指针
    int row_stride = jpeg_sturct.image_width * 3;//一行的长度
    while (jpeg_sturct.next_scanline < jpeg_sturct.image_height) {
        //计算一行rgb索引
        row_pointer[0] = &data[jpeg_sturct.next_scanline * row_stride];
        jpeg_write_scanlines(&jpeg_sturct, row_pointer, 1);
    }
    jpeg_finish_compress(&jpeg_sturct);
    jpeg_destroy_compress(&jpeg_sturct);
    fclose(file);
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_maple_skia_NativeCompress_nativeCompress__Landroid_graphics_Bitmap_2Ljava_lang_String_2(
        JNIEnv *env, jclass type, jobject inputBitmap, jstring absolutePath_) {
    const char *absolutePath = env->GetStringUTFChars(absolutePath_, 0);


    //图像源,rgb数组.
    BYTE *pixels;
    AndroidBitmapInfo bitmapInfo;
    AndroidBitmap_getInfo(env, inputBitmap, &bitmapInfo);
    AndroidBitmap_lockPixels(env, inputBitmap, reinterpret_cast<void **>(&pixels));
    int w = bitmapInfo.width;
    int h = bitmapInfo.height;
    int i = 0;
    int j = 0;
    int color;
    BYTE *data = NULL, *tmpData = NULL;

    BYTE r, g, b;
    data = static_cast<BYTE *>(malloc(w * h * 3));
    tmpData = data;
    for (i = 0; i < h; ++i) {
        for (j = 0; j < w; ++j) {
            // color = reinterpret_cast<int>(pixels);
            color = *((int *) pixels);
            r = ((color & 0x00ff0000) >> 16);

            g = ((color & 0x0000ff00) >> 8);

            b = ((color & 0x000000ff));
            *data = b;
            *(data + 1) = g;
            *(data + 2) = r;
            data += 3;
            pixels += 4;
        }
    }
    //jpeg压缩
    writeImg(tmpData, absolutePath, w, h);
    env->ReleaseStringUTFChars(absolutePath_, absolutePath);

    return env->NewStringUTF("finished");
}
