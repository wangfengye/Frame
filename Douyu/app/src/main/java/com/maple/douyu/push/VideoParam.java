package com.maple.douyu.push;

/**
 * Created by maple on 2019/10/25 15:41
 */
public class VideoParam {
    private int width;
    private int heigh;
    private int cameraId;

    public VideoParam(int width, int heigh, int cameraId) {
        this.width = width;
        this.heigh = heigh;
        this.cameraId = cameraId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeigh() {
        return heigh;
    }

    public void setHeigh(int heigh) {
        this.heigh = heigh;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }
}
