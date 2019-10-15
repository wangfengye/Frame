package com.maple.ffmpeg;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.Surface;

import java.nio.Buffer;

/**
 * Created by maple on 2019/10/11 15:15
 */
public class VideoPlayer {
    static {
        System.loadLibrary("avutil");
        System.loadLibrary("swresample");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        //  System.loadLibrary("postproc");
        System.loadLibrary("avfilter");
        System.loadLibrary("avdevice");
        System.loadLibrary("yuv");
        System.loadLibrary("native-lib");
    }

    public static native void render(String input, Surface surface);

    public static native void sound(String input, String output);

    public static AudioTrack createAudioTrack() {
        // 写死的配置,可能导致部分配置不同的音频播放错乱
        int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSizeInBytes = AudioTrack.getMinBufferSize(44100, channelConfig, audioFormat);
        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                44100, channelConfig,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSizeInBytes, AudioTrack.MODE_STREAM);
        return audioTrack;
    }

}
