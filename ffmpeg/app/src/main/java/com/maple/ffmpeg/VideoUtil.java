package com.maple.ffmpeg;

/**
 * Created by maple on 2019/10/11 11:05
 * ffmpeg 调用
 */
public class VideoUtil {
    static{
        System.loadLibrary("avutil");
        System.loadLibrary("swresample");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
      //  System.loadLibrary("postproc");
        System.loadLibrary("avfilter");
        System.loadLibrary("avdevice");
        System.loadLibrary("native-lib");
    }
    public static native void decode(String in,String out);
}
