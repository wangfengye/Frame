package com.maple.voicechange;

import android.content.Context;
import android.os.Environment;

/**
 * Created by maple on 2019/10/8 16:31
 */
public class EffectUtil {
    static {
        System.loadLibrary("fmod");
        System.loadLibrary("fmodL");
        System.loadLibrary("native-lib");
    }
    public static final int TYPE_NORMAL=0;//原声
    public static final int TYPE_LUOLI=1;
    public static final int TYPE_UNCLE=2;
    public static final int TYPE_JINGSONG=3;
    public static final int TYPE_GAOGUAI=4;
    public static final int TYPE_KONGLING=5;

    /**
     *  播放特效音乐
     * @param path 音乐路径
     * @param type 类型
     */
    public static native void fix(String path,int type);
}
