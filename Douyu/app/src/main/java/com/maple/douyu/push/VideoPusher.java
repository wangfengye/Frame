package com.maple.douyu.push;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by maple on 2019/10/25 14:35
 */
public class VideoPusher extends Pusher implements Camera.PreviewCallback {
    public static final String TAG = VideoPusher.class.getSimpleName();
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private VideoParam videoParams;
    private byte[] buffers;
    private boolean isPushing = false;
    private PushNative pushNative;

    public VideoPusher(SurfaceHolder surfaceHolder, VideoParam videoParams, PushNative pushNative) {
        this.surfaceHolder = surfaceHolder;
        this.videoParams = videoParams;
        this.pushNative = pushNative;
    }

    @Override
    public void startPush() {
        isPushing = true;
        startPreview();
    }

    @Override
    public void stopPush() {
        isPushing = false;

    }

    @Override
    public void release() {
        stopPreview();
        mCamera.release();
    }


    public void switchCamera() {
        if (videoParams.getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK) {
            videoParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            videoParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        stopPreview();
        startPreview();
    }

    private boolean startPreview() {

        mCamera = Camera.open(videoParams.getCameraId());
        try {
            int result;
            if (videoParams.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                //前置摄像头作镜像翻转
                result = 90;

            } else {  // back-facing
                result = 90;
            }
            mCamera.setDisplayOrientation(result);
            mCamera.setPreviewDisplay(surfaceHolder);
            byte[] buffer = new byte[1024 * 500];
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(this);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        mCamera.startPreview();

        return true;
    }

    private boolean stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
        }
        return false;
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        mCamera.addCallbackBuffer(bytes);
    }
}
