package com.maple.ffmpeg;

import java.util.UUID;

/**
 * Created by maple on 2019/10/18 11:32
 */
public class NativeThreadUtil {
    static {
        System.loadLibrary("native-lib");
    }
    public native void thread();

}
