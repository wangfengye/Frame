package com.maple.douyu.push;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by maple on 2019/10/25 14:35
 */
public class AudioPusher extends Pusher {
    public static final String TAG = "AudioPusher";
    private AudioParam audioParam;
    private AudioRecord audioRecord;
    private volatile  boolean  isPushing = false;
    private int minBufferSize;
    private PushNative pushNative;

    public AudioPusher(AudioParam audioParam, PushNative pushNative) {
        this.audioParam = audioParam;
        this.pushNative = pushNative;
    }

    @Override
    public void startPush() {
        isPushing = true;
        int channelConfig = audioParam.getChannel() == 1 ?
                AudioFormat.CHANNEL_IN_MONO :
                AudioFormat.CHANNEL_IN_STEREO;
        minBufferSize = AudioRecord.getMinBufferSize(audioParam.getSampleRateInHz(),
                channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        Log.i(TAG, "startPush: "+minBufferSize);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                audioParam.getSampleRateInHz(),
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
        pushNative.setAudioOptions(audioParam.getSampleRateInHz(),audioParam.getChannel());
        new Thread(new AudioRecordTask()).start();
    }

    @Override
    public void stopPush() {
        isPushing = false;

    }

    @Override
    public void release() {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    private class AudioRecordTask implements Runnable {

        @Override
        public void run() {
            audioRecord.startRecording();
            while (isPushing) {
                byte[] buffer = new byte[minBufferSize];
                int len = audioRecord.read(buffer, 0, buffer.length);
                if (len !=AudioRecord.ERROR_INVALID_OPERATION) {
                    pushNative.fireAudio(buffer, len);
                }
            }
            if (audioRecord!=null)audioRecord.stop();
        }
    }
}
