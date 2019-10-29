package com.maple.douyu.push;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by maple on 2019/10/25 14:35
 */
public class AudioPusher extends Pusher {
    private AudioParam audioParam;
    private AudioRecord audioRecord;
    private boolean isPushing = false;
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
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                audioParam.getSampleRateInHz(),
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
        new Thread(new AudioRecordTask()).start();
    }

    @Override
    public void stopPush() {
        isPushing = false;
        if (audioRecord!=null)audioRecord.stop();
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
                if (len > 0) {
                    pushNative.fireAudio(buffer, len);
                }
            }
        }
    }
}
