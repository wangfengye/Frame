package com.maple.demo;


import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

/**
 * @author: hexiao
 * @Date: 2019/8/7 19:35.
 * @Description: 录制视频
 */
public class RecordingVideoActivity extends AppCompatActivity {
    private static final int INTENT_RECORDING_CODE = 1;
    private static final String PARAM_VIDEO_NAME = "recording_name";

    @BindView(R.id.vcr_surface_camera)
    SurfaceView mSurfaceView;
    @BindView(R.id.video_time)
    Chronometer mVideoTime;
    @BindView(R.id.recording_video)
    Button mStartStopRecording;
    @BindView(R.id.pause_or_continue_video)
    Button mPauseContinueRecording;
    private boolean stopRecording = true;
    private boolean pauseRecording = true;
    private MediaRecorder mediarecorder;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private String fileUri;
    private long hadRecordTime = 0L;
    private String name;
    private boolean needUpload = false; // 是否需要上传，true：不需要上传，视频放到不需要上传的目录中，false：需要上传，放到需要上传的目录下


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void initData() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("videoname");
            needUpload = intent.getBooleanExtra("needUpload", false);
        }
        File movieDirectory = new File(Environment.getExternalStorageDirectory(), "hxzy/video/needUpload");
        if (needUpload) {
            movieDirectory = new File(Environment.getExternalStorageDirectory(), "NEED_UPLOAD_VIDEO");
        } else {
            movieDirectory = new File(Environment.getExternalStorageDirectory(), "NO_UPLOAD_VIDEO");
        }
        if (!movieDirectory.exists()) {
            movieDirectory.mkdirs();
        }
        fileUri = movieDirectory.getAbsolutePath() + File.separator + name;

        surfaceHolder = mSurfaceView.getHolder();
        // 设置Surface不需要维护自己的缓冲区
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置分辨率
        surfaceHolder.setFixedSize(320, 280);
        // 设置该组件不会让屏幕自动关闭
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(mCallBack); //相机创建回调接口
    }

    private SurfaceHolder.Callback mCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            initCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            if (surfaceHolder.getSurface() == null) {
                return;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            releaseCamera();
        }
    };



    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recording_video:
                startOrStopRecording();
                break;
            case R.id.pause_or_continue_video:
                pauseOrContinueRecording();
                break;
            default:
                break;
        }
    }

    private void startOrStopRecording() {
        if (stopRecording) {// 未开始录制，点击时开始录制
            stopRecording = false;
            pauseRecording = false;
            mVideoTime.setBase(SystemClock.elapsedRealtime());
            mVideoTime.start();
            mVideoTime.setVisibility(View.VISIBLE);
            mPauseContinueRecording.setVisibility(View.VISIBLE);
            startRecording();

        } else {// 结束录制返回上个页面
            String time = mVideoTime.getText().toString();
            int hadTime = Integer.parseInt(time.split(":")[1]);
            if (hadTime > 5) {
                stopRecording = true;
                mVideoTime.stop();
                stopRecording();
                if (camera!=null){
                    camera.lock();
                }
                releaseCamera();
                handler.sendEmptyMessageDelayed(0,2000);
                loadingDataDialog = LoadingDataDialog.getInstance(RecordingVideoActivity.this,"正在保存视频请稍后...");
                loadingDataDialog.showDialog();
            } else {
                ToastUtil.toastShort(this, "录制时间太短");
            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (loadingDataDialog!=null){
                        loadingDataDialog.cancelDialog();
                    }
                    Intent intent = new Intent();
                    intent.putExtra(PARAM_VIDEO_NAME, name);
                    setResult(INTENT_RECORDING_CODE, intent);
                    finish();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pauseOrContinueRecording() {
        if (pauseRecording) {// 正在暂停录制，点击继续录制
            pauseRecording = false;
            mPauseContinueRecording.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_recording_pause));
            pauseRecording();
            if (hadRecordTime != 0) {
                mVideoTime.setBase(mVideoTime.getBase() + (SystemClock.elapsedRealtime() - hadRecordTime));
            } else {
                mVideoTime.setBase(SystemClock.elapsedRealtime());
            }
            mVideoTime.start();
            ToastUtil.toastShort(this, "已继续录制");
        } else {// 正在录制，点击暂停录制
            pauseRecording = true;
            mPauseContinueRecording.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_recording_continue));
            continueRecording();
            mVideoTime.stop();
            // 记录已经录制的时间
            hadRecordTime = SystemClock.elapsedRealtime();
            ToastUtil.toastShort(this, "已暂停录制");
        }
    }


    private MediaRecorder.OnErrorListener onErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
            try {
                if (mediaRecorder != null) {
                    mediaRecorder.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 开始录制
     */
    private void startRecording() {
        //initCamera();
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象
        //mediarecorder.reset();
        mediarecorder.setOnErrorListener(onErrorListener);
        camera.unlock();
        mediarecorder.setCamera(camera);
        mediarecorder.setOrientationHint(90);

        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 设置录制视频源为Camera(相机)
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源

        mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 音频格式
        mediarecorder.setVideoSize(640, 480);// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错

        mediarecorder.setVideoFrameRate(30);//设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// 视频录制格式
        mediarecorder.setVideoEncodingBitRate(2 * 1024 * 1024);// 设置帧频率，影响清晰度与视频大小
        mediarecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mediarecorder.setOutputFile(fileUri);
        try {
            mediarecorder.prepare();// 准备录制
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediarecorder.start();// 开始录制
        mediarecorder.stop();
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        if (camera != null) {
            releaseCamera();
        }
        //默认启动后置摄像头
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (camera == null) {
            ToastUtil.toastShort(this, "未能获取到相机！");
            return;
        }
        try {
            camera.setPreviewDisplay(surfaceHolder);
            //配置CameraParams
            Camera.Parameters params = camera.getParameters();
            //设置相机的很速屏幕
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                params.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
            } else {
                params.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
            }
            // 设置聚焦模式
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            //缩短Recording启动时间
            params.setRecordingHint(true);
            //是否支持影像稳定能力，支持则开启
            if (params.isVideoStabilizationSupported()) {
                params.setVideoStabilization(true);
            }
            camera.setParameters(params);
            //启动相机预览
            camera.startPreview();
        } catch (IOException e) {
            LogUtil.info(e.getMessage());
        }

    }

    /**
     * 暂停
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pauseRecording() {
        if (mediarecorder != null && !pauseRecording) {
            mediarecorder.pause();
        }
    }

    /**
     * 继续
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void continueRecording() {
        if (mediarecorder != null && pauseRecording) {
            mediarecorder.resume();
        }
    }

    /**
     * 停止录制
     */
    private void stopRecording() {
        try {
            if (mediarecorder != null) {
                mediarecorder.setOnErrorListener(null);
                mediarecorder.setPreviewDisplay(null);
                mediarecorder.setPreviewDisplay(null);
                mediarecorder.stop();// 停止
                mediarecorder.reset();
                mediarecorder.release();
                mediarecorder = null;
            }
        } catch (Exception e) {

        }

    }

    /**
     * 停止相机
     */
    private void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (loadingDataDialog!=null){
            loadingDataDialog.cancelDialog();x
        }
        stopRecording();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!stopRecording) {
                VuUtil.alert(this, "提示", "正在录制视频，请先结束录制视频");
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

