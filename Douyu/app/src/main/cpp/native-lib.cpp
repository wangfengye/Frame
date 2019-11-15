#include <jni.h>
#include <string>
#include <x264.h>
#include <android/log.h>
#include <pthread.h>
#include <queue>
#include <list>
#include <unistd.h>

using namespace std;

#include "x264.h"
#include "rtmp.h"
#include "faac.h"

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native-douyu",FORMAT,##__VA_ARGS__)
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"native-douyu",FORMAT,##__VA_ARGS__)
// x264編碼輸入圖像yuv420p
x264_picture_t pic_in;
x264_picture_t pic_out;
//yuv個數
int y_len, u_len, v_len;
//x264编码处理器
x264_t *video_encode_handle;
faacEncHandle audio_encode_handle;
unsigned int start_time;
pthread_mutex_t mutex;
pthread_cond_t cond;
//rtmp流媒體地址
char *rtmp_path;
list<RTMPPacket *> q;

int is_pushing = false;
//audio 配置.
unsigned long inputSamplesl;//输入的采样个数
unsigned long maxOutputBytes;//编码输出之后的字节数
void add_rtmp_packet(RTMPPacket *packet) {
    pthread_mutex_lock(&mutex);
    if (is_pushing) {
        q.push_back(packet);
    }

    pthread_cond_signal(&cond);
    pthread_mutex_unlock(&mutex);
}

void add_aac_sequence_header() {
    unsigned char *buf;
    unsigned long len;
    faacEncGetDecoderSpecificInfo(audio_encode_handle, &buf, &len);
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, len + 2);
    RTMPPacket_Reset(packet);
    unsigned char *body = reinterpret_cast<unsigned char *>(packet->m_body);
    //头信息配置
    /*AF 00 + AAC RAW data*/
    body[0] = 0xAF;//10 5 SoundFormat(4bits):10=AAC,SoundRate(2bits):3=44kHz,SoundSize(1bit):1=16-bit samples,SoundType(1bit):1=Stereo sound
    body[1] = 0x00;//AACPacketType:0表示AAC sequence header
    memcpy(&body[2], body, len);
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = len + 2;
    packet->m_nChannel = 0x04;
    packet->m_hasAbsTimestamp = 0;
    packet->m_nTimeStamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    add_rtmp_packet(packet);
    free(buf);
}
jstring getUTF16(JNIEnv* env, const char* c_str) {
    jclass cls_string = env->FindClass( "java/lang/String");
    jmethodID mid_constructor = env->GetMethodID( cls_string, "<init>",
                                                  "([BLjava/lang/String;)V");
    jbyteArray bytes = env->NewByteArray( strlen(c_str));
    env->SetByteArrayRegion(bytes, 0, strlen(c_str), reinterpret_cast<const jbyte *>(c_str));

    jstring jstr_charset = env->NewStringUTF( "UTF-8");
    // jstring jstr_charset = env->NewStringUTF( "GB2312");// java层数据修改返回.
    return static_cast<jstring>(env->NewObject(cls_string, mid_constructor,
                                               bytes, jstr_charset));
}
jobject o;
JavaVM *vm ;
jmethodID mId;
void sendMessage(const char *message) {
    JNIEnv* env ;
    if(vm== nullptr){
        LOGE("资源已释放,无法执行sendMessage");
        return;
    }
    vm->AttachCurrentThread(&env,nullptr);
    jstring  jstring1= getUTF16(env,message);
    env->CallVoidMethod(o,mId,jstring1);
    vm->DetachCurrentThread();
}
void *push_thread(void *arg) {
    sleep(1);
    RTMP *rtmp = RTMP_Alloc();
    if (!rtmp) {
        LOGE("RTMP初始化失败");
        goto end;
    }
    RTMP_Init(rtmp);
    rtmp->Link.timeout = 5;//连接超时
    RTMP_SetupURL(rtmp, rtmp_path);
    RTMP_EnableWrite(rtmp);
    if (!RTMP_Connect(rtmp, nullptr)) {
        LOGE("连接失败");
        goto end;
    }
    //计时
    start_time = RTMP_GetTime();
    if (!RTMP_ConnectStream(rtmp, 0)) {
        LOGE("Stream 连接失败");
        goto end;
    }
    is_pushing = true;
    add_aac_sequence_header();
    sendMessage("推送通道连接成功");
    while (is_pushing) {
        pthread_mutex_lock(&mutex);
        RTMPPacket *packet = q.front();
        if (packet) {
            q.pop_front();
            //RTMP协议,stream_id数据
            packet->m_nInfoField2 = rtmp->m_stream_id;
            int i = RTMP_SendPacket(rtmp, packet, TRUE);
            if (!i) {
                LOGE("RTMP 异常断开");
                RTMPPacket_Free(packet);
                pthread_mutex_unlock(&mutex);
                goto end;
            }
            LOGI("推送一组数据");
        } else {
            pthread_cond_wait(&cond, &mutex);
        }
        pthread_mutex_unlock(&mutex);
    }
    end:
    LOGI("释放资源");
    free(rtmp_path);
    RTMP_Close(rtmp);
    RTMP_Free(rtmp);
    return 0;
}



extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_startPush(JNIEnv *env, jobject instance, jstring url_jstr) {

    const char *url = env->GetStringUTFChars(url_jstr, nullptr);


    env->GetJavaVM(&vm);
    o=env->NewGlobalRef(instance);
    jclass clazz = env->GetObjectClass(instance);
    mId = env->GetMethodID(clazz, "listenFromNative", "(Ljava/lang/String;)V");

    LOGI("push start: %s", url);
    //设置推送地址
    rtmp_path = static_cast<char *>(malloc(strlen(url) + 1));
    memset(rtmp_path, 0, strlen(url) + 1);
    memcpy(rtmp_path, url, strlen(url));
    //初始化锁
    pthread_mutex_init(&mutex, nullptr);
    pthread_cond_init(&cond, nullptr);
    pthread_t push_thread_id;
    pthread_create(&push_thread_id, nullptr, push_thread, nullptr);
    env->ReleaseStringUTFChars(url_jstr, url);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_stopPush(JNIEnv *env, jobject instance) {
    is_pushing = false;

}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_release(JNIEnv *env, jobject instance) {
    if (o!= nullptr){
        env->DeleteGlobalRef(o);
    }
    mId= nullptr;
    vm= nullptr;
    //vm= nullptr;
    //env->DeleteGlobalRef((jobject)vm);


}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_setVideoOptions(JNIEnv *env, jobject instance, jint width,
                                                     jint height, jint bitrate, jint fps) {
    LOGI("setVideoOptions start");
    // 设置视频参数
    //X264_API int x264_param_default_preset( x264_param_t *, const char *preset, const char *tune );
    x264_param_t param;
    // "ultrafast","zerolatency":0延时
    x264_param_default_preset(&param, x264_preset_names[0], x264_tune_names[7]);
    param.i_csp = X264_CSP_I420;
    param.i_width = width;
    param.i_height = height;
    y_len = width * height;
    u_len = y_len / 4;
    v_len = u_len;
    //码率控制 CQP(恒定质量)，CRF(恒定码率)，ABR(平均码率)
    param.rc.i_rc_method = X264_RC_CRF;
    //码率(比特率,单位kbps)
    param.rc.i_bitrate = bitrate / 1000;
    //瞬时最大码率
    param.rc.i_vbv_max_bitrate = bitrate / 1000 * 1.2;
    //码率控制不通过timebase和timestamp
    param.b_vfr_input = 0;
    //帧率分子
    param.i_fps_num = fps;
    //帧率分母
    param.i_fps_den = 1;
    param.i_timebase_den = param.i_fps_num;
    param.i_timebase_num = param.i_fps_den;
    //控制并行编码的线程数量.
    param.i_threads = 1;
    //是否把sps和pps放入每个关键帧.放入后能提高图像的纠错能力
    //sps:Sequenece Parameter Set 序列参数集
    //pps: Picture Paramter Set 图像参数集
    param.b_repeat_headers = 1;
    param.i_level_idc = 51; // H264的Level
    //基础级别,无B帧.
    x264_param_apply_profile(&param, "baseline");
    //x264_picture_t 初始化

    x264_picture_alloc(&pic_in, param.i_csp, param.i_width, param.i_height);
    pic_in.i_pts = 0;
    video_encode_handle = x264_encoder_open(&param);
    if (video_encode_handle) {
        LOGI("打开编码器成功...");
    } else {
        LOGI("打开编码器异常...");
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_setAudioOptions(JNIEnv *env, jobject instance,
                                                     jint sampleRateInHz, jint channel) {

    audio_encode_handle = faacEncOpen(sampleRateInHz, channel, &inputSamplesl, &maxOutputBytes);
    if (!audio_encode_handle) {
        LOGE("音频编码器创建异常");
        return;
    }
    //设置音频编码参数
    faacEncConfigurationPtr config = faacEncGetCurrentConfiguration(audio_encode_handle);
    config->mpegVersion = MPEG4;
    config->allowMidside = 1;
    config->aacObjectType = LOW;
    config->outputFormat = 0;
    config->useTns = 1;
    config->useLfe = 0;
    config->quantqual = 100;
    config->bandWidth = 0;
    config->shortctl = SHORTCTL_NORMAL;
    if (!faacEncSetConfiguration(audio_encode_handle, config)) {
        LOGE("音频编码器配置失败");
        return;
    }
    LOGI("音频编码器配置成功");
}


void add_264_sequence_header(unsigned char *pps, unsigned char *sps, int pps_len, int sps_len) {
    int body_size = 16 + sps_len + pps_len; //按照H264标准配置SPS和PPS，共使用了16字节
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    //RTMPPacket初始化
    RTMPPacket_Alloc(packet, body_size);
    RTMPPacket_Reset(packet);
    unsigned char *body = (unsigned char *) (packet->m_body);
    int i = 0;
    //二进制表示：00010111
    body[i++] = 0x17;//VideoHeaderTag:FrameType(1=key frame)+CodecID(7=AVC)
    body[i++] = 0x00;//AVCPacketType = 0表示设置AVCDecoderConfigurationRecord
    //composition time 0x000000 24bit ?
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    /*AVCDecoderConfigurationRecord*/
    body[i++] = 0x01;//configurationVersion，版本为1
    body[i++] = sps[1];//AVCProfileIndication
    body[i++] = sps[2];//profile_compatibility
    body[i++] = sps[3];//AVCLevelIndication
    body[i++] = 0xFF;//lengthSizeMinusOne,H264 视频中 NALU的长度，计算方法是 1 + (lengthSizeMinusOne & 3),实际测试时发现总

    /*sps*/
    body[i++] = 0xE1;//numOfSequenceParameterSets:SPS的个数，计算方法是 numOfSequenceParameterSets & 0x1F,实际测试时发现总为E1，计算结果为1.
    body[i++] = (sps_len >> 8) & 0xff;//sequenceParameterSetLength:SPS的长度
    body[i++] = sps_len & 0xff;//sequenceParameterSetNALUnits
    memcpy(&body[i], sps, sps_len);
    i += sps_len;
    /*pps*/
    body[i++] = 0x01;//numOfPictureParameterSets:PPS 的个数,计算方法是 numOfPictureParameterSets & 0x1F,实际测试时发现总为E1，计算结果为1.
    body[i++] = (pps_len >> 8) & 0xff;//pictureParameterSetLength:PPS的长度
    body[i++] = (pps_len) & 0xff;//PPS
    memcpy(&body[i], pps, pps_len);
    i += pps_len;

    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    //Payload Length
    packet->m_nBodySize = body_size;
    //Time Stamp：4字节
    //记录了每一个tag相对于第一个tag（File Header）的相对时间。
    //以毫秒为单位。而File Header的time stamp永远为0。
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_nChannel = 0x04; //Channel ID，Audio和Vidio通道
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM; //?
    //将RTMPPacket加入队列
    add_rtmp_packet(packet);
}

void add_264_body(unsigned char *buf, int len) {
    //去掉起始码
    if (buf[2] == 0x00) {
        buf += 4;
        len -= 4;
    } else if (buf[2] == 0x01) {
        buf += 3;
        len -= 3;
    }
    int body_size = len + 9;
    RTMPPacket *packet = (RTMPPacket *) (malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, body_size);
    unsigned char *body = (unsigned char *) (packet->m_body);
    //当NAL头信息中，type（5位）等于5，说明这是关键帧NAL单元
    //buf[0] NAL Header与运算，获取type，根据type判断关键帧和普通帧
    //00000101 & 00011111(0x1f) = 00000101
    int type = buf[0] & 0x1f;
    //Inter Frame 帧间压缩
    //VideoHeaderTag:FrameType(2=Inter Frame)+CodecID(7=AVC)
    body[0] = 0x27;
    //IDR I帧图像
    if (type == NAL_SLICE_IDR) {
        //VideoHeaderTag:FrameType(1=key frame)+CodecID(7=AVC)
        body[0] = 0x17;
    }
    //AVCPacketType = 1
    body[1] = 0x01; /*nal unit,NALUs（AVCPacketType == 1)*/
    body[2] = 0x00; //composition time 0x000000 24bit
    body[3] = 0x00;
    body[4] = 0x00;

    //写入NALU信息，右移8位，一个字节的读取
    body[5] = (len >> 24) & 0xff;
    body[6] = (len >> 16) & 0xff;
    body[7] = (len >> 8) & 0xff;
    body[8] = (len) & 0xff;

    /*copy data*/
    memcpy(&body[9], buf, len);

    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = body_size;
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;//当前packet的类型：Video
    packet->m_nChannel = 0x04;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
//	packet->m_nTimeStamp = -1;
    packet->m_nTimeStamp = RTMP_GetTime() - start_time;//记录了每一个tag相对于第一个tag（File Header）的相对时间
    add_rtmp_packet(packet);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_fireVideo(JNIEnv *env, jobject instance, jbyteArray data_) {
    LOGI("fire video");
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    jbyte *u = (jbyte *) (pic_in.img.plane[1]);
    jbyte *v = (jbyte *) (pic_in.img.plane[2]);
    //nv21 4:2:0 Formats, 12 Bits per Pixel
    //nv21与yuv420p，y个数一致，uv位置对调
    //nv21转yuv420p  y = w*h,u/v=w*h/4
    //nv21 = yvu yuv420p=yuv y=y u=y+1+1 v=y+1
    memcpy(pic_in.img.plane[0], data, y_len);//y一致,直接赋值
    int i = 0;
    for (i = 0; i < u_len; i++) {
        *(u + i) = *(data + y_len + i * 2 + 1);//给数组u赋值.
        *(v + i) = *(data + y_len + i * 2);
    }
    x264_nal_t *nal = nullptr;
    int n_nal = -1;
    if (x264_encoder_encode(video_encode_handle, &nal, &n_nal, &pic_in, &pic_out) < 0) {
        LOGE("编码失败");
        return;
    }
    //使用rtmp协议将h264编码的视频数据发送给流媒体服务器
    //帧分为关键帧和普通帧，为了提高画面的纠错率，关键帧应包含SPS和PPS数据
    int sps_len, pps_len;
    unsigned char sps[100];
    unsigned char pps[100];
    memset(sps, 0, 100);
    memset(pps, 0, 100);
    pic_in.i_pts += 1;
    //遍历nalu,
    for (i = 0; i < n_nal; i++) {
        if (nal[i].i_type == NAL_SPS) {
            sps_len = nal[i].i_payload - 4;
            memcpy(sps, nal[i].p_payload + 4, sps_len);//从第五个字节开始复制
        } else if (nal[i].i_type == NAL_PPS) {
            pps_len = nal[i].i_payload - 4;
            memcpy(pps, nal[i].p_payload + 4, pps_len);
            //发送序列信息
            add_264_sequence_header(pps, sps, pps_len, sps_len);
        } else {
            //发送帧信息
            add_264_body(nal[i].p_payload, nal[i].i_payload);
        }
    }
    env->ReleaseByteArrayElements(data_, data, 0);
}


void add_aac_body(unsigned char *buf, int len) {
    int body_size = 2 + len;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, body_size);
    RTMPPacket_Reset(packet);
    unsigned char *body = reinterpret_cast<unsigned char *>(packet->m_body);
    //头信息配置
    //AF 00 +AAC RAW data
    body[0] = 0xAF;
    //AACPacketType 1表示aac,raw数据
    body[1] = 0x01;
    memcpy(&body[2], buf, len);
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = body_size;
    packet->m_nChannel = 0x04;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nTimeStamp = RTMP_GetTime() - start_time;
    add_rtmp_packet(packet);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_maple_douyu_push_PushNative_fireAudio(JNIEnv *env, jobject instance, jbyteArray data_,
                                               jint len) {
    jbyte *buffer = env->GetByteArrayElements(data_, NULL);
    int *pcmbuf;
    unsigned char *bitbuf;

    short *pcmbuft = (short *) malloc(inputSamplesl * sizeof(int));
    pcmbuf = (int *) (pcmbuft);
    bitbuf = (unsigned char *) malloc(maxOutputBytes * sizeof(unsigned char));
    int nByteCount = 0;
    unsigned int nBufferSize = (unsigned int) len / 2;
    unsigned short *buf = (unsigned short *) buffer;
    while (nByteCount < nBufferSize) {
        int audioLength = inputSamplesl;
        if ((nByteCount + inputSamplesl) >= nBufferSize) {
            audioLength = nBufferSize - nByteCount;
        }
        int i;
        for (i = 0; i < audioLength; i++) {//每次从实时的pcm音频队列中读出量化位数为8的pcm数据。
            int s = ((int16_t *) buf + nByteCount)[i];
            pcmbuf[i] = s << 8;//用8个二进制位来表示一个采样量化点（模数转换）
        }
        nByteCount += inputSamplesl;
        //利用FAAC进行编码，pcmbuf为转换后的pcm流数据，audioLength为调用faacEncOpen时得到的输入采样数，bitbuf为编码后的数据buff，nMaxOutputBytes为调用faacEncOpen时得到的最大输出字节数
        int byteslen = faacEncEncode(audio_encode_handle, pcmbuf, audioLength,
                                     bitbuf, maxOutputBytes);
        if (byteslen < 1) {
            continue;
        }
        add_aac_body(bitbuf, byteslen);//从bitbuf中得到编码后的aac数据流，放到数据队列
    }
    env->ReleaseByteArrayElements(data_, buffer, 0);
    if (bitbuf)
        free(bitbuf);
    if (pcmbuf)
        free(pcmbuf);


}