package com.maple.douyu.push;

/**
 * Created by maple on 2019/10/25 15:40
 */
public class AudioParam {
    private int sampleRateInHz = 44100;
    private int channel = 1;

    public AudioParam() {
    }

    public AudioParam(int sampleRateInHz, int channel) {
        this.sampleRateInHz = sampleRateInHz;
        this.channel = channel;
    }

    public int getSampleRateInHz() {
        return sampleRateInHz;
    }

    public void setSampleRateInHz(int sampleRateInHz) {
        this.sampleRateInHz = sampleRateInHz;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
