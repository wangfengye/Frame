#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>
#include <unistd.h>


#include "libyuv.h"


extern "C" {
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"

#include "libswscale/swscale.h"

}

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"native",FORMAT,##__VA_ARGS__);

extern "C"
JNIEXPORT void JNICALL
Java_com_maple_ffmpeg_VideoUtil_decode(JNIEnv *env, jclass type, jstring in_, jstring out_) {
    const char *in = env->GetStringUTFChars(in_, 0);
    const char *out = env->GetStringUTFChars(out_, 0);
    // 注册全部组件
    avcodec_register_all();
    AVFormatContext *pFormatCtx = avformat_alloc_context();
    //打开输入视频文件
    if (avformat_open_input(&pFormatCtx, in, nullptr, nullptr) != 0) {
        LOGE("%s", "无法打开视频");
        return;
    }
    if (avformat_find_stream_info(pFormatCtx, nullptr) < 0) {
        LOGE("%s", "视频信息获取失败");
        return;
    }
    int v_stream_idx = -1;
    int i = 0;
    // 遍历所有流,找出视频流
    for (; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            v_stream_idx = i;
            break;
        }
    }
    if (v_stream_idx == -1) {
        LOGE("%s", "找不到视频流");
        return;
    }
    AVCodecContext *pCodecCtx = pFormatCtx->streams[v_stream_idx]->codec;
    AVCodec *pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
    if (pCodec == NULL) {
        LOGE("%s", "找不到解码器");
        return;
    }
    if (avcodec_open2(pCodecCtx, pCodec, nullptr) < 0) {
        LOGE("%s", "打不开解码器");
        return;
    }
    LOGI("文件格式: %s", pFormatCtx->iformat->name);
    long time = -1;
    AVStream *avStream = pFormatCtx->streams[v_stream_idx];
    int den = avStream->time_base.den;
    int num = avStream->time_base.num;
    if (pFormatCtx->duration != AV_NOPTS_VALUE) {
        time = (long) (pFormatCtx->duration * (num * 1.0 / den) / 1000);
    } else {
        time = (long) (avStream->duration * (num * 1.0 / den));
    }
    time = (long) (pFormatCtx->duration * (num * 1.0 / den) / 1000);
    LOGI("视频时长：%ld s", time);
    time = (long) (avStream->duration * (num * 1.0 / den));
    LOGI("视频时长：%ld s", time);
    LOGI("视频的宽高：%d,%d", pCodecCtx->width, pCodecCtx->height);
    LOGI("解码器的名称：%s", pCodec->name);
    // 开辟缓冲区
    AVPacket *packet = static_cast<AVPacket *>(av_malloc(sizeof(AVPacket)));
    //帧数据
    AVFrame *pFrame = av_frame_alloc();
    //YUV420
    AVFrame *pFrameYUV = av_frame_alloc();
    //只有指定了AVFrame的像素格式、画面大小才能真正分配内存
    //缓冲区分配内存
    uint8_t *out_buffer = (uint8_t *) av_malloc(
            avpicture_get_size(AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height));
    avpicture_fill(reinterpret_cast<AVPicture *>(pFrameYUV), out_buffer, AV_PIX_FMT_YUV420P,
                   pCodecCtx->width, pCodecCtx->height);
    //用于转码（缩放）的参数，转之前的宽高，转之后的宽高，格式等
    struct SwsContext *swsContext = sws_getContext(pCodecCtx->width, pCodecCtx->height,
                                                   pCodecCtx->pix_fmt,
                                                   pCodecCtx->width, pCodecCtx->height,
                                                   AV_PIX_FMT_YUV420P, SWS_BICUBIC, NULL, NULL,
                                                   NULL);
    int got_picture, ret;
    FILE *fp_yuv = fopen(out, "wb+");
    int frame_count = 0;
    //逐帧堆区数
    while (av_read_frame(pFormatCtx, packet) >= 0) {
        if (packet->stream_index == v_stream_idx) {
            // 解码一帧数据
            ret = avcodec_decode_video2(pCodecCtx, pFrame, &got_picture, packet);
            if (ret < 0) {
                LOGE("%s", "解码异常");
                return;
            }
            if (got_picture) {
                //AVFrame转为像素格式YUV420，宽高
                //2 6输入、输出数据
                //3 7输入、输出画面一行的数据的大小 AVFrame 转换是一行一行转换的
                //4 输入数据第一列要转码的位置 从0开始
                //5 输入画面的高度
                sws_scale(swsContext, pFrame->data, pFrame->linesize, 0, pCodecCtx->height,
                          pFrameYUV->data, pFrameYUV->linesize);

                //输出到YUV文件
                //AVFrame像素帧写入文件
                //data解码后的图像像素数据（音频采样数据）
                //Y 亮度 UV 色度（压缩了） 人对亮度更加敏感
                //U V 个数是Y的1/4
                int y_size = pCodecCtx->width * pCodecCtx->height;
                fwrite(pFrameYUV->data[0], 1, y_size, fp_yuv);
                fwrite(pFrameYUV->data[1], 1, y_size / 4, fp_yuv);
                fwrite(pFrameYUV->data[2], 1, y_size / 4, fp_yuv);

                frame_count++;
                LOGI("解码第%d帧", frame_count);
            }
        }
        av_free_packet(packet);
    }
    fclose(fp_yuv);
    env->ReleaseStringUTFChars(in_, in);
    env->ReleaseStringUTFChars(out_, out);
}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_ffmpeg_VideoPlayer_render(JNIEnv *env, jclass type, jstring input_,
                                         jobject surface) {
    const char *input = env->GetStringUTFChars(input_, 0);
    // 注册全部组件
    avcodec_register_all();
    AVFormatContext *pFormatCtx = avformat_alloc_context();
    //打开输入视频文件
    if (avformat_open_input(&pFormatCtx, input, nullptr, nullptr) != 0) {
        LOGE("%s", "无法打开视频");
        return;
    }
    if (avformat_find_stream_info(pFormatCtx, nullptr) < 0) {
        LOGE("%s", "视频信息获取失败");
        return;
    }
    int v_stream_idx = -1;
    int i = 0;
    // 遍历所有流,找出视频流(其他,字幕,音频等)
    for (; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            v_stream_idx = i;
            break;
        }
    }
    if (v_stream_idx == -1) {
        LOGE("%s", "找不到视频流");
        return;
    }
    AVCodecContext *pCodeCtx = pFormatCtx->streams[v_stream_idx]->codec;
    AVCodec *pCodec = avcodec_find_decoder(pCodeCtx->codec_id);
    if (pCodec == NULL) {
        LOGE("%s", "找不到解码器");
        return;
    }
    if (avcodec_open2(pCodeCtx, pCodec, nullptr) < 0) {
        LOGE("%s", "打不开解码器");
        return;
    }
    LOGI("文件格式: %s", pFormatCtx->iformat->name);
    long time = -1;
    AVStream *avStream = pFormatCtx->streams[v_stream_idx];
    int den = avStream->time_base.den;
    int num = avStream->time_base.num;

    time = (long) (avStream->duration * (num * 1.0 / den));
    LOGI("视频时长：%ld s",pFormatCtx->duration/1000);
    LOGI("视频的宽高：%d,%d", pCodeCtx->width, pCodeCtx->height);
    LOGI("解码器的名称：%s", pCodec->name);
    // 开辟缓冲区
    AVPacket *packet = static_cast<AVPacket *>(av_malloc(sizeof(AVPacket)));
    //帧数据
    AVFrame *yuv_frame = av_frame_alloc();
    AVFrame *rgb_frame = av_frame_alloc();
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_Buffer outBuffer;
    int len, got_frame, framecount = 0;
    int w = pCodeCtx->width ;
    int h = pCodeCtx->height ;

    while (av_read_frame(pFormatCtx, packet) >= 0) {
        len = avcodec_decode_video2(pCodeCtx, yuv_frame, &got_frame, packet);
        if (got_frame) {
            LOGI("解码%d帧",framecount++);
            ANativeWindow_setBuffersGeometry(nativeWindow, w, h,
                                             WINDOW_FORMAT_RGBA_8888);
            ANativeWindow_lock(nativeWindow, &outBuffer, NULL);
            avpicture_fill((AVPicture *)rgb_frame, static_cast<const uint8_t *>(outBuffer.bits), AV_PIX_FMT_RGBA, pCodeCtx->width, pCodeCtx->height);
//            av_image_fill_arrays(rgb_frame->data, rgb_frame->linesize,
//                                 static_cast<const uint8_t *>(outBuffer.bits), pCodeCtx->pix_fmt, w, h, 0);
            //缩放数据
/*            libyuv::I420Scale(yuv_frame->data[0], yuv_frame->linesize[0],
                              yuv_frame->data[2], yuv_frame->linesize[2],
                              yuv_frame->data[1], yuv_frame->linesize[1],
                              pCodeCtx->width, pCodeCtx->height,
                              yuv_frame_sc->data[0],w,
                              yuv_frame_sc->data[2],w>>1,
                              yuv_frame_sc->data[1], w>>1,
                              w, h,
                              (libyuv::FilterMode) 0);*/
            //yuv0->RGBA_8888
            libyuv::I420ToARGB(yuv_frame->data[0], yuv_frame->linesize[0],
                               yuv_frame->data[2], yuv_frame->linesize[2],
                               yuv_frame->data[1], yuv_frame->linesize[1],
                               rgb_frame->data[0], rgb_frame->linesize[0],
                               w, h);
            ANativeWindow_unlockAndPost(nativeWindow);
            usleep(1000 * 16);
        }
        av_free_packet(packet);
    }
    ANativeWindow_release(nativeWindow);
    av_frame_free(&yuv_frame);
    avcodec_close(pCodeCtx);
    avformat_free_context(pFormatCtx);
    env->ReleaseStringUTFChars(input_, input);
}