package com.maple.ffmpeg;

import android.view.Surface;

/**
 * Created by maple on 2019/10/11 15:15
 */
public class VideoPlayer {
    static{
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
}
