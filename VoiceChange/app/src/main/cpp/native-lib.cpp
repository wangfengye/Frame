#include <jni.h>
#include <android/log.h>
#include <string>
#include <unistd.h>
#include "inc/fmod.hpp"
#include "common.h"

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native-lib",FORMAT,##__VA_ARGS__);
extern "C" JNIEXPORT jstring JNICALL
Java_com_maple_voicechange_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
#define MODE_NORMAL 0
#define MODE_LUOLI 1
#define  MODE_UNCLE 2
#define MODE_JINGSONG 3
#define MODE_GAOGUAI 4
#define MODE_KONGLING 5
const long split = 500 * 1000;
const long maxchannels = 32;

using namespace FMOD;
bool looping=false;
extern "C"
JNIEXPORT void JNICALL
Java_com_maple_voicechange_EffectUtil_fix(JNIEnv *env, jclass type_, jstring path_, jint type) {
    if(looping){
        looping=false;
        return;
    } else{
        looping=true;
    }
    const char *path = env->GetStringUTFChars(path_, nullptr);
    System *system;
    Sound *sound;
    Channel *channel = nullptr;
    FMOD_RESULT result;
    DSP *dsp;
    try {
        result = System_Create(&system);
        ERRCHECK(result);
        result = system->init(maxchannels, FMOD_INIT_NORMAL, nullptr);
        ERRCHECK(result);
        LOGI("%s", path);
        result = system->createSound(path, FMOD_DEFAULT, nullptr, &sound);
        ERRCHECK(result);
        result = system->playSound(sound, nullptr, false, &channel);
        ERRCHECK(result);
        LOGI("%s", "NORMAL");
        switch (type) {
            case MODE_NORMAL://原生,不加音效
                break;
            case MODE_LUOLI:
                // dsp提升或降低音调的一种音效
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
                //设置音调参数
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, 2.5F);
                //添加音效
                channel->addDSP(0, dsp);
                break;
            case MODE_UNCLE:
                system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT, &dsp);
                dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH, .8F);
                channel->addDSP(0, dsp);
                break;
            case MODE_GAOGUAI:
                float f;
                // 语速
                channel->getFrequency(&f);
                f = f * 1.8F;
                channel->setFrequency(f);
                break;
            case MODE_KONGLING:
                //回声,差时播放
                system->createDSPByType(FMOD_DSP_TYPE_ECHO, &dsp);
                dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY, 618);
                dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK, 20);
                channel->addDSP(0, dsp);
                break;
            case MODE_JINGSONG:
                //颤抖
                system->createDSPByType(FMOD_DSP_TYPE_TREMOLO, &dsp);
                dsp->setParameterFloat(FMOD_DSP_TREMOLO_SKEW, 0.8F);
                channel->addDSP(0, dsp);
            default:
                break;
        }
        result = system->update();
        ERRCHECK(result);
        bool palying = true;

        while (palying&&looping) {
            result = channel->isPlaying(&palying);
            ERRCHECK(result);
            usleep(split);//单位是微秒
        }
    } catch (...) {//...表示全部异常
        LOGI("%s", "出现异常")
    }
    sound->release();
    system->close();
    system->release();

    env->ReleaseStringUTFChars(path_, path);
    LOGI("%s", "finished")
}
