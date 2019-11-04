package com.maple.douyu.push;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import javax.security.auth.login.LoginException;

/**
 * Created by maple on 2019/10/25 16:01
 */
public class PushNative {
    static {
        System.loadLibrary("live");
    }

    public native void startPush(String url);

    public native void stopPush();

    public native void release();

    /**
     * 视频参数配置
     *
     * @param width  播放宽
     * @param height 高
     * @param birate
     * @param fps
     */
    public native void setVideoOptions(int width, int height, int birate, int fps);

    /**
     * 设置音频参数
     *
     * @param sampleRateInHz
     * @param channel
     */
    public native void setAudioOptions(int sampleRateInHz, int channel);


    /**
     * 发送视频数据
     *
     * @param data
     */
    public native void fireVideo(byte[] data);

    /**
     * 发送音频数据
     *
     * @param data
     * @param len
     */
    public native void fireAudio(byte[] data, int len);

    static {
        System.loadLibrary("live");
    }

    Activity context;

    public void setContext(Activity context) {
        this.context = context;
    }

    /**
     * 接收c层发送的消息
     *
     * @param message C层消息
     */
    public void listenFromNative(final String message) {
        Log.i("listenFromNative: ", message);
        if (context != null) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
