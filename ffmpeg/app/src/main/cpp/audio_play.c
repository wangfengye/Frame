//
// Created by 10404 on 2019/10/14.
//
#include <jni.h>
#include <android/log.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>
#include <unistd.h>
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"

#include "libswscale/swscale.h"
#include "libswresample/swresample.h"
#include "libavutil/frame.h"
#include "libavutil/mem.h"

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"native",FORMAT,##__VA_ARGS__);

static void decode(AVCodecContext *dec_ctx, AVPacket *pkt, AVFrame *frame,
                   FILE *outfile) {
    int i, ch;
    int ret, data_size;
    LOGI("%d",0)
    /* send the packet with the compressed data to the decoder */
    ret = avcodec_send_packet(dec_ctx, pkt);
    if (ret < 0) {
        fprintf(stderr, "Error submitting the packet to the decoder\n");
        exit(1);
    }
    LOGI("%d",1)
    /* read all the output frames (in general there may be any number of them */
    while (ret >= 0) {    LOGI("%d",2);
        ret = avcodec_receive_frame(dec_ctx, frame);
        if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF)
            return;
        else if (ret < 0) {
            fprintf(stderr, "Error during decoding\n");
            exit(1);
        }
        data_size = av_get_bytes_per_sample(dec_ctx->sample_fmt);
        if (data_size < 0) {
            /* This should not occur, checking just for paranoia */
            fprintf(stderr, "Failed to calculate data size\n");
            exit(1);
        }    LOGI("%d",3)
        for (i = 0; i < frame->nb_samples; i++)
            for (ch = 0; ch < dec_ctx->channels; ch++)
                fwrite(frame->data[ch] + data_size * i, 1, data_size, outfile);
    }
}

#define AUDIO_INBUF_SIZE 20480
#define AUDIO_REFILL_THRESH 4096

JNIEXPORT void JNICALL
Java_com_maple_ffmpeg_VideoPlayer_sound(JNIEnv *env, jclass type, jstring input_, jstring output_) {
    const char *input = (*env)->GetStringUTFChars(env, input_, 0);
    const char *output = (*env)->GetStringUTFChars(env, output_, 0);
    LOGI("%s", "SOUND NDK START");
    const AVCodec *codec;
    AVCodecContext *c = NULL;
    AVCodecParserContext *parser = NULL;
    int len, ret;
    FILE *f, *outfile;
    uint8_t inbuf[AUDIO_INBUF_SIZE + AV_INPUT_BUFFER_PADDING_SIZE];
    uint8_t *data;
    size_t data_size;
    AVPacket *pkt;
    AVFrame *decode_frame = NULL;
    pkt = av_packet_alloc();
    avcodec_register_all();
    AVFormatContext *pFormatCtx = avformat_alloc_context();
//打开输入视频文件
    if (avformat_open_input(&pFormatCtx, input, NULL, NULL) != 0) {
        LOGE("%s", "无法打开视频");
        return;
    }
    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
        LOGE("%s", "视频信息获取失败");
        return;
    }
    int v_stream_idx = -1;
    int i = 0;
// 遍历所有流,找出视频流(其他,字幕,音频等)
    for (; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
            v_stream_idx = i;
            break;
        }
    }
    codec = avcodec_find_decoder(pFormatCtx->streams[v_stream_idx]->codecpar->codec_id);

    if (!codec) {
        LOGE("%s", "无解析器");
        return;
    }
    LOGI("%s", codec->name);
    parser = av_parser_init(codec->id);
    if (!parser) {
        fprintf(stderr, "Parser not found\n");
        exit(1);
    }
    c = avcodec_alloc_context3(codec);
    if (!c) {
        fprintf(stderr, "Could not allocate audio codec context\n");
        exit(1);
    }
    if (avcodec_open2(c, codec, NULL) < 0) {
        fprintf(stderr, "Could not open codec\n");
        exit(1);
    }
    f = fopen(input, "rb");
    outfile = fopen(output, "wb");
    data = inbuf;
    data_size = fread(inbuf, 1, AUDIO_INBUF_SIZE, f);
    LOGI("%s", "进入循环");
    while (data_size > 0) {
        LOGI("%s", "loop");
        if (!decode_frame) {
            if (!(decode_frame = av_frame_alloc())) {
                LOGE("%s", "av_frame_alloc 异常");
                exit(1);
            }
        }
        ret = av_parser_parse2(parser, c, &pkt->data, &pkt->size, data, data_size,
                               AV_NOPTS_VALUE, AV_NOPTS_VALUE, 0);
        if (ret < 0) {
            LOGE("Ret = %d", ret);
            exit(1);
        }
        data += ret;
        data_size -= ret;
        if (pkt->size) {
            decode(c, pkt, decode_frame, outfile);
        }
        if (data_size < AUDIO_REFILL_THRESH) {
            memmove(inbuf, data, data_size);
            data = inbuf;
            len = fread(data + data_size, 1,
                        AUDIO_INBUF_SIZE - data_size, f);
            if (len > 0)
                data_size += len;
        }
    }
/* flush the decoder */
    pkt->data = NULL;
    pkt->size = 0;
    decode(c, pkt, decode_frame, outfile);

    fclose(outfile);
    fclose(f);

    avcodec_free_context(&c);
    av_parser_close(parser);
    av_frame_free(&decode_frame);
    av_packet_free(&pkt);

    LOGI("%s", "SOUND NDK FINISH");

    (*env)->ReleaseStringUTFChars(env, input_, input);
    (*env)->ReleaseStringUTFChars(env, output_, output);
}