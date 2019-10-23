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
#include "libswresample/swresample.h"
#include "libavutil/frame.h"
#include "libavutil/mem.h"

}

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"native",FORMAT,##__VA_ARGS__);
#define MAX_AUDIO_FRME_SIZE 48000 * 4
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
}

extern "C"
JNIEXPORT void JNICALL
Java_com_maple_ffmpeg_VideoPlayer_render(JNIEnv *env, jclass type, jstring input_,
                                         jobject surface) {
    const char *input = env->GetStringUTFChars(input_, 0);
    av_register_all();
    LOGE("注册成功")
    AVFormatContext *avFormatContext = avformat_alloc_context();//获取上下文
    int error;
    char buf[] = "";
    //打开视频地址并获取里面的内容(解封装)
    if (error = avformat_open_input(&avFormatContext, input, NULL, NULL) < 0) {
        av_strerror(error, buf, 1024);
        // LOGE("%s" ,inputPath)
        LOGE("Couldn't open file %s: %d(%s)", input, error, buf);
        // LOGE("%d",error)
        LOGE("打开视频失败")
        return;
    }
    if (avformat_find_stream_info(avFormatContext, NULL) < 0) {
        LOGE("获取内容失败")
        return;
    }
    //获取到整个内容过后找到里面的视频流
    int video_index = -1;
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        if (avFormatContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            //如果是视频流,标记一哈
            video_index = i;
        }
    }
    LOGE("成功找到视频流")
    //对视频流进行解码
    //获取解码器上下文
    AVCodecContext *avCodecContext = avFormatContext->streams[video_index]->codec;
    //获取解码器
    AVCodec *avCodec = avcodec_find_decoder(avCodecContext->codec_id);
    //打开解码器
    if (avcodec_open2(avCodecContext, avCodec, NULL) < 0) {
        LOGE("打开失败")
        return;
    }

    //申请AVPacket
    AVPacket *packet = (AVPacket *) av_malloc(sizeof(AVPacket));
    av_init_packet(packet);
    //申请AVFrame
    AVFrame *frame = av_frame_alloc();//分配一个AVFrame结构体,AVFrame结构体一般用于存储原始数据，指向解码后的原始帧
    AVFrame *rgb_frame = av_frame_alloc();//分配一个AVFrame结构体，指向存放转换成rgb后的帧
    //输出文件
    //FILE *fp = fopen(outputPath,"wb");


    //缓存区

    //与缓存区相关联，设置rgb_frame缓存区



    SwsContext *swsContext= nullptr;

    //取到nativewindow
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);

    if (nativeWindow == 0) {
        LOGE("nativewindow取到失败")
        return;
    }
    //视频缓冲区
    ANativeWindow_Buffer native_outBuffer;


    int dw=0;int dh=0;
    int frameCount;
    int h = 0;
    LOGE("解码 ")
    while (av_read_frame(avFormatContext, packet) >= 0) {
        LOGE("解码 %d", packet->stream_index)
        LOGE("VINDEX %d", video_index)
        if (packet->stream_index == video_index) {
            LOGE("解码 hhhhh")
            //如果是视频流
            //解码
            avcodec_decode_video2(avCodecContext, frame, &frameCount, packet);
            LOGE("解码中....  %d", frameCount)
            if (frameCount) {
                LOGE("转换并绘制")
                //说明有内容
                //绘制之前配置nativewindow
                ANativeWindow_setBuffersGeometry(nativeWindow, avCodecContext->width,
                                                 avCodecContext->height, WINDOW_FORMAT_RGBA_8888);
                //上锁
                ANativeWindow_lock(nativeWindow, &native_outBuffer, NULL);
                if (swsContext==NULL){
                    LOGI("avCodecContext: %d,%d",avCodecContext->width,avCodecContext->height);
                    LOGI("native_outBuffer: %d,%d",native_outBuffer.width,native_outBuffer.height);
                    dw=native_outBuffer.width;
                    dh= dw*avCodecContext->height/avCodecContext->width;
                    swsContext = sws_getContext(avCodecContext->width, avCodecContext->height,
                                     avCodecContext->pix_fmt,
                                     dw, dh,
                                     AV_PIX_FMT_RGBA,
                                     SWS_BICUBIC, NULL, NULL, NULL);
                    uint8_t *out_buffer = (uint8_t *) av_malloc(avpicture_get_size(AV_PIX_FMT_RGBA,
                                                                                   dw,
                                                                                  dh));
                    avpicture_fill((AVPicture *) rgb_frame, out_buffer, AV_PIX_FMT_RGBA, dw,
                                  dh);
                }

                //转换为rgb格式
                sws_scale(swsContext, (const uint8_t *const *) frame->data, frame->linesize, 0,
                          frame->height, rgb_frame->data,
                          rgb_frame->linesize);
                //  rgb_frame是有画面数据
                uint8_t *dst = (uint8_t *) native_outBuffer.bits;
                //拿到一行有多少个字节 RGBA
                int destStride = native_outBuffer.stride * 4;
                //像素数据的首地址
                uint8_t *src = rgb_frame->data[0];
                //  实际内存一行数量
                int srcStride = rgb_frame->linesize[0];
                //int i=0;
                for (int i = 0; i < dh; ++i) {
                // memcpy(void *dest, const void *src, size_t n)
                    //将rgb_frame中每一行的数据复制给nativewindow
                    memcpy(dst + i * destStride, src + i * srcStride, srcStride);
                }
//解锁
                ANativeWindow_unlockAndPost(nativeWindow);
                usleep(1000 * 16);

            }
        }
        av_free_packet(packet);
    }
    //释放
    ANativeWindow_release(nativeWindow);
    av_frame_free(&frame);
    av_frame_free(&rgb_frame);
    avcodec_close(avCodecContext);
    avformat_free_context(avFormatContext);

    env->ReleaseStringUTFChars(input_, input);
}


