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
#include <sys/prctl.h>

#include <queue>


using namespace std;
//todo; 音视频同步



extern "C" {
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "libswresample/swresample.h"
#include "libavutil/frame.h"
#include "libavutil/mem.h"
#include "libyuv.h"
#include "libavutil/time.h"
}

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"native",FORMAT,##__VA_ARGS__);
#define ARRAY_LEN(a) (sizeof(a) / sizeof(a[0]))
//预留视频,音频,字幕,三个流的上下文位置
constexpr int MAX_STREAM = 3;
constexpr int MAX_AUDIO_FRME_SIZE = 48000 * 4;
constexpr long SLEEPING = 1000 * 16;
constexpr long MIN_SLEEP_TIME_US = 1000ll;
constexpr long AUDIO_TIME_ADJUST_US=-200000ll;
extern "C" {
queue<AVPacket *> video_frames;
queue<AVPacket *> audio_frames;
pthread_mutex_t video_mutex;
bool NeedSleep = false;//解码一帧是是否延时


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
    int64_t start_time;
    //锁
    pthread_mutex_t mutex;
    //条件变量
    pthread_cond_t cond;
    int64_t audio_clock;
} Player;
/**
 * 获取视频当前播放时间
 */
int64_t player_get_current_video_time(Player *player) {
    int64_t current_time = av_gettime();
    return current_time - player->start_time;
}
/**
 * 延时操作.
 * @param player
 * @param stream_time
 * @param stream_no
 */
void player_wait_for_frame(Player *player, int64_t stream_time,
                           int stream_no) {
    pthread_mutex_lock(&player->mutex);
    for (;;) {
        int64_t current_video_time = player_get_current_video_time(player);
        int64_t sleep_time = stream_time - current_video_time;
        if (sleep_time < -300000ll) {
            int64_t new_value = player->start_time - sleep_time;
            player->start_time = new_value;
            pthread_cond_broadcast(&player->cond);
        }
        if (sleep_time <= MIN_SLEEP_TIME_US) {
            //We do not need to wait if time is slower then minimal sleep time
            break;
        }
        if (sleep_time > 500000ll) {
            // if sleep time is bigger then 500ms just sleep this 500ms
            // and check everything again
            sleep_time = 500000ll;
        }
        timespec to;
        clock_gettime(CLOCK_REALTIME, &to);
        to.tv_nsec = to.tv_nsec + (long) sleep_time;

        int timeout_ret = pthread_cond_timedwait(&player->cond, &player->mutex,
                                                 &to);
    }
    pthread_mutex_unlock(&player->mutex);
}
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
    LOGI("视频格式%s", codec->name);
    player->input_codec_ctx[stream_idx] = codec_ctx;
}
void decode_video_frame(Player *player, AVPacket *packet) {
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
        ANativeWindow_unlockAndPost(player->nativeWindow);
        //todo 计算延时
        int64_t pts = av_frame_get_best_effort_timestamp(yuv_frame);
        double clock = yuv_frame->best_effort_timestamp * av_q2d(player->input_format_ctx->streams[player->video_stream_index]->time_base);
        double audio_clock=player->audio_clock*av_q2d(player->input_format_ctx->streams[player->audio_stream_index]->time_base);
        double diff=clock-audio_clock;
        LOGI("DIFF %f",diff)
        if (diff>0){
            usleep(diff*1000000);
        } else {

        }
       // int64_t time = av_rescale_q(pts,
        //                            player->input_format_ctx->streams[player->video_stream_index]->time_base,
         //                           AV_TIME_BASE_Q);
        //player_wait_for_frame(player, time, player->video_stream_index);

    }
    av_frame_free(&yuv_frame);
    av_frame_free(&rgb_frame);
}
void *decode_video(void *arg) {
    prctl(PR_SET_NAME, "decode_video_thread");
    Player *player = (Player *) arg;
    AVFormatContext *format_ctx = player->input_format_ctx;
    AVPacket *packet = (AVPacket *) av_malloc(sizeof(AVPacket));
    while (av_read_frame(format_ctx, packet) >= 0) {
        if (packet->stream_index == player->video_stream_index) {
            decode_video_frame(player, packet);
        }
        av_free_packet(packet);
    }
    return nullptr;
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
void decode_audio_frame(Player *player, AVPacket *packet) {
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
        int64_t pts = packet->pts;
        if (pts != AV_NOPTS_VALUE) {
            player->audio_clock = av_rescale_q(pts,
                    player->input_format_ctx->streams[player->video_stream_index]->time_base, AV_TIME_BASE_Q);
            player->audio_clock= frame->pts;
            LOGI("player_write_audio - read from pts");
           // player_wait_for_frame(player,
             //                     player->audio_clock + AUDIO_TIME_ADJUST_US, player->audio_stream_index);
            usleep(SLEEPING);
        }

        //关联当前线程的JNIEnv
        JavaVM *javaVM = player->javaVM;
        JNIEnv *env;
        javaVM->AttachCurrentThread(&env, nullptr);

        //out_buffer缓冲区数据，转成byte数组
        jbyteArray audio_sample_array = env->NewByteArray(out_buffer_size);
        jbyte *sample_bytep = env->GetByteArrayElements(audio_sample_array, nullptr);
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

    }

    av_frame_free(&frame);
}


/**
 * 解码子线程函数
 */
void *decode_audio(void *arg) {
    prctl(PR_SET_NAME, "decode_audio_thread");
    auto *player = (Player *) arg;
    AVFormatContext *format_ctx = player->input_format_ctx;
    //编码数据
    auto *packet = (AVPacket *) av_malloc(sizeof(AVPacket));
    //6.逐帧读取压缩的视频数据AVPacket

    while (av_read_frame(format_ctx, packet) >= 0) {
        if (packet->stream_index == player->audio_stream_index) {
            decode_audio_frame(player, packet);

        }
        //    av_free_packet(packet);
    }
    return nullptr;
}
void *decode_data(void *arg) {
    prctl(PR_SET_NAME, "decode_data");
    auto *player = (Player *) arg;
    for (;;) {
        auto *packet = (AVPacket *) av_malloc(sizeof(AVPacket));
        if (av_read_frame(player->input_format_ctx, packet) < 0) {
            av_packet_free(&packet);
            break;
        }
        if (packet->stream_index == player->audio_stream_index) {
            // decode_audio(player, packet);

            audio_frames.push(packet);
            LOGI("videoFrameCache: %d", audio_frames.size());

        } else if (packet->stream_index == player->video_stream_index) {

            video_frames.push(packet);
            LOGI("videoFrameCache: %d", video_frames.size());

        }

        if (video_frames.size() > 500) {
            usleep(100);
        }
    }
    return nullptr;
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
                       0, nullptr);
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
void *sync_play_video(void *arg) {
    prctl(PR_SET_NAME, "sync_video");
    auto *player = (Player *) arg;
    usleep(SLEEPING);
    int retry = 3;
    for (;;) {

        if (video_frames.empty()) {
            LOGI("VIDEO播放终止: %d,%d", video_frames.size(), audio_frames.size());
            if (retry > 0) {
                retry--;

                continue;
            } else {
                break;
            }
        }
        retry = 3;
        AVPacket *video_packet = video_frames.front();
        video_frames.pop();
        decode_video_frame(player, video_packet);
        av_packet_free(&video_packet);

    }
    return nullptr;
}
void *sync_play_audio(void *arg) {
    prctl(PR_SET_NAME, "sync_audio");
    auto *player = (Player *) arg;
    usleep(SLEEPING);
    int retry = 3;
    for (;;) {

        if (audio_frames.empty()) {
            LOGI("AUDIO播放终止: %d,%d", video_frames.size(), audio_frames.size());
            if (retry > 0) {
                retry--;

                continue;
            } else {
                break;
            }
        }
        retry = 3;
        AVPacket *audio_packet = audio_frames.front();
        audio_frames.pop();
        decode_audio_frame(player, audio_packet);
        av_packet_free(&audio_packet);
    }
    return nullptr;
}
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
    //两个线程分别解码

    pthread_mutex_init(&player->mutex,NULL);
    pthread_cond_init(&player->cond,NULL);
    // 同步
    pthread_mutex_init(&video_mutex, nullptr);
    pthread_t decodetid;

    pthread_create(&decodetid, nullptr, decode_data, player);
    pthread_create(&(player->decode_threads[player->video_stream_index]), nullptr, sync_play_video,
                   player);
    pthread_create(&(player->decode_threads[player->audio_stream_index]), nullptr, sync_play_audio,
                   player);


    player->start_time = 0;
    pthread_join(decodetid, nullptr);
    pthread_join(player->decode_threads[player->video_stream_index], nullptr);
    pthread_join(player->decode_threads[player->audio_stream_index], nullptr);
    pthread_mutex_destroy(&video_mutex);
    env->ReleaseStringUTFChars(input_, input);
}

}

