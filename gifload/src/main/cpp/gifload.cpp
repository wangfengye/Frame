//
// Created by 10404 on 2019/7/17.
//

#include <jni.h>
#include <android/bitmap.h>
#include "giflib/gif_lib.h"


#include "gif.h"


extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_gifload_GifLoader_openFile(JNIEnv *env, jobject instance, jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);

    int err;
    GifFileType *gif = DGifOpenFileName(path, &err);
    err = DGifSlurp(gif);

    env->ReleaseStringUTFChars(path_, path);
    return reinterpret_cast<jlong>(gif);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_gifload_GifLoader_getWidth(JNIEnv *env, jobject instance, jlong gifInfo) {
    return ((GifFileType *) gifInfo)->SWidth;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_gifload_GifLoader_getHeight(JNIEnv *env, jobject instance, jlong gifInfo) {
    return ((GifFileType *) gifInfo)->SHeight;

}extern "C"
JNIEXPORT jint JNICALL
Java_com_example_gifload_GifLoader_getLength(JNIEnv *env, jobject instance, jlong gifInfo) {
    return ((GifFileType *) gifInfo)->ImageCount;
}extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_gifload_GifLoader_renderFrameN(JNIEnv *env, jobject instance, jlong gifInfo,
                                                jobject bitmap, jint index) {

    GifFileType *gifFileType = reinterpret_cast<GifFileType *>(gifInfo);
    AndroidBitmapInfo info;
    void *pixels;
    int ret;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        return -1;//-1表示传入数据异常
    }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        return -1;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        return -2;//lock异常
    }
    //渲染
    int delay_time = drawFrame(gifFileType, &info, (int *) pixels, index, false);
    AndroidBitmap_unlockPixels(env, bitmap);
    return delay_time;

}

#define   argb(a, r, g, b)(((a)&0xff)<<24)|(((0)&0xff)<<16)|(((0)&0xff)<<8)|((0)&0xff)
#define dispose(ext)(((ext)->Bytes[0]&0x1c)>>2)
#define trans_index(ext)((ext)->Bytes[3])
#define transparency(ext)((ext)->Bytes[0] &1)
#define  delay(ext)(10*((ext)->Bytes[2]<<8|(ext)->Bytes[1]))

// 更新矩阵,获取下一帧延时
int drawFrame(GifFileType *gif, AndroidBitmapInfo *info, int *pixels, int frame_no,
              bool force_dispose_l) {
    GifColorType *bg;
    GifColorType *color;
    SavedImage *frame;
    ExtensionBlock *ext = 0;
    GifImageDesc *frameInfo;
    ColorMapObject *colorMap;
    int *line;
    int width, height, x, y, j, loc, n, inc, p;
    int *px;
    width = gif->SWidth;
    height = gif->SHeight;
    frame = &(gif->SavedImages[frame_no]);
    frameInfo = &(frame->ImageDesc);
    if (frameInfo->ColorMap) {
        colorMap = frameInfo->ColorMap;
    } else {
        colorMap = gif->SColorMap;
    }
    bg = &colorMap->Colors[gif->SBackGroundColor];
    for (j = 0; j < frame->ExtensionBlockCount; ++j) {
        if (frame->ExtensionBlocks[j].Function == GRAPHICS_EXT_FUNC_CODE) {
            ext = &(frame->ExtensionBlocks[j]);
            break;
        }
    }
    px = pixels;
    if (ext && dispose(ext) == 1 && force_dispose_l && frame_no > 0) {
        drawFrame(gif, info, pixels, frame_no - 1, true);
    } else if (ext && dispose(ext) == 2 && bg) {
        for (y = 0; y < height; y++) {
            line = (int *) px;
            for (x = 0; x < width; x++) {
                line[x] = argb(255, bg->Red, bg->Green, bg->Blue);
            }
            px = (int *) ((char *) px + info->stride);
        }
    } else if (ext && dispose(ext) == 3 && frame_no > 1) {
        drawFrame(gif, info, pixels, frame_no - 2, true);
    }
    px = pixels;
    if (frameInfo->Interlace) {
        n = 0;
        inc = 8;
        p = 0;
        px = (int *) ((char *) px + info->stride * frameInfo->Top);
        for (y = frameInfo->Top; y < frameInfo->Top + frameInfo->Height; y++) {
            for (x = frameInfo->Left; x < frameInfo->Left + frameInfo->Width; ++x) {
                loc = (y - frameInfo->Top) * frameInfo->Width + (x - frameInfo->Left);
                if (ext && frame->RasterBits[loc] == trans_index(ext) && transparency(ext)) {
                    continue;
                }
                color = (ext && frame->RasterBits[loc] == trans_index(ext)) ? bg
                                                                            : &colorMap->Colors[frame->RasterBits[loc]];
                if (color)line[x] = argb(255, color->Red, color->Green, color->Blue);
            }
            px = (int *) ((char *) px + info->stride * inc);
            n += inc;
            if (n >= frameInfo->Height) {
                n = 0;
                switch (p) {
                    case 0:
                        px = (int *) ((char *) pixels + info->stride * (4 + frameInfo->Top));
                        inc = 8;
                        p++;
                        break;
                    case 1:
                        px = (int *) ((char *) pixels + info->stride * (2 + frameInfo->Top));
                        inc = 4;
                        p++;
                        break;
                    case 2:
                        px = (int *) ((char *) pixels + info->stride * (1 + frameInfo->Top));
                        inc = 1;
                        p++;
                        break;
                }
            }
        }

    } else {
        px = (int *) ((char *) pixels + info->stride * frameInfo->Top);
        for (y = frameInfo->Top; y < frameInfo->Top + frameInfo->Height; y++) {
            line = (int *) px;
            for (x = frameInfo->Left; x < frameInfo->Left + frameInfo->Width; x++) {
                loc = (y - frameInfo->Top) * frameInfo->Width + (x - frameInfo->Left);
                if (ext && frame->RasterBits[loc] == trans_index(ext) && transparency(ext)) {
                    continue;
                }
                color = (ext && frame->RasterBits[loc] == trans_index(ext)) ? bg
                                                                            : &colorMap->Colors[frame->RasterBits[loc]];
                if (color)line[x] = argb(255, color->Red, color->Green, color->Blue);
            }
            px = (int *) ((char *) px + info->stride);
        }
    }
    return delay(ext);
}