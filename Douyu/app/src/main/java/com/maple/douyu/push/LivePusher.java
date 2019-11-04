package com.maple.douyu.push;


import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;


/**
 * Created by maple on 2019/10/25 14:35
 * 预览
 */
public class LivePusher extends Pusher implements SurfaceHolder.Callback {
    public static final String TAG = "LivePusher";
    private SurfaceHolder surfaceHolder;
    VideoPusher videoPusher;
    AudioPusher audioPusher;
    public PushNative pushNative;


    public LivePusher(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        this.surfaceHolder.addCallback(this);
        prepare();
    }

    private void prepare() {
        // 实例化实现native推送的调用类
        pushNative = new PushNative();
        // 实例化音视频推流器
        VideoParam videoParam = new VideoParam(480, 320, Camera.CameraInfo.CAMERA_FACING_BACK);
        videoPusher = new VideoPusher(surfaceHolder, videoParam, pushNative);
        AudioParam audioParam = new AudioParam();
        audioPusher = new AudioPusher(audioParam, pushNative);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // startPush();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopPush();
        release();
    }

    public void switchCamera() {
        videoPusher.switchCamera();
    }


    private boolean pushing = false;

    @Override
    public void startPush() {
        if (pushing) {
            stopPush();
            pushing = false;
            release();
            return;
        }
        pushing = true;
        videoPusher.startPush();
        audioPusher.startPush();
        pushNative.startPush("rtmp://192.168.168.149:1935/live/maple");
    }


    @Override
    public void stopPush() {
        videoPusher.stopPush();
        audioPusher.stopPush();
        pushNative.stopPush();
    }

    @Override
    public void release() {
        videoPusher.release();
        audioPusher.release();
        pushNative.release();
    }
}

