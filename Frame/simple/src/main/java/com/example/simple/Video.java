package com.example.simple;


import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * 摄像头调用.
 */
public class Video extends AppCompatActivity implements SurfaceHolder.Callback {
    public static final String TAG = "Video";
    Camera mCamera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    byte[] buffers;
    HandlerThread mHandlerThread = new HandlerThread("Video");
    Handler handler ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        surfaceView = findViewById(R.id.sf);
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHandlerThread.start();
        handler = new Handler(mHandlerThread.getLooper());
        handler.post(new Task());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

     class Task implements Runnable, android.hardware.Camera.PreviewCallback{

        @Override
        public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
            // 该方法由camera通过handler回调,若开启线程无Looper,使用主线程Looper.

            if(mCamera != null){
                mCamera.addCallbackBuffer(buffers);
                //编码
                Log.d(TAG, Thread.currentThread().getName()+ "_onPreviewFrame");
            }
        }

        @Override
        public void run() {
            //打开相机
            //子线程中打开
            Log.d("jason", Thread.currentThread().getName() + "_open");
            mCamera = Camera.open(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setRotation(180);
            //设置相机参数
            parameters.setPreviewSize(480, 320); //预览画面宽高
            mCamera.setParameters(parameters);
            //获取预览图像数据
            buffers = new byte[480 * 320 * 4];
            mCamera.addCallbackBuffer(buffers);
            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.startPreview();

            Log.d(TAG, Thread.currentThread().getName()+ "_run");
        }
    }
}
