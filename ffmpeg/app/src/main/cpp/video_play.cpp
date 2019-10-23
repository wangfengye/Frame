//
// Created by 10404 on 2019/10/22.
//

#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>
#include <unistd.h>
#include <pthread.h>

#include "libyuv.h"
// open sl



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
#define ARRAY_LEN(a) (sizeof(a) / sizeof(a[0]))
//预留视频,音频,字幕,三个流的上下文位置
constexpr int MAX_STREAM = 3;
constexpr int MAX_AUDIO_FRME_SIZE = 48000 * 4;


extern "C" {
typedef struct Player {
    JavaVM *javaVM;
    // 上下文
    AVFormatContext *input_format_ctx;
    // 音视频索引
    int audio_stream_index;
    int video_stream_index;
    //解码器上下文数组
    AVCodecContext *input_codec_ctx[MAX_STREAM];
    pthread_t decode_threads[MAX_STREAM];
    ANativeWindow *nativeWindow;
    SwrContext *swr_ctx;
    //输入的采样格式
    enum AVSampleFormat in_sample_fmt;
    //输出采样格式16bit PCM
    enum AVSampleFormat out_sample_fmt;
    //输入采样率
    int in_sample_rate;
    //输出采样率
    int out_sample_rate;
    //输出的声道个数
    int out_channel_nb;

    //JNI
    jobject audio_track;
    jmethodID audio_track_write_mid;
} Player;

/**
 * 初始化.获取音视频索引
 * @param player 自定义封装对象
 * @param input_cstr 视频地址.
 */
void init_input_format_ctx(Player *player, const char *input_cstr) {
    //注册组件
    av_register_all();
    //封装格式上下文
    AVFormatContext *format_ctx = avformat_alloc_context();

    //打开输入视频文件
    if (avformat_open_input(&format_ctx, input_cstr, nullptr, nullptr) != 0) {
        LOGE("%s", "打开输入视频文件失败");
        return;
    }
    //获取视频信息
    if (avformat_find_stream_info(format_ctx, nullptr) < 0) {
        LOGE("%s", "获取视频信息失败");
        return;
    }

    //视频解码，需要找到视频对应的AVStream所在format_ctx->streams的索引位置
    //获取音频和视频流的索引位置
    int i;
    for (i = 0; i < format_ctx->nb_streams; i++) {
        if (format_ctx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            player->video_stream_index = i;
        } else if (format_ctx->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
            player->audio_stream_index = i;
        }
    }
    player->input_format_ctx = format_ctx;
}

/**
 * 初始化解码器上下文
 */
void init_codec_context(struct Player *player, int stream_idx) {
    AVFormatContext *format_ctx = player->input_format_ctx;
    //获取解码器
    LOGI("init_codec_context begin: %d", stream_idx);
    AVCodecContext *codec_ctx = format_ctx->streams[stream_idx]->codec;
    LOGI("init_codec_context end");
    AVCodec *codec = avcodec_find_decoder(codec_ctx->codec_id);
    if (codec == nullptr) {
        LOGE("%s", "无法解码");
        return;
    }
    //打开解码器
    if (avcodec_open2(codec_ctx, codec, nullptr) < 0) {
        LOGE("%s", "解码器无法打开");
        return;
    }

    player->input_codec_ctx[stream_idx] = codec_ctx;
}
void decode_videoFrame(Player *player, AVPacket *packet) {
    AVFrame *yuv_frame = av_frame_alloc();
    AVFrame *rgb_frame = av_frame_alloc();
    //绘制时的缓冲区
    ANativeWindow_Buffer outBuffer;
    AVCodecContext *codec_ctx = player->input_codec_ctx[player->video_stream_index];
    if (codec_ctx == nullptr) {
        LOGI("codec_ctx null");
    } else {
        LOGI("codec_ctx exist");
    }
    int got_frame;
    //解码AVPacket->AVFrame
    avcodec_decode_video2(codec_ctx, yuv_frame, &got_frame, packet);
    LOGI("avcodec_decode_video2");
    //Zero if no frame could be decompresse
    //非零，正在解码
    if (got_frame) {
        //lock
        //设置缓冲区的属性（宽、高、像素格式）
        ANativeWindow_setBuffersGeometry(player->nativeWindow, codec_ctx->width, codec_ctx->height,
                                         WINDOW_FORMAT_RGBA_8888);
        ANativeWindow_lock(player->nativeWindow, &outBuffer, nullptr);

        //设置rgb_frame的属性（像素格式、宽高）和缓冲区
        //rgb_frame缓冲区与outBuffer.bits是同一块内存
        avpicture_fill((AVPicture *) rgb_frame, (const uint8_t *) outBuffer.bits, AV_PIX_FMT_RGBA,
                       codec_ctx->width, codec_ctx->height);
        //YUV->RGBA_8888
        libyuv::I420ToARGB(yuv_frame->data[0], yuv_frame->linesize[0],
                           yuv_frame->data[2], yuv_frame->linesize[2],
                           yuv_frame->data[1], yuv_frame->linesize[1],
                           rgb_frame->data[0], rgb_frame->linesize[0],
                           codec_ctx->width, codec_ctx->height);

        //unlock
        ANativeWindow_unlockAndPost(player->nativeWindow);

        usleep(1000 * 16);
    }
    av_frame_free(&yuv_frame);
    av_frame_free(&rgb_frame);
}
void *decode_video(void *arg) {
    Player *player = (Player *) arg;
    AVFormatContext *format_ctx = player->input_format_ctx;

    AVPacket *packet = (AVPacket *) av_malloc(sizeof(AVPacket));
    int video_frame_count = 0;
    while (av_read_frame(format_ctx, packet) >= 0) {
        if (packet->stream_index == player->video_stream_index) {
            LOGI("video_frame_count:%d", video_frame_count++);
            decode_videoFrame(player, packet);

        }
        av_free_packet(packet);
    }
}
void jni_audio_prepare(JNIEnv *env, jclass player_class, struct Player *player) {
    //JNI begin------------------
    //JasonPlayer


    //AudioTrack对象
    jmethodID create_audio_track_mid = env->GetStaticMethodID(player_class, "createAudioTrack",
                                                              "(II)Landroid/media/AudioTrack;");
    jobject audio_track = env->CallStaticObjectMethod(player_class, create_audio_track_mid,
                                                      player->out_sample_rate,
                                                      player->out_channel_nb);

    //调用AudioTrack.play方法
    jclass audio_track_class = env->GetObjectClass(audio_track);
    jmethodID audio_track_play_mid = env->GetMethodID(audio_track_class, "play", "()V");
    env->CallVoidMethod(audio_track, audio_track_play_mid);

    //AudioTrack.write
    jmethodID audio_track_write_mid = env->GetMethodID(audio_track_class, "write", "([BII)I");

    //JNI end------------------
    player->audio_track = env->NewGlobalRef(audio_track);
    //(*env)->DeleteGlobalRef
    player->audio_track_write_mid = audio_track_write_mid;
}
void decode_video_prepare(Player *player, JNIEnv *env, jobject surface) {
    player->nativeWindow = ANativeWindow_fromSurface(env, surface);
}
void decode_audio(struct Player *player, AVPacket *packet) {
    AVCodecContext *codec_ctx = player->input_codec_ctx[player->audio_stream_index];
    LOGI("%s", "decode_audio");
    //解压缩数据
    AVFrame *frame = av_frame_alloc();
    int got_frame;
    avcodec_decode_audio4(codec_ctx, frame, &got_frame, packet);

    //16bit 44100 PCM 数据（重采样缓冲区）
    uint8_t *out_buffer = (uint8_t *) av_malloc(MAX_AUDIO_FRME_SIZE);
    //解码一帧成功
    if (got_frame > 0) {
        swr_convert(player->swr_ctx, &out_buffer, MAX_AUDIO_FRME_SIZE,
                    (const uint8_t **) frame->data, frame->nb_samples);
        //获取sample的size
        int out_buffer_size = av_samples_get_buffer_size(NULL, player->out_channel_nb,
                                                         frame->nb_samples, player->out_sample_fmt,
                                                         1);

        //关联当前线程的JNIEnv
        JavaVM *javaVM = player->javaVM;
        JNIEnv *env;
        javaVM->AttachCurrentThread(&env, nullptr);

        //out_buffer缓冲区数据，转成byte数组
        jbyteArray audio_sample_array = env->NewByteArray(out_buffer_size);
        jbyte *sample_bytep = env->GetByteArrayElements(audio_sample_array, NULL);
        //out_buffer的数据复制到sampe_bytep
        memcpy(sample_bytep, out_buffer, out_buffer_size);
        //同步
        env->ReleaseByteArrayElements(audio_sample_array, sample_bytep, 0);

        //AudioTrack.write PCM数据
        env->CallIntMethod(player->audio_track, player->audio_track_write_mid,
                           audio_sample_array, 0, out_buffer_size);
        //释放局部引用
        env->DeleteLocalRef(audio_sample_array);

        javaVM->DetachCurrentThread();

        usleep(1000 * 16);
    }

    av_frame_free(&frame);
}


/**
 * 解码子线程函数
 */
void *decode_data(void *arg) {
    struct Player *player = (struct Player *) arg;
    AVFormatContext *format_ctx = player->input_format_ctx;
    //编码数据
    AVPacket *packet = (AVPacket *) av_malloc(sizeof(AVPacket));
    //6.一阵一阵读取压缩的视频数据AVPacket
    int video_frame_count = 0;
    while (av_read_frame(format_ctx, packet) >= 0) {
        if (packet->stream_index == player->video_stream_index) {
            //decode_video(player,packet);
            //LOGI("video_frame_count:%d",video_frame_count++);
        } else if (packet->stream_index == player->audio_stream_index) {
            decode_audio(player, packet);
        }
        av_free_packet(packet);
    }
}

void decode_audio_prepare(Player *player) {
    AVCodecContext *codec_ctx = player->input_codec_ctx[player->audio_stream_index];

    //重采样设置参数-------------start
    //输入的采样格式
    enum AVSampleFormat in_sample_fmt = codec_ctx->sample_fmt;
    //输出采样格式16bit PCM
    enum AVSampleFormat out_sample_fmt = AV_SAMPLE_FMT_S16;
    //输入采样率
    int in_sample_rate = codec_ctx->sample_rate;
    //输出采样率
    int out_sample_rate = in_sample_rate;
    //获取输入的声道布局
    //根据声道个数获取默认的声道布局（2个声道，默认立体声stereo）
    //av_get_default_channel_layout(codecCtx->channels);
    uint64_t in_ch_layout = codec_ctx->channel_layout;
    //输出的声道布局（立体声）
    uint64_t out_ch_layout = AV_CH_LAYOUT_STEREO;

    //frame->16bit 44100 PCM 统一音频采样格式与采样率
    SwrContext *swr_ctx = swr_alloc();
    swr_alloc_set_opts(swr_ctx,
                       out_ch_layout, out_sample_fmt, out_sample_rate,
                       in_ch_layout, in_sample_fmt, in_sample_rate,
                       0, NULL);
    swr_init(swr_ctx);

    //输出的声道个数
    int out_channel_nb = av_get_channel_layout_nb_channels(out_ch_layout);

    //重采样设置参数-------------end

    player->in_sample_fmt = in_sample_fmt;
    player->out_sample_fmt = out_sample_fmt;
    player->in_sample_rate = in_sample_rate;
    player->out_sample_rate = out_sample_rate;
    player->out_channel_nb = out_channel_nb;
    player->swr_ctx = swr_ctx;
} ;
JNIEXPORT void JNICALL
Java_com_maple_ffmpeg_VideoPlayer_play(JNIEnv *env, jclass type, jstring input_, jobject surface) {
    const char *input = env->GetStringUTFChars(input_, nullptr);

    auto *player = (Player *) malloc(sizeof(Player));
    env->GetJavaVM(&(player->javaVM));
    // 获取音视频索引
    init_input_format_ctx(player, input);
    // 获取各部分的解码器即上下文.
    init_codec_context(player, player->audio_stream_index);
    init_codec_context(player, player->video_stream_index);
    decode_video_prepare(player, env, surface);
    decode_audio_prepare(player);
    jni_audio_prepare(env, type, player);
    //子线程解码

    pthread_create(&(player->decode_threads[player->video_stream_index]), nullptr, decode_video,
                   player);
    pthread_create(&(player->decode_threads[player->audio_stream_index]), nullptr, decode_data,
                   player);
    pthread_join(player->decode_threads[player->video_stream_index], nullptr);
    int i;

    env->ReleaseStringUTFChars(input_, input);
}

}

